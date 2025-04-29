package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.model.Usuario;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProductoController {
    //sku, nombre, categoria, stock, activo, precio y proveedor

    @FXML
    private Label sku;
    @FXML
    private Label nombre;
    @FXML
    private Label categoria;
    @FXML
    private Label stock;
    @FXML
    private Label activo;
    @FXML
    private Label precio;
    @FXML
    private Label proveedor;


    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML
    public void mostrarProductos(javafx.event.ActionEvent actionEvent) {

        try{

            String json = gson.toJson(producto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:7070/api/productos"))
                    .header("Content-Type", "application/json")
                    .GET(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            Producto producto = new Producto();
            sku.setText(producto.getSku());
            nombre.setText(producto.getNombre());
            categoria.setText(producto.getCategoria());
            stock.setText(producto.getStock());
            activo.setText(producto.getActivo());
            precio.setText(producto.getPrecio());
            proveedor.setText(producto.getProveedor());

        }catch (Exception e){
            e.printStackTrace();
            notificar("Error Critico", e.getMessage(), false);
        }
    }


}