package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.dto.DetalleVentaDTO;
import org.example.desktop.model.*;
import org.example.desktop.util.VariablesEntorno;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import java.time.format.DateTimeFormatter;

public class HistorialVentasController implements Initializable {

    // Controles de búsqueda y filtros
    @FXML
    private TextField txtBuscarVenta;
    @FXML
    private ComboBox<String> filtroEstado;
    @FXML
    private ComboBox<String> filtroMedioPago;
    @FXML
    private DatePicker fechaDesde;
    @FXML
    private DatePicker fechaHasta;

    // Botones de filtros
    @FXML
    private Button btnFiltrar;
    @FXML
    private Button btnLimpiar;

    // Tabla de ventas
    @FXML
    private TableView<Venta> tablaVentas;

    // Columnas de la tabla
    @FXML
    private TableColumn<Venta, Integer> idColumn;
    @FXML
    private TableColumn<Venta, Double> totalColumn;
    @FXML
    private TableColumn<Venta, String> vendedorColumn;
    @FXML
    private TableColumn<Venta, Venta.EstadoVenta> estadoColumn;
    @FXML
    private TableColumn<Venta, String> medioPagoColumn;
    @FXML
    private TableColumn<Venta, String> fechaColumn;
    @FXML
    private TableColumn<Venta, String> observacionesColumn;

    // Botones de acción
    @FXML
    private Button btnVerDetalles;
    @FXML
    private Button btnCambiarEstado;
    @FXML
    private Button btnGenerarPDF;
    @FXML
    private Button btnExportar;

    // Labels de estadísticas
    @FXML
    private Label lblTotalVentas;
    @FXML
    private Label lblVentasCompletadas;
    @FXML
    private Label lblVentasPendientes;
    @FXML
    private Label lblVentasCanceladas;
    @FXML
    private Label lblMontoTotal;
    @FXML
    private Label lblPromedioVenta;

    // Otros campos necesarios
    private HttpClient httpClient;
    private Gson gson;
    private Venta[] ventasOriginales;



    private MedioPago[] mediosPago;
    private Usuario[] usuarios;
    private DetalleVentaDTO[] detalleVentasDTO;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar HttpClient y Gson
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();

        // Configurar las columnas de la tabla
        configurarColumnas();

