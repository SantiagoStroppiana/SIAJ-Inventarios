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

public class ProductoController implements Initializable {


    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML private TextField txtBuscarProducto;
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> skuColumn;
    @FXML private TableColumn<Producto, String> nombreColumn;
    @FXML private TableColumn<Producto, Integer> stockColumn;
    @FXML private TableColumn<Producto, BigDecimal> precioColumn;
    @FXML private TableColumn<Producto, String> estadoColumn;
    @FXML private TableColumn<Producto, String> proveedorColumn;
    @FXML private Button agregar;
    @FXML private Button actualizar;
    @FXML private Button desactivar;
    @FXML private Button ver;
    @FXML private SplitMenuButton menuCategorias;
    @FXML private SplitMenuButton menuProveedor;
    private Proveedor proveedorSeleccionado = null;
    private Boolean estadoSeleccionado = null;




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

            String [] estados ={"Activo","Inactivo"};
/*
            for (int i = 0 ; i < estados.length; i++) {
                MenuItem item = new MenuItem(estados[i]);
                item.setOnAction(event -> {menuEstado.setText(item.getText());});
                menuEstado.getItems().add(item);
            }
            */

            /*for (String p : estados) {

                MenuItem item = new MenuItem(p);
                item.setOnAction(event -> {menuEstado.setText(p);});
                menuEstado.getItems().add(item);
            }*/

            for (String estado : estados) {
                MenuItem item = new MenuItem(estado);
                item.setOnAction(event -> {
                    menuEstado.setText(estado);
                    // Asigno true si es "Activo", false si es "Inactivo"
                    estadoSeleccionado = estado.equals("Activo");
                });
                menuEstado.getItems().add(item);
            }


        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error Crítico", e.getMessage(), false);
        }





    }
    public void mostrarProveedores() {

        {

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(VariablesEntorno.getServerURL() + "/api/proveedores"))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response);

                String responseBody = response.body();
                System.out.println(responseBody);

                Proveedor[] proveedores = gson.fromJson(responseBody, Proveedor[].class);


                /*
                for (Proveedor p : proveedores) {

                    MenuItem item = new MenuItem(p.getRazonSocial());
                    item.setOnAction(event -> {menuProveedor.setText(p.getRazonSocial());});
                    menuProveedor.getItems().add(item);
                }*/
                for (Proveedor p : proveedores) {
                    MenuItem item = new MenuItem(p.getRazonSocial());
                    item.setOnAction(event -> {
                        menuProveedor.setText(p.getRazonSocial());
                        proveedorSeleccionado = p;  // guardo el proveedor seleccionado
                    });
                    menuProveedor.getItems().add(item);
                }




            } catch (Exception e) {
                e.printStackTrace();
                notificar("Error Crítico", e.getMessage(), false);
            }





        }
    }

    public void mostrarCategorias() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/categorias"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);

            String responseBody = response.body();
            System.out.println(responseBody);

            Categoria[] categorias = gson.fromJson(responseBody, Categoria[].class);

            for (Categoria p : categorias) {
                System.out.println("Categoria: " + p.getNombre());
                MenuItem item = new MenuItem(p.getNombre());
                item.setOnAction(event -> {menuCategorias.setText(p.getNombre());});
                menuCategorias.getItems().add(item);
            }


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
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        proveedorColumn.setCellValueFactory(cellData -> {
            Producto producto = cellData.getValue();
            return new SimpleStringProperty(
                    producto.getProveedorid() != null ? producto.getProveedorid().getRazonSocial() : "Sin proveedor"
            );
        });

        agregar.setOnAction(event -> crearProducto());
        desactivar.setOnAction(event -> cambiarEstado());
        actualizar.setOnAction(event -> actualizarProductos());
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
    @FXML private SplitMenuButton menuCategoria;
    @FXML private SplitMenuButton menuEstado;
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

            Stage stage = new Stage(); // Esto NO usa StageManager
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
            Producto producto = new Producto();
            producto.setSku(sku);
            producto.setNombre(nombre);
            producto.setStock(stock);
            producto.setPrecio(precio);
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


    }
