package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.HelloApplication;
import org.example.desktop.model.Categoria;
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.model.Producto;
import org.example.desktop.model.Proveedor;
import org.example.desktop.util.VariablesEntorno;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductoController implements Initializable {


    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML private TextField txtBuscarProducto;
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> skuColumn;
    @FXML private TableColumn<Producto, String> nombreColumn;
    @FXML private TableColumn<Producto, Integer> stockColumn;
    @FXML private TableColumn<Producto, BigDecimal> precioColumn;
    @FXML private TableColumn<Producto, BigDecimal> precioCostoColumn;
    @FXML private TableColumn<Producto, String> estadoColumn;
    @FXML private TableColumn<Producto, String> proveedorColumn;
    @FXML private Button agregar;
    @FXML private Button actualizar;
    @FXML private Button desactivar;
    @FXML private Button ver;
    @FXML private Button categorias;
    @FXML private ComboBox<String> menuCategorias;
    @FXML private ComboBox<String> menuEstado;
    @FXML private ComboBox<Proveedor> menuProveedor;
    private Proveedor proveedorSeleccionado = null;
    private Boolean estadoSeleccionado = null;
    private static final Logger logger = Logger.getLogger(ProductoController.class.getName());




    @FXML
    public void mostrarProductos() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/productos"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);

            String responseBody = response.body();
            System.out.println(responseBody);

            Producto[] productos = gson.fromJson(responseBody, Producto[].class);

            tablaProductos.getItems().clear();
            tablaProductos.getItems().addAll(productos);

            for (Producto p : productos) {
                System.out.println("Producto: " + p.getNombre() + ", SKU: " + p.getSku());
            }

            System.out.println("Respuesta del backend:");
            System.out.println(responseBody);

            for (Producto p : productos) {
                System.out.println("Producto: " + p.getNombre() + ", Proveedor: " +
                        (p.getProveedorid() != null ? p.getProveedorid().getRazonSocial() : "null"));
            }


        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error Crítico", e.getMessage(), false);
        }

    }


    public void mostrarEstado() {
        try {
            String[] estados = { "Activo", "Inactivo" };

            menuEstado.getItems().clear();
            for (String estado : estados) {
                menuEstado.getItems().add(estado);
            }

            menuEstado.getSelectionModel().clearSelection(); // No seleccionar por defecto
            menuEstado.setPromptText("Estado"); // Mostrar texto inicial

            menuEstado.setOnAction(event -> {
                Object selected = menuEstado.getValue();
                if (selected instanceof String selectedText) {
                    estadoSeleccionado = selectedText.equals("Activo");
                }
            });

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en mostrarEstado", e);
            notificar("Error Crítico", e.getMessage(), false);
        }
    }

    public void mostrarProveedores() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/proveedores"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            Proveedor[] proveedores = gson.fromJson(responseBody, Proveedor[].class);

            menuProveedor.getItems().clear();
            menuProveedor.getItems().addAll(proveedores);
            menuProveedor.getSelectionModel().clearSelection(); // No seleccionar por defecto
            menuProveedor.setPromptText("Proveedor");

            menuProveedor.setCellFactory(lv -> new ListCell<Proveedor>() {
                @Override
                protected void updateItem(Proveedor item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getRazonSocial());
                }
            });
            menuProveedor.setButtonCell(new ListCell<Proveedor>() {
                @Override
                protected void updateItem(Proveedor item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getRazonSocial());
                }
            });

            menuProveedor.setOnAction(event -> {
                Proveedor seleccionado = menuProveedor.getValue();
                proveedorSeleccionado = seleccionado;
            });

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al mostrar proveedores", e);
            notificar("Error Crítico", e.getMessage(), false);
        }
    }

    public void mostrarCategorias() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/categorias"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            Categoria[] categorias = gson.fromJson(responseBody, Categoria[].class);

            menuCategorias.getItems().clear();
            menuCategorias.setPromptText("Categoría"); // Texto inicial

            for (Categoria categoria : categorias) {
                menuCategorias.getItems().add(categoria.getNombre());
            }

            menuCategorias.getSelectionModel().clearSelection(); // No seleccionar por defecto

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error Crítico", e.getMessage(), false);
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio"));
        precioCostoColumn.setCellValueFactory(new PropertyValueFactory<>("precioCosto"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        proveedorColumn.setCellValueFactory(cellData -> {
            Producto producto = cellData.getValue();
            return new SimpleStringProperty(
                    producto.getProveedorid() != null ? producto.getProveedorid().getRazonSocial() : "Sin proveedor"
            );
        });

        setupKeyboardNavigation();


        agregar.setOnAction(event -> crearProducto());
        desactivar.setOnAction(event -> cambiarEstado());
        actualizar.setOnAction(event -> actualizarProductos());
        categorias.setOnAction(event -> {
            try {
                verCategorias();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        ver.setOnAction(event -> {
            try {
                verProducto();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        mostrarProductos();
        mostrarCategorias();
        mostrarEstado();
        mostrarProveedores();
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


    @FXML private TextField txtSku;
    @FXML private TextField txtNombre;
    @FXML private TextField txtStock;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtPrecioCosto;
 //   @FXML private SplitMenuButton menuProveedor;


    public void actualizarProductos() {
        mostrarProductos(); // refrescar la tabla
    }


    private Stage stage;
    private HelloApplication application;

    public void setMainStage(Stage stage) {
        this.stage = stage;
    }

    public void setApplication(HelloApplication application) {
        this.application = application;
    }
    @FXML
    public void verProducto() throws IOException {
        Producto producto = tablaProductos.getSelectionModel().getSelectedItem();

        if (producto == null) {
            notificar("Seleccionar producto", "Debe seleccionar un producto en la tabla.", false);
        } else {


            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/desktop/producto-detalle-view.fxml"));


            Parent root = fxmlLoader.load();

            ProductoDetalleController controller = fxmlLoader.getController();
            controller.setProducto(producto);
            controller.cargarProducto();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 800, 550));
            stage.setTitle("Detalle de Producto");
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana anterior si querés
            stage.setOnCloseRequest(event -> {mostrarProductos();});
            stage.showAndWait();

        }

    }

    @FXML
    public void cambiarEstado() {
        Producto producto = tablaProductos.getSelectionModel().getSelectedItem();

        if (producto == null) {
            notificar("Seleccionar producto", "Debe seleccionar un producto en la tabla.", false);
            return;
        }

        try {
            producto.setActivo(!producto.isActivo()); // cambiar estado

            String json = gson.toJson(producto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/modificarProducto"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();

            if (responseBody.trim().startsWith("{")) {
                MensajesResultados resultado = gson.fromJson(responseBody, MensajesResultados.class);
                if (resultado.isExito()) {
                    notificar("Producto modificado", resultado.getMensaje(), true);
                } else {
                    notificar("Error al modificar producto", resultado.getMensaje(), false);
                }
            } else {
                notificar("Respuesta del servidor incorrecta", responseBody, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error crítico", e.getMessage(), false);
        }

        mostrarProductos(); // refrescar la tabla
    }

    @FXML
    public void crearProducto() {
        try {


            String sku = txtSku.getText().trim();
            String nombre = txtNombre.getText().trim();
            String stockStr = txtStock.getText().trim();
            String precioStr = txtPrecio.getText().trim();
            String precioCostoStr = txtPrecioCosto.getText().trim();


            if (sku.isEmpty() || nombre.isEmpty() ||  stockStr.isEmpty() || precioStr.isEmpty()) {
                notificar("Campos incompletos", "Todos los campos son obligatorios.", false);
                return;
            }


            int stock;
            try {
                stock = Integer.parseInt(stockStr);
                if (stock < 0) {
                    notificar("Stock inválido", "El stock no puede ser negativo.", false);
                    return;
                }
            } catch (NumberFormatException e) {
                notificar("Error de formato", "El stock debe ser un número entero.", false);
                return;
            }

            BigDecimal precio;
            BigDecimal precioCosto;
            try {
                double precioDouble = Double.parseDouble(precioStr);
                if (precioDouble < 0) {
                    notificar("Precio inválido", "El precio no puede ser negativo.", false);
                    return;
                }
                precio = BigDecimal.valueOf(precioDouble);
            } catch (NumberFormatException e) {
                notificar("Error de formato", "El precio debe ser un número válido.", false);
                return;
            }

            if (proveedorSeleccionado == null) {
                notificar("Error", "Debe seleccionar un proveedor.", false);
                return;
            }
            if (estadoSeleccionado == null) {
                notificar("Error", "Debe seleccionar un estado.", false);
                return;
            }

            double precioDoubleCosto = Double.parseDouble(precioCostoStr);
            precioCosto = BigDecimal.valueOf(precioDoubleCosto);

            Producto producto = new Producto();
            producto.setSku(sku);
            producto.setNombre(nombre);
            producto.setStock(stock);
            producto.setPrecio(precio);
            producto.setPrecioCosto(precioCosto);
            producto.setActivo(estadoSeleccionado);
            producto.setProveedorid(proveedorSeleccionado);
            producto.setImg("");
            producto.setFecha_alta(System.currentTimeMillis());

            String json = gson.toJson(producto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/crearProducto"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            System.out.println("Código de estado: " + response.statusCode());
            System.out.println("Respuesta del servidor: " + response.body());
            System.out.println("Datos enviados al servidor: " + json);

            if (responseBody.trim().startsWith("{")) {
                MensajesResultados resultado = gson.fromJson(responseBody, MensajesResultados.class);

                if (resultado.isExito()) {
                    // Agregar el producto directamente a la tabla

                  /*  Thread.sleep(10000);
                    mostrarProductos();
*/
                    mostrarProductos();

                    notificar("Producto creado", resultado.getMensaje(), true);
                    // limpiarCampos(); // si tenés esta función activa
                } else {
                    notificar("Error crear producto", resultado.getMensaje(), false);
                }

            } else {
                notificar("Respuesta del servidor incorrecta", responseBody, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error critico", e.getMessage(), false);
        }
    }
    private void setupKeyboardNavigation() {
        // Configurar navegación con Enter en campos de texto
        txtSku.setOnAction(e -> txtNombre.requestFocus());
        txtNombre.setOnAction(e -> txtStock.requestFocus());
        txtStock.setOnAction(e -> txtPrecio.requestFocus());
        txtPrecio.setOnAction(e -> menuCategorias.requestFocus());

        // Para ComboBox, usar setOnKeyPressed
        menuCategorias.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.TAB) || e.getCode().equals(KeyCode.ENTER)) {
                menuEstado.requestFocus();
            }
        });

        menuEstado.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.TAB) || e.getCode().equals(KeyCode.ENTER)) {
                menuProveedor.requestFocus();
            }
        });

        menuProveedor.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.TAB) || e.getCode().equals(KeyCode.ENTER)) {
                agregar.requestFocus();
            }
        });

        // Configurar navegación entre botones
        agregar.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.TAB)) {
                ver.requestFocus();
            }
        });

        ver.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.TAB)) {
                actualizar.requestFocus();
            }
        });

        actualizar.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.TAB)) {
                desactivar.requestFocus();
            }
        });

        desactivar.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.TAB)) {
                txtBuscarProducto.requestFocus();
            }
        });

        txtBuscarProducto.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.TAB)) {
                tablaProductos.requestFocus();
            }
        });
    }


    @FXML
    public void verCategorias() throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/desktop/categorias-view.fxml"));


            Parent root = fxmlLoader.load();



            Stage stage = new Stage(); // Esto NO usa StageManager
            stage.setScene(new Scene(root, 800, 550));
            stage.setTitle("Detalle de Categorias");
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana anterior si querés
            //stage.setOnCloseRequest(event -> {mostrarProductos();});
            stage.showAndWait();



    }

    }
