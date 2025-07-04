package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.model.Producto;
import org.example.desktop.model.Venta;
import org.example.desktop.util.VariablesEntorno;
import javafx.scene.control.Label;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.control.TextField;
import javafx.collections.ObservableList;

public class VentaController implements Initializable {

    @FXML
    private GridPane productGrid;

    @FXML
    private VBox cartContent;

    @FXML
    private Label totalLabel;

    @FXML
    private Label cartTitleLabel;

    @FXML
    private Button btnProcesarVenta, btnLimpiarCarrito;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

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

    public void mostrarProductos() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/productos"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            Producto[] productos = gson.fromJson(responseBody, Producto[].class);

            // Limpiar productos actuales del GridPane
            productGrid.getChildren().clear();

            int column = 0;
            int row = 0;

            for (Producto producto : productos) {
                VBox productoCard = crearTarjetaProducto(producto);
                productGrid.add(productoCard, column, row);

                column++;
                if (column == 2) {
                    column = 0;
                    row++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error Crítico", e.getMessage(), false);
        }
    }

    private VBox crearTarjetaProducto(Producto producto) {
        VBox card = new VBox();
        card.getStyleClass().add("pv-product-card");
        card.setSpacing(5);

        Label nombre = new Label(producto.getNombre());
        nombre.setFont(Font.font("System Bold", 16));
        nombre.getStyleClass().add("pv-product-name");

        Label categoria = new Label(/*producto.getCategoria() != null ?
                producto.getCategoria().getNombre() : "Sin categoría"*/);
        categoria.getStyleClass().add("pv-product-category");

        Label precio = new Label("$" + producto.getPrecio());
        precio.setFont(Font.font("System Bold", 18));
        precio.getStyleClass().add("pv-product-price");

        Label stock = new Label("Stock: " + producto.getStock() + " unidades");
        stock.getStyleClass().add("pv-product-stock");

        TextField cantidadField = new TextField("1");
        cantidadField.setPrefWidth(50);
        cantidadField.setPromptText("Cant.");
        cantidadField.setMaxWidth(60);

        Button agregarBtn = new Button("+ Agregar al Carrito");
        agregarBtn.getStyleClass().add("pv-add-btn");
        agregarBtn.setOnAction(e -> {
            int cantidad = 1;
            try {
                cantidad = Integer.parseInt(cantidadField.getText());
                if (cantidad < 1) cantidad = 1;
            } catch (NumberFormatException ex) {
                cantidad = 1;
            }
            agregarAlCarrito(producto, cantidad);
        });

        card.getChildren().addAll(nombre, categoria, precio, stock, cantidadField, agregarBtn);

        return card;
    }

    private void agregarAlCarrito(Producto producto, int cantidadInicial) {
        HBox item = new HBox();
        item.setSpacing(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("pv-cart-item");

        Label nombre = new Label(producto.getNombre());
        nombre.setPrefWidth(130);
        nombre.getStyleClass().add("pv-cart-item-name");

        TextField cantidadField = new TextField(String.valueOf(cantidadInicial));
        cantidadField.setPrefWidth(50);
        cantidadField.setAlignment(Pos.CENTER);

        Label precioUnitario = new Label("$" + producto.getPrecio());
        precioUnitario.setPrefWidth(80);
        precioUnitario.getStyleClass().add("pv-cart-item-price");

        Label subtotalLabel = new Label();
        subtotalLabel.setPrefWidth(85);
        subtotalLabel.getStyleClass().add("pv-cart-item-price");
        subtotalLabel.setStyle("-fx-text-fill: #ffffff");

        Button eliminarBtn = new Button("✖");
        eliminarBtn.setOnAction(e -> {
            cartContent.getChildren().remove(item);
            actualizarTotales();
        });

        // CORRECCIÓN: Convertir BigDecimal a double
        cantidadField.textProperty().addListener((obs, oldVal, newVal) -> {
            actualizarSubtotal(item, producto.getPrecio().doubleValue());
            actualizarTotales();
        });

        item.getChildren().addAll(nombre, cantidadField, precioUnitario, subtotalLabel, eliminarBtn);
        cartContent.getChildren().add(item);

        // Calcular subtotal inicial
        actualizarSubtotal(item, producto.getPrecio().doubleValue());
        actualizarTotales();
    }

    // NUEVO MÉTODO: Para actualizar el subtotal de un item específico
    private void actualizarSubtotal(HBox item, double precioUnitario) {
        try {
            TextField cantidadField = (TextField) item.getChildren().get(1);
            Label subtotalLabel = (Label) item.getChildren().get(3);

            int cantidad = Integer.parseInt(cantidadField.getText());
            double subtotal = precioUnitario * cantidad;

            subtotalLabel.setText("$" + String.format("%.2f", subtotal));
        } catch (NumberFormatException e) {
            Label subtotalLabel = (Label) item.getChildren().get(3);
            subtotalLabel.setText("Error");
        }
    }

    // MÉTODO CORREGIDO: actualizarTotales
    private void actualizarTotales() {
        double total = 0.0;
        int totalItems = 0;

        for (Node node : cartContent.getChildren()) {
            if (node instanceof HBox item) {
                // CORRECCIÓN: Usar item.getChildren() en lugar de FXML.getChildren()
                ObservableList<Node> children = item.getChildren();

                try {
                    // Obtener cantidad del TextField
                    TextField cantidadField = (TextField) children.get(1);
                    int cantidad = Integer.parseInt(cantidadField.getText());

                    // Obtener precio del Label (removiendo el símbolo $)
                    Label precioLabel = (Label) children.get(2);
                    String precioStr = precioLabel.getText().replace("$", "").trim();
                    double precio = Double.parseDouble(precioStr);

                    // Calcular subtotal
                    double subtotal = precio * cantidad;

                    // Actualizar el label de subtotal
                    Label subtotalLabel = (Label) children.get(3);
                    subtotalLabel.setText("$" + String.format("%.2f", subtotal));

                    total += subtotal;
                    totalItems += cantidad;
                } catch (NumberFormatException e) {
                    // En caso de error, mostrar "Error" en el subtotal
                    Label subtotalLabel = (Label) children.get(3);
                    subtotalLabel.setText("Error");
                }
            }
        }

        totalLabel.setText(String.format("%.2f", total));
        cartTitleLabel.setText("🛒 Carrito de Compras (" + totalItems + ")");
    }

    @FXML
    private void limpiarCarrito() {
        // CORRECCIÓN: Usar cartContent en lugar de HttpRequest
        cartContent.getChildren().clear();
        actualizarTotales();
    }

    @FXML
    public void mostrarVentas() {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/ventas"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            Venta[] ventas = gson.fromJson(responseBody, Venta[].class);

//            tablaUsuarios.getItems().clear();
            //          tablaUsuarios.getItems().addAll(usuarios);

            for (Venta v : ventas){
                System.out.println(v);
            }

            System.out.println("Respuesta del backend:");
            System.out.println(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error Crítico", e.getMessage(), false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mostrarVentas();
        mostrarProductos();
        btnLimpiarCarrito.setOnAction(onclick -> {limpiarCarrito();});
    }
}