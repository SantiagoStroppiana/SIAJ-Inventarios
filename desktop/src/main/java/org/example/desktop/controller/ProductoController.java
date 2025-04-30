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
import org.example.desktop.model.Producto;

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


    @FXML
    public void mostrarProductos() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:7070/api/productos"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

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



}