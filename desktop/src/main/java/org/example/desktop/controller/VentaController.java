package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.DTO.ProductoVentaDTO;
import org.example.desktop.model.*;
import org.example.desktop.util.VariablesEntorno;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

import javafx.scene.text.Font;
import javafx.collections.ObservableList;
import org.example.desktop.util.VentaPDFGenerator;

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

    @FXML
    private ComboBox<MedioPago> comboMedioPago;  // 👈 NO ComboBox<String>

    private final Map<Integer, HBox> productosEnCarrito = new HashMap<>();

    @FXML
    private Button btnHistorialVentas;


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
            listaProductos = Arrays.asList(productos);

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
            agregarAlCarrito(producto,cantidad);
        });

        card.getChildren().addAll(nombre, categoria, precio, stock, cantidadField, agregarBtn);

        return card;
    }

    private Producto buscarProductoPorNombre(String nombre) {
        for (Producto p : listaProductos) {
            if (p.getNombre().equals(nombre)) {
                return p;
            }
        }
        return null;
    }

    private void agregarAlCarrito(Producto producto, int cantidadInicial) {

        if (producto.getStock() <= 0) {
            notificar("Falta de stock", "No hay stock del producto " + producto.getNombre(), false);
        } else {


        // Si ya existe en el carrito, sumar la cantidad
        if (productosEnCarrito.containsKey(producto.getId())) {
            HBox itemExistente = productosEnCarrito.get(producto.getId());
            TextField cantidadField = (TextField) itemExistente.getChildren().get(1);

            try {
                int cantidadActual = Integer.parseInt(cantidadField.getText());
                if (cantidadActual+cantidadInicial>producto.getStock()) {
                    notificar("Falta de stock", "No alcanza el stock del producto " + producto.getNombre()+"\nhay "+producto.getStock()+ " unidades", false);
                } else {
                    cantidadField.setText(String.valueOf(cantidadActual + cantidadInicial));
                }
            } catch (NumberFormatException ignored) {
                cantidadField.setText(String.valueOf(cantidadInicial));
            }

            actualizarSubtotal(itemExistente, producto.getPrecio().doubleValue());
            actualizarTotales();
            return;
        }

        // Crear nueva fila para el producto
        HBox item = new HBox();
        item.setSpacing(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("pv-cart-item");

        Label nombre = new Label(producto.getNombre());
        nombre.setPrefWidth(130);
        nombre.getStyleClass().add("pv-cart-item-name");

        TextField cantidadField = new TextField(String.valueOf(cantidadInicial));
        cantidadField.setPrefWidth(70);
        cantidadField.setAlignment(Pos.CENTER);

        Label precioUnitario = new Label("$" + producto.getPrecio());
        precioUnitario.setPrefWidth(80);
        precioUnitario.getStyleClass().add("pv-cart-item-price");

        Label subtotalLabel = new Label();
        subtotalLabel.setPrefWidth(85);
        subtotalLabel.getStyleClass().add("pv-cart-item-price");
        subtotalLabel.setStyle("-fx-text-fill: #ffffff");

        Button eliminarBtn = crearBotonEliminar(item, producto);

            cantidadField.textProperty().addListener((obs, oldVal, newVal) -> {
            actualizarSubtotal(item, producto.getPrecio().doubleValue());
            actualizarTotales();
        });

        item.getChildren().addAll(nombre, cantidadField, precioUnitario, subtotalLabel, eliminarBtn);
        cartContent.getChildren().add(item);
        // long productoId = producto.getId();
        productosEnCarrito.put(producto.getId(), item);  // Guardar referencia

        actualizarSubtotal(item, producto.getPrecio().doubleValue());
        actualizarTotales();
    }
    }
    private Button crearBotonEliminar(Node item, Producto producto) {
        Button eliminarBtn = new Button("X");
        eliminarBtn.setOnAction(e -> {
            cartContent.getChildren().remove(item);
            productosEnCarrito.remove(producto.getId());
            actualizarTotales();
        });

        eliminarBtn.setPrefWidth(50);
        eliminarBtn.setMinWidth(50);
        eliminarBtn.setStyle(
                "-fx-background-color: #ff4d4d;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-cursor: hand;"
        );

        eliminarBtn.setOnMouseEntered(e -> eliminarBtn.setStyle(
                "-fx-background-color: #cc0000;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-cursor: hand;"
        ));

        eliminarBtn.setOnMouseExited(e -> eliminarBtn.setStyle(
                "-fx-background-color: #ff4d4d;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-cursor: hand;"
        ));
        return eliminarBtn;
    }



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
        cartContent.getChildren().clear();
        productosEnCarrito.clear(); // Limpia también el mapa
        actualizarTotales();
    }



    private List<Producto> listaProductos = new ArrayList<>();

    public void procesarVenta() {
        if (cartContent.getChildren().isEmpty()) {
            notificar("Carrito vacío", "Agregá productos antes de procesar la venta.", false);
            return;
        }

        try {
            // Paso 1: Crear venta
            double total = Double.parseDouble(totalLabel.getText().replace(",", "."));

            MedioPago seleccionado = comboMedioPago.getSelectionModel().getSelectedItem();

            if (seleccionado != null) {
                int id = seleccionado.getId();
                String tipo = seleccionado.getTipo();

                System.out.println("ID: " + id + " | Tipo: " + tipo);
                // Usá estos datos en tu lógica, por ejemplo para enviarlos al backend
            } else {
                notificar("Error", "Seleccione un medio de pago.", false);
            }


            String ventaJson = gson.toJson(new Venta(
                    total,
                    Venta.EstadoVenta.pendiente,
                    LocalDateTime.now().toString(),
                    new Usuario(1,"","","","",new Rol()),       // ID hardcodeado, después lo podés hacer dinámico
                    seleccionado//new MedioPago(2,"")      // También configurable




            ));

            HttpRequest crearVentaRequest = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/crearVenta"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(ventaJson))
                    .build();

            HttpResponse<String> ventaResponse = httpClient.send(crearVentaRequest, HttpResponse.BodyHandlers.ofString());

            if (ventaResponse.statusCode() != 200 && ventaResponse.statusCode() != 201) {
                notificar("Error", "No se pudo crear la venta", false);
                return;
            }

            Venta ventaCreada = gson.fromJson(ventaResponse.body(), Venta.class);

            // Paso 2: Enviar los detalles
            for (Node node : cartContent.getChildren()) {
                if (node instanceof HBox item) {
                    Label nombreLabel = (Label) item.getChildren().get(0);
                    TextField cantidadField = (TextField) item.getChildren().get(1);
                    Label precioLabel = (Label) item.getChildren().get(2);

                    String nombre = nombreLabel.getText();
                    int cantidad = Integer.parseInt(cantidadField.getText());
                    double precioUnitario = Double.parseDouble(precioLabel.getText().replace("$", "").trim());

                    Producto producto = buscarProductoPorNombre(nombre);
                    if (producto == null) continue;

                    String detalleJson = gson.toJson(new DetalleVenta(
                            ventaCreada ,
                            cantidad ,
                            precioUnitario ,
                            producto
                    ));

                    HttpRequest detalleRequest = HttpRequest.newBuilder()
                            .uri(URI.create(VariablesEntorno.getServerURL() + "/api/crear-detalle-venta"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(detalleJson))
                            .build();

                    httpClient.send(detalleRequest, HttpResponse.BodyHandlers.ofString());
                }
            }


            // Paso 3: Generar PDF y abrirlo automáticamente

            // Construir lista de detalles para el PDF
            List<DetalleVenta> detalles = new ArrayList<>();
            for (Node node : cartContent.getChildren()) {
                if (node instanceof HBox item) {
                    Label nombreLabel = (Label) item.getChildren().get(0);
                    TextField cantidadField = (TextField) item.getChildren().get(1);
                    Label precioLabel = (Label) item.getChildren().get(2);

                    String nombre = nombreLabel.getText();
                    int cantidad = Integer.parseInt(cantidadField.getText());
                    double precioUnitario = Double.parseDouble(precioLabel.getText().replace("$", "").trim());

                    Producto producto = buscarProductoPorNombre(nombre);
                    if (producto == null) continue;

                    detalles.add(new DetalleVenta(ventaCreada, cantidad, precioUnitario, producto));
                }
            }

            // Crear carpeta Facturas en Documentos si no existe
            String rutaDocumentos = System.getProperty("user.home") + File.separator + "Documents";
            File carpetaFacturas = new File(rutaDocumentos + File.separator + "Facturas");
            if (!carpetaFacturas.exists()) {
                carpetaFacturas.mkdirs();
            }

            // Generar ruta del archivo PDF
            String archivoSalida = carpetaFacturas.getAbsolutePath() + File.separator + "venta_" + ventaCreada.getId() + ".pdf";

            // Generar PDF
            ventaCreada.setMedioPago(seleccionado);
            VentaPDFGenerator generador = new VentaPDFGenerator();
            generador.generarPDF(ventaCreada, detalles, archivoSalida);

            // Abrir el PDF automáticamente con el visor predeterminado del sistema
            Desktop.getDesktop().open(new File(archivoSalida));



            notificar("Venta Procesada", "La venta fue registrada exitosamente.", true);
            limpiarCarrito();

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error", "No se pudo procesar la venta: " + e.getMessage(), false);
        }
        mostrarProductos();
    }

    private void cargarMediosDePago() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/medioPagos"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            MedioPago[] medioPagos = gson.fromJson(responseBody, MedioPago[].class);

            comboMedioPago.getItems().clear();
            comboMedioPago.getItems().addAll(Arrays.asList(medioPagos));

            comboMedioPago.setCellFactory(lv -> new ListCell<MedioPago>() {
                @Override
                protected void updateItem(MedioPago item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getTipo());
                }
            });

            comboMedioPago.setButtonCell(new ListCell<MedioPago>() {
                @Override
                protected void updateItem(MedioPago item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getTipo());
                }
            });

            comboMedioPago.getSelectionModel().selectFirst();

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error Crítico", e.getMessage(), false);
        }
    }

    @FXML
    public void verVentas() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/desktop/historial-ventas.fxml"));

        Parent root = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root, 1600, 900)); // Dimensiones adaptadas para la vista de historial
        stage.setTitle("Historial de Ventas");
        stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana anterior
        stage.setResizable(true); // Permite redimensionar para mejor experiencia
        stage.setMinWidth(1200); // Ancho mínimo para que se vea bien
        stage.setMinHeight(700);  // Alto mínimo para que se vea bien

        // Opcional: Centrar la ventana en pantalla
        stage.centerOnScreen();

        // Opcional: Si querés hacer algo cuando se cierre la ventana
        // stage.setOnCloseRequest(event -> {
        //     // Código para ejecutar al cerrar
        //     System.out.println("Cerrando historial de ventas");
        // });

        stage.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mostrarProductos();
        cargarMediosDePago();
        btnProcesarVenta.setOnAction(onclick -> {procesarVenta();});
        btnLimpiarCarrito.setOnAction(onclick -> {limpiarCarrito();});
        btnHistorialVentas.setOnAction(onclick -> {
            try {
                verVentas();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}