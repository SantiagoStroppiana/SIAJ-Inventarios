package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.model.Producto;
import org.example.desktop.model.Proveedor;

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

    @FXML
    public void mostrarProductos() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:7000/api/productos"))
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


        mostrarProductos();
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
    @FXML private SplitMenuButton menuProveedor;


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
            Producto producto = new Producto();
            producto.setSku(sku);
            producto.setNombre(nombre);
            producto.setStock(stock);
            producto.setPrecio(precio);
            //producto.setCategoria(producto.getCategoria());
            producto.setActivo(true);
            producto.setProveedorid(new Proveedor(1, null, null, null, null, true));
            producto.setImg(""); // ajustar según tu lógica

            /*
            Producto producto = new Producto();
            producto.setSku(txtSku.getText());
            producto.setNombre(txtNombre.getText());
            producto.setStock(Integer.parseInt(txtStock.getText()));
            producto.setPrecio(BigDecimal.valueOf(Double.parseDouble(txtPrecio.getText())));
            //producto.setCategoria(producto.getCategoria());
            producto.setActivo(true);
            producto.setProveedorid(new Proveedor(1,null,null,null,null,true));
            producto.setImg("");

            */

            String json = gson.toJson(producto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:7000/api/crearProducto"))
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
                    notificar("Procuto creado", resultado.getMensaje(), true);
                   // limpiarCampos();

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

   /*
    private void limpiarCampos() {
        nombre.setText("");
        apellido.setText("");
        email.setText("");
        password.setText("");
    }*/
}