        // Cargar datos iniciales
        cargarEstadosComboBox();
        mostrarVentas();
        cargarUsuarios();
        cargarMediosPago();
        cargarMediosPagoComboBox();
        mostrarProductos();
        mostrarDetallesVentas();
        txtBuscarVenta.textProperty().addListener((obs, oldValue, newValue) -> {
            if (ventasOriginales == null) return;

            String filtro = newValue.toLowerCase();

            tablaVentas.getItems().setAll(
                    java.util.Arrays.stream(ventasOriginales)
                            .filter(v -> {
                                String vendedor = buscarUsuarioPorId(v.getUsuarioDTO().getId()) != null ?
                                        buscarUsuarioPorId(v.getUsuarioDTO().getId()).getNombre().toLowerCase() : "";

                                String medioPago = buscarMedioPagoPorId(v.getMedioPago().getId()) != null ?
                                        buscarMedioPagoPorId(v.getMedioPago().getId()).getTipo().toLowerCase() : "";

                                //String observaciones = v.getObservaciones() != null ? v.getObservaciones().toLowerCase() : "";

                                return vendedor.contains(filtro)
                                        || medioPago.contains(filtro)
                                        //|| observaciones.contains(filtro)
                                        || String.valueOf(v.getId()).contains(filtro)
                                        || String.valueOf(v.getTotal()).contains(filtro)
                                        || (v.getFechaPago() != null && v.getFechaPago().toLowerCase().contains(filtro))
                                        || v.getEstado().toString().toLowerCase().contains(filtro);
                            })
                            .toList()
            );
        });

    }





    private void configurarColumnas() {
        // Configurar columnas básicas
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Columna de vendedor - obtener nombre del usuario
        vendedorColumn.setCellValueFactory(cellData -> {
            int usuarioId = cellData.getValue().getUsuarioDTO().getId(); // Asumiendo que tienes getUsuarioId()
            Usuario usuario = buscarUsuarioPorId(usuarioId);
            return new SimpleStringProperty(usuario != null ? usuario.getNombre() : "N/A");
        });

        // Columna de estado
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Columna de medio de pago
        medioPagoColumn.setCellValueFactory(cellData -> {
            int medioPagoId = cellData.getValue().getMedioPago().getId(); // Asumiendo que tienes getMedioPagoId()
            MedioPago medioPago = buscarMedioPagoPorId(medioPagoId);
            return new SimpleStringProperty(medioPago != null ? medioPago.getTipo() : "N/A");
        });

        // Columna de fecha - formatear la fecha
        fechaColumn.setCellValueFactory(cellData -> {
            String fechaStr = cellData.getValue().getFechaPago();
            if (fechaStr != null) {
                try {
                    // Primero lo parseás desde el formato que viene del backend
                    DateTimeFormatter parser = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    LocalDateTime fecha = LocalDateTime.parse(fechaStr, parser);

                    // Luego lo formateás al formato que querés mostrar
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    String fechaFormateada = fecha.format(formatter);

                    return new SimpleStringProperty(fechaFormateada);
                } catch (Exception e) {
                    // Por si la fecha viene mal
                    return new SimpleStringProperty("Fecha inválida");
                }
            }
            return new SimpleStringProperty("N/A");
        });



        // Columna de observaciones
        observacionesColumn.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        // Formatear la columna de total para mostrar como moneda
        totalColumn.setCellFactory(column -> new TableCell<Venta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        // Formatear la columna de estado con colores
        estadoColumn.setCellFactory(column -> new TableCell<Venta, Venta.EstadoVenta>() {
            @Override
            protected void updateItem(Venta.EstadoVenta item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    String estadoStr = item.toString();
                    switch (estadoStr) {
                        case "COMPLETADA":
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case "PENDIENTE":
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            break;
                        case "CANCELADA":
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

    private void cargarEstadosComboBox() {
        filtroEstado.getItems().clear();
        filtroEstado.getItems().add("Todos");

        // Agregar los estados del enum
        for (Venta.EstadoVenta estado : Venta.EstadoVenta.values()) {
            filtroEstado.getItems().add(estado.toString());
        }

        filtroEstado.setValue("Todos");
    }

    private void cargarMediosPagoComboBox() {
        // Usar el array ya cargado en lugar de hacer otra petición
        filtroMedioPago.getItems().clear();
        filtroMedioPago.getItems().add("Todos");

        if (mediosPago != null) {
            for (MedioPago medio : mediosPago) {
                filtroMedioPago.getItems().add(medio.getTipo());
            }
        }

        filtroMedioPago.setValue("Todos");
    }

    @FXML
    public void mostrarVentas() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/ventas"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            System.out.println("Ventas del BackEnd"+responseBody+"\n\n\n\n");
            ventasOriginales = gson.fromJson(responseBody, Venta[].class);

            tablaVentas.getItems().clear();
            tablaVentas.getItems().addAll(ventasOriginales);


            // Actualizar las estadísticas
            actualizarEstadisticas(ventasOriginales);

            System.out.println("Ventas cargadas correctamente: " + ventasOriginales.length);

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error Crítico", "Error al cargar las ventas: " + e.getMessage(), false);
        }
    }
    DetalleVenta[] detalleVentas;
    @FXML
    public void mostrarDetallesVentas() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/detalle-ventas"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            System.out.println("Ventas del BackEnd"+responseBody+"\n\n\n\n");
            detalleVentasDTO = gson.fromJson(responseBody, DetalleVentaDTO[].class);


            System.out.println("Detalles de ventas cargados correctamente: " + detalleVentasDTO.length);

             detalleVentas = new DetalleVenta[detalleVentasDTO.length];
            for (int i = 0; i < detalleVentasDTO.length; i++) {
                DetalleVentaDTO dto = detalleVentasDTO[i];

                // Buscar la venta y producto en tus listas originales
                Venta venta = buscarVentaPorId(dto.getVentaId());
                Producto producto = buscarProductoPorId(dto.getProductoId());

                // Construir el DetalleVenta real
                detalleVentas[i] = new DetalleVenta(dto.getId(), venta, dto.getCantidad(), dto.getPrecioUnitario(), producto);
            }



        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error Crítico", "Error al cargar las ventas: " + e.getMessage(), false);
        }
    }

    private Venta buscarVentaPorId(int id) {
        for (Venta v : ventasOriginales) {
            if (v.getId() == id) return v;
        }
        return null; // o lanzar una excepción si es crítico
    }
    Producto[] productosOriginales;
    public void mostrarProductos() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/productos"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            productosOriginales = gson.fromJson(responseBody, Producto[].class);


        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error Crítico", e.getMessage(), false);
        }

    }

    private Producto buscarProductoPorId(int id) {
        for (Producto p : productosOriginales) {
            if (p.getId() == id) return p;
        }
        return null;
    }




    private void actualizarEstadisticas(Venta[] ventas) {
        int totalVentas = ventas.length;
        int completadas = 0;
        int pendientes = 0;
        int canceladas = 0;
        double montoTotal = 0;

        for (Venta venta : ventas) {
            String estadoStr = venta.getEstado().toString();
            switch (estadoStr) {
                case "completada":
                    completadas++;
                    montoTotal += venta.getTotal();
                    break;
                case "pendiente":
                    pendientes++;
                    break;
                case "cancelada":
                    canceladas++;
                    break;
            }
        }

        double promedio = completadas > 0 ? montoTotal / completadas : 0;

        // Actualizar los labels
        lblTotalVentas.setText("Total Ventas: " + totalVentas);
        lblVentasCompletadas.setText("Completadas: " + completadas);
        lblVentasPendientes.setText("Pendientes: " + pendientes);
        lblVentasCanceladas.setText("Canceladas: " + canceladas);
        lblMontoTotal.setText(String.format("Monto Total: $%.2f", montoTotal));
        lblPromedioVenta.setText(String.format("Promedio: $%.2f", promedio));
    }

    private void notificar(String titulo, String mensaje, boolean exito) {
        Platform.runLater(() -> {
            Notifications notificacion = Notifications.create()
                    .title(titulo)
                    .text(mensaje)
                    .position(Pos.TOP_CENTER)
                    .hideAfter(Duration.seconds(4));
            if (exito) {
                notificacion.showInformation();
            } else {
                notificacion.showError();
            }
        });
    }

    private void cargarUsuarios() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/usuarios"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            usuarios = gson.fromJson(responseBody, Usuario[].class);
            System.out.println("Usuarios cargados: " + usuarios.length);

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error", "No se pudieron cargar los usuarios", false);
        }
    }
    private void cargarMediosPago() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/medioPagos"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            mediosPago = gson.fromJson(responseBody, MedioPago[].class);
            System.out.println("Medios de pago cargados: " + mediosPago.length);

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error", "No se pudieron cargar los medios de pago", false);
        }
    }

    private Usuario buscarUsuarioPorId(int id) {
        if (usuarios != null) {
            for (Usuario usuario : usuarios) {
                if (usuario.getId() == id) {
                    return usuario;
                }
            }
        }
        return null;
    }



    private MedioPago buscarMedioPagoPorId(int id) {
        if (mediosPago != null) {
            for (MedioPago medio : mediosPago) {
                if (medio.getId() == id) {
                    return medio;
                }
            }
        }
        return null;
    }


    // Métodos para los botones - implementar funcionalidad después
    @FXML
    private void onCambiarEstado() {
        // Implementar después
    }

    @FXML
    private void onGenerarPDF() {
        // Implementar después
    }

    @FXML
    private void onExportar() {
        // Implementar después
    }

    @FXML
    private void onFiltrar() {
        // Implementar después
    }

    @FXML
    private void onLimpiar() {
        // Implementar después
    }

    @FXML
    private void onVolver() {
        // Implementar después
    }
    @FXML
    private void onVerDetalles() {
        Venta ventaSeleccionada = tablaVentas.getSelectionModel().getSelectedItem();
        if (ventaSeleccionada == null) {
            notificar("Advertencia", "Debe seleccionar una venta para ver sus detalles.", false);
            return;
        }

        try {
            // Filtrar los detalles que pertenecen a esa venta
            List<DetalleVenta> detallesDeLaVenta = Arrays.stream(detalleVentasDTO)
                    .filter(dto -> dto.getVentaId() == ventaSeleccionada.getId())
                    .map(dto -> new DetalleVenta(
                            dto.getId(),
                            ventaSeleccionada,
                            dto.getCantidad(),
                            dto.getPrecioUnitario(),
                            buscarProductoPorId(dto.getProductoId())
                    ))
                    .toList();

            // Cargar la vista nueva
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/desktop/DetalleVentaView.fxml"));
            Parent root = loader.load();

            DetalleVentaController controller = loader.getController();
            controller.setDetalles(detallesDeLaVenta);

            Stage stage = new Stage();
            stage.setTitle("Detalle de Venta #" + ventaSeleccionada.getId());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error", "No se pudo abrir la ventana de detalles: " + e.getMessage(), false);
        }
    }

}