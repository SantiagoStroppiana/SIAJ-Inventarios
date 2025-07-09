package org.example.desktop.controller;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.dto.OrdenCompraDTO;
import org.example.desktop.model.*;
import org.example.desktop.util.UserSession;
import org.example.desktop.util.VariablesEntorno;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.google.gson.Gson;

public class OrdenCompraController {

    @FXML
    private ComboBox<Proveedor> supplierComboBox;

    @FXML
    private DatePicker orderDatePicker;

    @FXML
    private GridPane productGrid;

    @FXML
    private VBox emptyProductsState;

    @FXML
    private VBox orderContent;

    @FXML
    private VBox emptyOrderState;

    @FXML
    private Label totalLabel;

    @FXML
    private Label orderTitleLabel;

    @FXML
    private TextArea notesTextArea;

    @FXML
    private Button btnGenerateOrder;

    @FXML
    private Button btnClearOrder;

    @FXML
    private Button btnRefreshCatalog;

    // Cliente HTTP y Gson
    private HttpClient httpClient = HttpClient.newHttpClient();
    private Gson gson = new Gson();

    // Lista para manejar los items de la orden
    private List<ItemOrden> itemsOrden = new ArrayList<>();
    private double totalOrden = 0.0;

    @FXML
    private void initialize() {
        // Cargar proveedores desde el backend
        cargarProveedores();

        // Configurar fecha por defecto
        orderDatePicker.setValue(LocalDate.now());

        // Configurar evento cuando cambie el proveedor
        supplierComboBox.setOnAction(e -> {
            Proveedor proveedorSeleccionado = supplierComboBox.getValue();
            if (proveedorSeleccionado != null) {
                cargarProductosPorProveedor(proveedorSeleccionado);
            } else {
                mostrarEstadoVacioProductos();
            }
        });

        // Configurar botones
        btnClearOrder.setOnAction(e -> limpiarOrden());
        btnGenerateOrder.setOnAction(e -> {
            try {
                generarOrden();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnRefreshCatalog.setOnAction(e -> actualizarCatalogo());
    }

    private void cargarProveedores() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/proveedores"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);

            String responseBody = response.body();
            System.out.println(responseBody);

            Proveedor[] proveedoresArray = gson.fromJson(responseBody, Proveedor[].class);
            List<Proveedor> proveedores = Arrays.asList(proveedoresArray);

            for (Proveedor p : proveedores) {
                System.out.println("Proveedor: " + p.getRazonSocial() + ", Teléfono: " + p.getTelefono());
            }

            System.out.println("Respuesta del backend:");
            System.out.println(responseBody);

            supplierComboBox.getItems().addAll(proveedores);

            // Configurar cómo mostrar los proveedores en el ComboBox
            supplierComboBox.setConverter(new StringConverter<Proveedor>() {
                @Override
                public String toString(Proveedor proveedor) {
                    return proveedor != null ? proveedor.getRazonSocial() : "";
                }

                @Override
                public Proveedor fromString(String string) {
                    return null; // No necesario para este caso
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los proveedores");
        }
    }

    private void cargarProductosPorProveedor(Proveedor proveedor) {
        // Limpiar productos anteriores
        productGrid.getChildren().clear();

        try {
            // Armo la URL con el ID del proveedor
            String url = VariablesEntorno.getServerURL() + "/api/productos/proveedor/" + proveedor.getId();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // Envío la petición
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Parseo la respuesta
            String responseBody = response.body();
            Producto[] productosArray = gson.fromJson(responseBody, Producto[].class);
            List<Producto> productos = Arrays.asList(productosArray);

            System.out.println("Productos del proveedor " + proveedor.getRazonSocial() + ":");
            for (Producto p : productos) {
                System.out.println("- " + p.getNombre() + " - $" + p.getPrecio());
            }

            // Mostrar los productos en la UI
            if (productos.isEmpty()) {
                mostrarEstadoVacioProductos(); // Muestra algo tipo "No hay productos"
            } else {
                mostrarProductos(productos); // Muestra los productos en la grilla
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los productos del proveedor");
            mostrarEstadoVacioProductos();
        }
    }


    private void mostrarEstadoVacioProductos() {
        emptyProductsState.setVisible(true);
        emptyProductsState.setManaged(true);
        productGrid.setVisible(false);
        productGrid.setManaged(false);
    }

    private void mostrarProductos(List<Producto> productos) {
        // Ocultar estado vacío
        emptyProductsState.setVisible(false);
        emptyProductsState.setManaged(false);
        productGrid.setVisible(true);
        productGrid.setManaged(true);

        // Crear tarjetas de productos
        int columna = 0;
        int fila = 0;
        int maxColumnas = 3;

        for (Producto producto : productos) {
            VBox tarjetaProducto = crearTarjetaProducto(producto);
            productGrid.add(tarjetaProducto, columna, fila);

            columna++;
            if (columna >= maxColumnas) {
                columna = 0;
                fila++;
            }
        }
    }

    private VBox crearTarjetaProducto(Producto producto) {
        VBox tarjeta = new VBox(10);
        tarjeta.getStyleClass().add("pv-product-card");
        tarjeta.setPrefWidth(220);
        tarjeta.setPrefHeight(280);
        tarjeta.setPadding(new Insets(15));

        // Imagen del producto
        Label imagenLabel = new Label("📦");
        imagenLabel.setStyle("-fx-font-size: 48px; -fx-alignment: center;");

        // Nombre del producto
        Label nombreLabel = new Label(producto.getNombre());
        nombreLabel.getStyleClass().add("pv-product-name");
        nombreLabel.setWrapText(true);
        nombreLabel.setMaxWidth(190);
        nombreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Precio
        Label precioLabel = new Label(String.format("$%.2f", producto.getPrecio()));
        precioLabel.getStyleClass().add("pv-product-price");
        precioLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2196F3;");

        // Stock
        Label stockLabel = new Label("Stock: " + producto.getStock());
        stockLabel.getStyleClass().add("pv-product-stock");
        stockLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Botón agregar
        Button botonAgregar = new Button("+ Agregar a Orden");
        botonAgregar.getStyleClass().addAll("pv-action-btn", "pv-add-btn");
        botonAgregar.setMaxWidth(Double.MAX_VALUE);
        botonAgregar.setOnAction(e -> agregarProductoAOrden(producto));

        tarjeta.getChildren().addAll(imagenLabel, nombreLabel, precioLabel, stockLabel, botonAgregar);
        return tarjeta;
    }

    private void agregarProductoAOrden(Producto producto) {
        // Crear dialog para cantidad
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Cantidad");
        dialog.setHeaderText("Agregar " + producto.getNombre());
        dialog.setContentText("Cantidad:");

        Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            try {
                int cantidad = Integer.parseInt(resultado.get());
                if (cantidad > 0 && cantidad <= producto.getStock()) {
                    agregarItemAOrden(producto, cantidad);
                } else {
                    mostrarAlerta("Error", "Cantidad inválida o excede el stock disponible");
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Por favor ingrese un número válido");
            }
        }
    }

    private void agregarItemAOrden(Producto producto, int cantidad) {
        // Verificar si el producto ya está en la orden
        ItemOrden itemExistente = null;
        for (ItemOrden item : itemsOrden) {
            if (item.getProducto().getId() == producto.getId()) {
                itemExistente = item;
                break;
            }
        }

        if (itemExistente != null) {
            // Actualizar cantidad
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
        } else {
            // Crear nuevo item
            ItemOrden nuevoItem = new ItemOrden(producto, cantidad);
            itemsOrden.add(nuevoItem);
        }

        actualizarVistaOrden();
    }

    private void actualizarVistaOrden() {
        // Limpiar contenido anterior
        orderContent.getChildren().clear();

        if (itemsOrden.isEmpty()) {
            // Mostrar estado vacío
            emptyOrderState.setVisible(true);
            emptyOrderState.setManaged(true);
        } else {
            // Ocultar estado vacío
            emptyOrderState.setVisible(false);
            emptyOrderState.setManaged(false);

            // Crear tarjetas de items
            for (ItemOrden item : itemsOrden) {
                HBox tarjetaItem = crearTarjetaItem(item);
                orderContent.getChildren().add(tarjetaItem);
            }
        }

        // Actualizar total y contador
        calcularTotal();
        actualizarContadorItems();

        // Habilitar/deshabilitar botón generar orden
        btnGenerateOrder.setDisable(itemsOrden.isEmpty());
    }

    private HBox crearTarjetaItem(ItemOrden item) {
        HBox tarjeta = new HBox(15);
        tarjeta.getStyleClass().add("pv-cart-item");
        tarjeta.setPadding(new Insets(10));
        tarjeta.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 8px;");

        // Información del producto
        VBox infoProducto = new VBox(5);
        infoProducto.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(infoProducto, Priority.ALWAYS);

        Label nombreLabel = new Label(item.getProducto().getNombre());
        nombreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label precioLabel = new Label(String.format("$%.2f c/u", item.getProducto().getPrecio()));
        precioLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        infoProducto.getChildren().addAll(nombreLabel, precioLabel);

        // Controles de cantidad
        HBox controlesCanitdad = new HBox(5);
        controlesCanitdad.setStyle("-fx-alignment: center;");

        Button btnMenos = new Button("-");
        btnMenos.setStyle("-fx-pref-width: 30px; -fx-pref-height: 30px;");
        btnMenos.setOnAction(e -> cambiarCantidad(item, -1));

        Label cantidadLabel = new Label(String.valueOf(item.getCantidad()));
        cantidadLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 30px; -fx-alignment: center;");

        Button btnMas = new Button("+");
        btnMas.setStyle("-fx-pref-width: 30px; -fx-pref-height: 30px;");
        btnMas.setOnAction(e -> cambiarCantidad(item, 1));

        controlesCanitdad.getChildren().addAll(btnMenos, cantidadLabel, btnMas);

        // Total del item
        Label totalItemLabel = new Label(String.format("$%.2f", item.getTotal()));
        totalItemLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Botón eliminar
        Button btnEliminar = new Button("🗑️");
        btnEliminar.setStyle("-fx-pref-width: 35px; -fx-pref-height: 35px;");
        btnEliminar.setOnAction(e -> eliminarItem(item));

        tarjeta.getChildren().addAll(infoProducto, controlesCanitdad, totalItemLabel, btnEliminar);
        return tarjeta;
    }

    private void cambiarCantidad(ItemOrden item, int cambio) {
        int nuevaCantidad = item.getCantidad() + cambio;
        if (nuevaCantidad <= 0) {
            eliminarItem(item);
        } else if (nuevaCantidad <= item.getProducto().getStock()) {
            item.setCantidad(nuevaCantidad);
            actualizarVistaOrden();
        } else {
            mostrarAlerta("Error", "Cantidad excede el stock disponible");
        }
    }

    private void eliminarItem(ItemOrden item) {
        itemsOrden.remove(item);
        actualizarVistaOrden();
    }

    private void calcularTotal() {
        totalOrden = itemsOrden.stream()
                .mapToDouble(ItemOrden::getTotal)
                .sum();
        totalLabel.setText(String.format("%.2f", totalOrden));
    }

    private void actualizarContadorItems() {
        int totalItems = itemsOrden.stream()
                .mapToInt(ItemOrden::getCantidad)
                .sum();
        orderTitleLabel.setText("📝 Items de la Orden (" + totalItems + ")");
    }

    private void limpiarOrden() {
        itemsOrden.clear();
        actualizarVistaOrden();
    }

    private void generarOrden() throws IOException, InterruptedException {
        if (supplierComboBox.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar un proveedor");
            return;
        }

        if (orderDatePicker.getValue() == null) {
            mostrarAlerta("Error", "Debe seleccionar una fecha");
            return;
        }

        if (itemsOrden.isEmpty()) {
            mostrarAlerta("Error", "Debe agregar al menos un item");
            return;
        }

        // Aquí harías tu petición HTTP para generar la orden
        // OrdenCompra orden = new OrdenCompra(
        //     supplierComboBox.getValue(),
        //     orderDatePicker.getValue(),
        //     itemsOrden,
        //     notesTextArea.getText()
        // );
        // backendService.crearOrdenCompra(orden);
        // Paso 1: Crear venta
        // Obtener datos necesarios
        Proveedor proveedor = supplierComboBox.getValue();
        LocalDate fecha = orderDatePicker.getValue();
        //MedioPago medioPago = comboMedioPago.getSelectionModel().getSelectedItem(); // Asegurate de tenerlo declarado
        MedioPago medioPagoDummy = new MedioPago();
        medioPagoDummy.setId(1); // ID temporal
        medioPagoDummy.setTipo("TEMPORAL"); // Tipo cualquiera
        double total = Double.parseDouble(totalLabel.getText().replace(",", "."));


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String fechaFormateada = LocalDateTime.now().format(formatter);


        if (medioPagoDummy == null) {
            notificar("Error", "Seleccione un medio de pago.", false);
            return;
        }

// Crear objeto OrdenCompra (adaptalo si usás DTO)
        OrdenCompraDTO ordenCompraDTO = new OrdenCompraDTO(
                proveedor.getId(),
                medioPagoDummy.getId(),
                new BigDecimal(total),
                fechaFormateada,
                OrdenCompra.EstadoOrden.pendiente.toString()  // o el enum correspondiente
        );


        /*
        String fechaPagoStr = LocalDateTime.now().toString(); // por ejemplo: "2025-07-09T23:15:30"

        Map<String, Object> ordenMap = new HashMap<>();
        ordenMap.put("estado", "pendiente");
        ordenMap.put("proveedor", proveedor.getId());
        ordenMap.put("total", total);
        ordenMap.put("medioPago", medioPagoDummy.getId());
        ordenMap.put("fechaPago", fechaPagoStr); // ← string plano


// Serializar a JSON
        String ordenJson = gson.toJson(ordenMap);

// Enviar al backend
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VariablesEntorno.getServerURL() + "/api/crearOrdenCompra"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ordenJson))
                .build();*/
        String ordenJson = gson.toJson(ordenCompraDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VariablesEntorno.getServerURL() + "/api/crearOrdenCompra"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ordenJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            mostrarAlerta("Éxito", "Orden de compra generada correctamente.");
            limpiarOrden();
        } else {
            System.err.println("Error al crear orden: " + response.body());
            notificar("Error", "No se pudo crear la orden de compra.", false);
        }







        // TODO: FALTARIA CREAR LOS DETALLES OC

        mostrarAlerta("Éxito", "Orden de compra generada correctamente");
        limpiarOrden();
    }
    private void notificar(String titulo, String mensaje, boolean exito){
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

    private void actualizarCatalogo() {
        Proveedor proveedorSeleccionado = supplierComboBox.getValue();
        if (proveedorSeleccionado != null) {
            cargarProductosPorProveedor(proveedorSeleccionado);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Clase interna para manejar items de la orden
    private static class ItemOrden {
        private Producto producto;
        private int cantidad;

        public ItemOrden(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }

        public Producto getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public double getTotal() { return producto.getPrecio().doubleValue() * cantidad; }
    }

}