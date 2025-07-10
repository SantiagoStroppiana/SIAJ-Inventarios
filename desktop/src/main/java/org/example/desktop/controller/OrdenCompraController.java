package org.example.desktop.controller;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.dto.DetalleOrdenCompraDTO;
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

    private HttpClient httpClient = HttpClient.newHttpClient();
    private Gson gson = new Gson();

    // Lista para manejar los items de la orden
    private List<ItemOrden> itemsOrden = new ArrayList<>();
    private double totalOrden = 0.0;

    @FXML
    private void initialize() {
        cargarProveedores();

        orderDatePicker.setValue(LocalDate.now());

        supplierComboBox.setOnAction(e -> {
            Proveedor proveedorSeleccionado = supplierComboBox.getValue();
            if (proveedorSeleccionado != null) {
                cargarProductosPorProveedor(proveedorSeleccionado);
            } else {
                mostrarEstadoVacioProductos();
            }
        });

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
    private List<Producto> productos = new ArrayList<>();

    private void cargarProductosPorProveedor(Proveedor proveedor) {
        productGrid.getChildren().clear();

        try {
            String url = VariablesEntorno.getServerURL() + "/api/productos/proveedor/" + proveedor.getId();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            Producto[] productosArray = gson.fromJson(responseBody, Producto[].class);
            productos = Arrays.asList(productosArray);
            System.out.println("Productos del proveedor " + proveedor.getRazonSocial() + ":");
            for (Producto p : productos) {
                System.out.println("- " + p.getNombre() + " - $" + p.getPrecio());
            }

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


        Label imagenLabel = new Label("📦");
        imagenLabel.setStyle("-fx-font-size: 48px; -fx-alignment: center;");

        Label nombreLabel = new Label(producto.getNombre());
        nombreLabel.getStyleClass().add("pv-product-name");
        nombreLabel.setWrapText(true);
        nombreLabel.setMaxWidth(190);
        nombreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label precioLabel = new Label(String.format("$%.2f", producto.getPrecio()));
        precioLabel.getStyleClass().add("pv-product-price");
        precioLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2196F3;");

        Label stockLabel = new Label("Stock: " + producto.getStock());
        stockLabel.getStyleClass().add("pv-product-stock");
        stockLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Button botonAgregar = new Button("+ Agregar a Orden");
        botonAgregar.getStyleClass().addAll("pv-action-btn", "pv-add-btn");
        botonAgregar.setMaxWidth(Double.MAX_VALUE);
        botonAgregar.setOnAction(e -> agregarProductoAOrden(producto));

        tarjeta.getChildren().addAll(imagenLabel, nombreLabel, precioLabel, stockLabel, botonAgregar);
        return tarjeta;
    }

    private void agregarProductoAOrden(Producto producto) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Cantidad");
        dialog.setHeaderText("Agregar " + producto.getNombre());
        dialog.setContentText("Cantidad:");

        Optional<String> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            System.out.println("🔍 Usuario ingresó cantidad: " + resultado.get());
            try {
                int cantidad = Integer.parseInt(resultado.get());
                System.out.println("🔍 Cantidad parseada: " + cantidad);
                System.out.println("🔍 Stock disponible: " + producto.getStock());
                if (cantidad > 0 && cantidad <= producto.getStock()) {
                    System.out.println("🔍 Llamando agregarItemAOrden...");
                    agregarItemAOrden(producto, cantidad);
                } else {
                    System.out.println("❌ Cantidad inválida o excede stock");
                    mostrarAlerta("Error", "Cantidad inválida o excede el stock disponible");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Error al parsear cantidad: " + e.getMessage());
                mostrarAlerta("Error", "Por favor ingrese un número válido");
            }
        } else {
            System.out.println("🔍 Usuario canceló el dialog");
        }
    }

    private void agregarItemAOrden(Producto producto, int cantidad) {


        ItemOrden itemExistente = null;
        for (ItemOrden item : itemsOrden) {
            if (item.getProducto().getId() == producto.getId()) {
                itemExistente = item;
                break;
            }
        }

        if (itemExistente != null) {

            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);

        } else {


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
        System.out.println("🔍 generarOrden llamado - itemsOrden size: " + itemsOrden.size());

//        // Imprimir todos los items
//        for (int i = 0; i < itemsOrden.size(); i++) {
//            ItemOrden item = itemsOrden.get(i);
//            System.out.println("🔍 Item " + i + ": " + item.getProducto().getNombre() +
//                    " - Cantidad: " + item.getCantidad() +
//                    " - Precio: $" + item.getProducto().getPrecio());
//        }

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

        // Paso 1: Crear orden de compra
        Proveedor proveedor = supplierComboBox.getValue();
        LocalDate fecha = orderDatePicker.getValue();
        MedioPago medioPagoDummy = new MedioPago();
        medioPagoDummy.setId(1);
        medioPagoDummy.setTipo("TEMPORAL");
        double total = Double.parseDouble(totalLabel.getText().replace(",", "."));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String fechaFormateada = LocalDateTime.now().format(formatter);

        if (medioPagoDummy == null) {
            notificar("Error", "Seleccione un medio de pago.", false);
            return;
        }

        OrdenCompraDTO ordenCompraDTO = new OrdenCompraDTO(
                proveedor.getId(),
                medioPagoDummy.getId(),
                new BigDecimal(total),
                fechaFormateada,
                OrdenCompra.EstadoOrden.pendiente.toString()
        );

        String ordenJson = gson.toJson(ordenCompraDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VariablesEntorno.getServerURL() + "/api/crearOrdenCompra"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ordenJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            System.err.println("Error al crear orden: " + response.body());
            notificar("Error", "No se pudo crear la orden de compra.", false);
            return;
        }

        OrdenCompraDTO ordenCompraCreadaDTO = gson.fromJson(response.body(), OrdenCompraDTO.class);

        // Paso 2: Crear los detalles de la orden
        boolean todosDetallesCreados = true;

        for (ItemOrden item : itemsOrden) {
            int cantidad = item.getCantidad();
            double precioUnitario = item.getProducto().getPrecio().doubleValue();
            Producto producto = item.getProducto();

            DetalleOrdenCompraDTO detalle = new DetalleOrdenCompraDTO();
            detalle.setOrdenCompraId(ordenCompraCreadaDTO.getId());
            detalle.setProductoId(producto.getId());
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);

            String json = gson.toJson(detalle);
            System.out.println("JSON detalle: " + json);

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/crear-detalle-orden"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
            System.out.println("Respuesta detalle: " + response2.body());

            if (response2.statusCode() == 200 || response2.statusCode() == 201) {

                notificar("Detalle creado", producto.getNombre(), true);
            } else {
                System.err.println("Error al crear detalle para producto " + producto.getNombre() + ": " + response2.body());
                notificar("Error", "No se pudo crear el detalle para " + producto.getNombre(), false);
                todosDetallesCreados = false;
            }
        }

        // Paso 3: Mostrar mensaje de éxito y limpiar SOLO si todo salió bien
        if (todosDetallesCreados) {
            mostrarAlerta("Éxito", "Orden de compra y todos los detalles creados correctamente.");
            limpiarOrden(); // ← AHORA sí, al final
        } else {
            mostrarAlerta("Advertencia", "La orden se creó pero hubo problemas con algunos detalles.");
        }
    }

    private Producto buscarProductoEnLista(String nombre) {
        for (Producto producto : productos) {
            if (producto.getNombre().equalsIgnoreCase(nombre)) {
                return producto;
            }
        }
        return null;
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