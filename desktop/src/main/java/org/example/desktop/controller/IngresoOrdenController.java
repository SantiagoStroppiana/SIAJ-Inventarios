package org.example.desktop.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.Notifications;
import org.example.desktop.dto.DetalleOrdenCompraDTO;
import org.example.desktop.dto.OrdenCompraDTO;
import org.example.desktop.model.*;
import org.example.desktop.util.VariablesEntorno;


import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class IngresoOrdenController {

    @FXML
    private ComboBox<OrdenCompraDTO> ordenCompraComboBox;

    @FXML
    private VBox productosIngresadosContainer;

    @FXML
    private TextArea notaIngresoTextArea;

    @FXML
    private Button btnConfirmarIngreso;

    @FXML
    private Button btnCancelar;

    // Cliente HTTP y Gson
    private HttpClient httpClient = HttpClient.newHttpClient();
    private Gson gson = new Gson();

    // Lista para manejar los detalles de la orden
    private List<DetalleOrdenCompraDTO> detallesOrden = new ArrayList<>();
    private List<ItemIngreso> itemsIngreso = new ArrayList<>();

    @FXML
    private void initialize() {
        // Cargar órdenes de compra desde el backend
        cargarOrdenesCompra();

        // Configurar evento cuando cambie la orden
        ordenCompraComboBox.setOnAction(e -> {
            OrdenCompraDTO ordenSeleccionada = ordenCompraComboBox.getValue();
            if (ordenSeleccionada != null) {
                cargarDetallesOrden(ordenSeleccionada);
            } else {
                limpiarProductosIngresados();
            }
        });

        // Configurar botones
        btnConfirmarIngreso.setOnAction(e -> {
            try {
                confirmarIngreso();
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
                mostrarAlerta("Error", "Error al confirmar el ingreso");
            }
        });

        btnCancelar.setOnAction(e -> cancelarIngreso());

        // Deshabilitar botón confirmar inicialmente
        btnConfirmarIngreso.setDisable(true);
    }

    private void cargarOrdenesCompra() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/OrdenCompras"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Respuesta órdenes de compra: " + response.body());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                System.out.println("JSON recibido: " + responseBody);

                Type listType = new TypeToken<List<OrdenCompraDTO>>(){}.getType();
                List<OrdenCompraDTO> ordenesCompra = gson.fromJson(responseBody, listType);

                // Filtrar solo órdenes pendientes
                List<OrdenCompraDTO> ordenesPendientes = new ArrayList<>();
                for (OrdenCompraDTO orden : ordenesCompra) {
                    if (orden.getEstado().equals("pendiente")) {
                        ordenesPendientes.add(orden);
                    }
                }

                for (OrdenCompraDTO orden : ordenesPendientes) {
                    System.out.println("Orden: " + orden.getId() +
                            ", Proveedor: " + orden.getProveedorId() +
                            ", Total: " + orden.getTotal());
                }

                ordenCompraComboBox.getItems().addAll(ordenesPendientes);

                // Configurar cómo mostrar las órdenes en el ComboBox
                ordenCompraComboBox.setConverter(new StringConverter<OrdenCompraDTO>() {
                    @Override
                    public String toString(OrdenCompraDTO orden) {
                        if (orden != null) {
                            return String.format("Orden #%d - %s - $%.2f",
                                    orden.getId(),
                                    orden.getProveedorId(),
                                    orden.getTotal());
                        }
                        return "";
                    }

                    @Override
                    public OrdenCompraDTO fromString(String string) {
                        return null; // No necesario para este caso
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar las órdenes de compra");
        }
    }
    List<Producto> productos = new ArrayList<>();
    private void cargarDetallesOrden(OrdenCompraDTO orden) {
        // Limpiar detalles anteriores
        detallesOrden.clear();
        itemsIngreso.clear();
        productosIngresadosContainer.getChildren().clear();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/detalles-orden"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Respuesta detalles orden: " + response.body());

            if (response.statusCode() == 200) {

                String responseBody = response.body();
                System.out.println("JSON recibido: " + response.body());
                Type listType = new TypeToken<List<DetalleOrdenCompraDTO>>(){}.getType();
                List<DetalleOrdenCompraDTO> todosLosDetalles = gson.fromJson(responseBody, listType);





                try {
                    HttpRequest requestProductos = HttpRequest.newBuilder()
                            .uri(URI.create(VariablesEntorno.getServerURL() + "/api/productos"))
                            .GET()
                            .build();

                    HttpResponse<String> responseProductos = httpClient.send(requestProductos, HttpResponse.BodyHandlers.ofString());

                    if (responseProductos.statusCode() == 200) {
                        Type productoListType = new TypeToken<List<Producto>>(){}.getType();
                        productos = gson.fromJson(responseProductos.body(), productoListType);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarAlerta("Error", "No se pudieron cargar los productos");
                    return;
                }







                // Filtrar detalles por orden seleccionada
                for (DetalleOrdenCompraDTO detalle : todosLosDetalles) {
                    if (detalle.getOrdenCompraId() == orden.getId()) {
                        detallesOrden.add(detalle);
                    }
                }

                System.out.println("Detalles de la orden " + orden.getId() + ":");
                for (DetalleOrdenCompraDTO detalle : detallesOrden) {
                    Producto producto = productos.stream()
                            .filter(p -> p.getId() == detalle.getProductoId())
                            .findFirst()
                            .orElse(null);

                    if (producto != null) {
                        System.out.println("- Producto: " + producto.getNombre() +
                                ", Cantidad: " + detalle.getCantidad() +
                                ", Precio: $" + detalle.getPrecioUnitario());
                    } else {
                        System.out.println("- Producto ID: " + detalle.getProductoId() +
                                " (no encontrado), Cantidad: " + detalle.getCantidad() +
                                ", Precio: $" + detalle.getPrecioUnitario());
                    }
                }


                // Mostrar los detalles en la UI
                if (detallesOrden.isEmpty()) {
                    mostrarMensajeVacio();
                } else {
                    mostrarDetallesOrden();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los detalles de la orden");
            limpiarProductosIngresados();
        }
    }

    private void mostrarMensajeVacio() {
        Label mensajeVacio = new Label("No se encontraron productos para esta orden");
        mensajeVacio.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-padding: 20px;");
        productosIngresadosContainer.getChildren().add(mensajeVacio);
        btnConfirmarIngreso.setDisable(true);
    }

    private void mostrarDetallesOrden() {
        // Crear items de ingreso para cada detalle
        for (DetalleOrdenCompraDTO detalle : detallesOrden) {
            // Aquí deberías obtener la información del producto por su ID
            // Por ahora uso un placeholder
            ItemIngreso itemIngreso = new ItemIngreso(detalle, detalle.getCantidad());
            itemsIngreso.add(itemIngreso);

            HBox tarjetaItem = crearTarjetaItemIngreso(itemIngreso);
            productosIngresadosContainer.getChildren().add(tarjetaItem);
        }

        // Habilitar botón confirmar
        btnConfirmarIngreso.setDisable(false);
    }

    private Producto obtenerProductoPorId(int id) {
        return productos.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }


    private HBox crearTarjetaItemIngreso(ItemIngreso item) {
        HBox tarjeta = new HBox(20);
        tarjeta.getStyleClass().add("pv-cart-item");
        tarjeta.setPadding(new Insets(15));
        tarjeta.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 10px; -fx-border-color: #444; -fx-border-radius: 10px;");

        VBox infoProducto = new VBox(5);
        infoProducto.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(infoProducto, Priority.ALWAYS);

        int idproducto = item.getDetalle().getProductoId();
        Producto producto = obtenerProductoPorId(idproducto);
        Label nombreLabel = new Label("Nombre: " + producto.getNombre());
        nombreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #ffffff;");

        Label skuLabel = new Label("SKU: " + producto.getSku());
        skuLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaaaaa;");

        Label precioLabel = new Label(String.format("Precio: $%.2f c/u", item.getDetalle().getPrecioUnitario()));
        precioLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaaaaa;");

        infoProducto.getChildren().addAll(nombreLabel, skuLabel, precioLabel);

        VBox cantidadSolicitada = new VBox(5);
        cantidadSolicitada.setStyle("-fx-alignment: center;");

        Label labelSolicitada = new Label("Solicitada:");
        labelSolicitada.setStyle("-fx-font-size: 12px; -fx-text-fill: #bbbbbb;");

        Label cantidadSolicitadaLabel = new Label(String.valueOf(item.getDetalle().getCantidad()));
        cantidadSolicitadaLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #42a5f5;");

        cantidadSolicitada.getChildren().addAll(labelSolicitada, cantidadSolicitadaLabel);

        VBox cantidadIngresada = new VBox(5);
        cantidadIngresada.setStyle("-fx-alignment: center;");

        Label labelIngresada = new Label("Ingresada:");
        labelIngresada.setStyle("-fx-font-size: 12px; -fx-text-fill: #bbbbbb;");

        // 🔴 Primero declaramos totalItemLabel
        Label totalItemLabel = new Label(String.format("$%.2f", item.getTotal()));
        totalItemLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #66bb6a;");

        Spinner<Integer> spinnerCantidad = new Spinner<>(0, item.getDetalle().getCantidad(), item.getCantidadIngresada());
        spinnerCantidad.setEditable(true);
        spinnerCantidad.setPrefWidth(80);
        spinnerCantidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            item.setCantidadIngresada(newVal);
            totalItemLabel.setText(String.format("$%.2f", item.getTotal()));  // ✅ Ya existe acá
        });

        cantidadIngresada.getChildren().addAll(labelIngresada, spinnerCantidad);

        VBox totalItem = new VBox(5);
        totalItem.setStyle("-fx-alignment: center;");

        Label labelTotal = new Label("Total:");
        labelTotal.setStyle("-fx-font-size: 12px; -fx-text-fill: #bbbbbb;");

        totalItem.getChildren().addAll(labelTotal, totalItemLabel);

        tarjeta.getChildren().addAll(infoProducto, cantidadSolicitada, cantidadIngresada, totalItem);
        return tarjeta;
    }


    private void confirmarIngreso() throws IOException, InterruptedException {
        System.out.println("🔍 confirmarIngreso llamado");

        OrdenCompraDTO ordenSeleccionada = ordenCompraComboBox.getValue();
        if (ordenSeleccionada == null) {
            mostrarAlerta("Error", "Debe seleccionar una orden de compra");
            return;
        }

        if (itemsIngreso.isEmpty()) {
            mostrarAlerta("Error", "No hay productos para ingresar");
            return;
        }

        // Verificar que al menos un producto tenga cantidad ingresada
        boolean hayProductosIngresados = false;
        for (ItemIngreso item : itemsIngreso) {
            if (item.getCantidadIngresada() > 0) {
                hayProductosIngresados = true;
                break;
            }
        }

        if (!hayProductosIngresados) {
            mostrarAlerta("Error", "Debe ingresar al menos un producto");
            return;
        }

        // Aquí deberías implementar la lógica para:
        // 1. Actualizar el stock de los productos
        // 2. Marcar la orden como recibida/procesada
        // 3. Registrar el ingreso en el sistema

        // Por ahora, simulo el proceso exitoso
        boolean ingresoExitoso = true;

        if (ingresoExitoso) {
            notificar("Éxito", "Ingreso confirmado correctamente", true);

            // Limpiar formulario
            limpiarFormulario();
        } else {
            notificar("Error", "No se pudo confirmar el ingreso", false);
        }
    }

    private void cancelarIngreso() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Está seguro de que desea cancelar?");
        confirmacion.setContentText("Se perderán todos los datos ingresados.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        ordenCompraComboBox.setValue(null);
        limpiarProductosIngresados();
        notaIngresoTextArea.clear();
        btnConfirmarIngreso.setDisable(true);
    }

    private void limpiarProductosIngresados() {
        detallesOrden.clear();
        itemsIngreso.clear();
        productosIngresadosContainer.getChildren().clear();
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

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Clase interna para manejar items de ingreso
    private static class ItemIngreso {
        private DetalleOrdenCompraDTO detalle;
        private int cantidadIngresada;

        public ItemIngreso(DetalleOrdenCompraDTO detalle, int cantidadIngresada) {
            this.detalle = detalle;
            this.cantidadIngresada = cantidadIngresada;
        }

        public DetalleOrdenCompraDTO getDetalle() {
            return detalle;
        }

        public int getCantidadIngresada() {
            return cantidadIngresada;
        }

        public void setCantidadIngresada(int cantidadIngresada) {
            this.cantidadIngresada = cantidadIngresada;
        }

        public double getTotal() {
            return detalle.getPrecioUnitario() * cantidadIngresada;
        }
    }
}