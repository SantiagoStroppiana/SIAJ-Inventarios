package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.model.Producto;
import org.example.desktop.model.Proveedor;
import org.example.desktop.util.VariablesEntorno;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProductoDetalleController {

    private Producto producto;

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    @FXML private TextField txtSku;
    @FXML private TextField txtNombre;
    @FXML private TextField txtStock;
    @FXML private TextField txtPrecio;
    @FXML private Button modificar;
    @FXML private Button guardar;
    @FXML private SplitMenuButton menuProveedor;
    @FXML private SplitMenuButton menuCategorias;
    @FXML private SplitMenuButton menuEstado;
    private Proveedor proveedorSeleccionado = null;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private Boolean estadoSeleccionado = null;




    public void cargarProducto() {
        proveedorSeleccionado = producto.getProveedorid();
        txtSku.setText(producto.getSku());
        txtNombre.setText(producto.getNombre());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtStock.setText(String.valueOf(producto.getStock()));
        menuProveedor.setText(producto.getProveedor());
        menuEstado.setText(producto.getEstado() ? "Activo" : "Inactivo");
        menuProveedor.setDisable(true);
        menuCategorias.setDisable(true);
        menuEstado.setDisable(true);
        mostrarProveedores();
        mostrarEstado();


        txtStock.setEditable(false);
        txtSku.setEditable(false);
        txtNombre.setEditable(false);
        txtPrecio.setEditable(false);

        modificar.setOnAction(event -> {habilitarEdicion();});
        guardar.setOnAction(event -> {modificarProducto();});
    }

    public void habilitarEdicion() {
        if (txtSku.isEditable() && txtNombre.isEditable() && txtStock.isEditable() && txtPrecio.isEditable()) {
         /*   txtStock.setEditable(false);
            txtSku.setEditable(false);
            txtNombre.setEditable(false);
            txtPrecio.setEditable(false); */
        } else {
            txtStock.setEditable(true);
            txtSku.setEditable(true);
            txtNombre.setEditable(true);
            txtPrecio.setEditable(true);
            menuProveedor.setDisable(false);
            menuCategorias.setDisable(false);
            menuEstado.setDisable(false);
        }
    }

    public void inhabilitarEdicion() {
        txtStock.setEditable(false);
        txtSku.setEditable(false);
        txtNombre.setEditable(false);
        txtPrecio.setEditable(false);
        menuProveedor.setDisable(true);
        menuCategorias.setDisable(true);
        menuEstado.setDisable(true);
    }


    public void modificarProducto(){
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
            Producto producto2 = new Producto();
            producto2.setId(producto.getId());
            producto2.setSku(sku);
            producto2.setNombre(nombre);
            producto2.setStock(stock);
            producto2.setPrecio(precio);
            //producto.setCategoria(producto.getCategoria());

            producto2.setActivo(menuEstado.getText().equals("Activo") ? true : false);
            //producto2.setProveedorid(new Proveedor(producto.getProveedorid().getId(), null, null, null, null, true));
            producto2.setProveedorid(proveedorSeleccionado);
            producto2.setImg("");

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

            String json = gson.toJson(producto2);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/modificarProducto"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            System.out.println("Código de estado: " + response.statusCode());
            System.out.println("Respuesta del servidor: " + response.body());
            System.out.println("Datos enviados al servidor: " + json);

            if (responseBody.trim().startsWith("{")) {
                MensajesResultados resultado = gson.fromJson(responseBody, MensajesResultados.class);

                if (resultado.isExito()) {
                    notificar("Producto modificado", resultado.getMensaje(), true);
                    System.out.println("Producto modificado" + resultado.getMensaje());
                    inhabilitarEdicion();
                    // limpiarCampos();

                } else {
                    notificar("Error al modificar producto", resultado.getMensaje(), false);
                }
            } else {
                notificar("Respuesta del servidor incorrecta", responseBody, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error critico", e.getMessage(), false);
        }
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
    /*

                <TextField fx:id="txtSku" layoutX="330.0" layoutY="83.0" prefHeight="38.0" prefWidth="230.0" promptText="SKU" />
                <TextField fx:id="txtNombre" layoutX="330.0" layoutY="129.0" prefHeight="38.0" prefWidth="230.0" promptText="Nombre producto" />
                <TextField fx:id="txtStock" layoutX="330.0" layoutY="174.0" prefHeight="38.0" prefWidth="230.0" promptText="Stock" />
                <TextField fx:id="txtPrecio" layoutX="330.0" layoutY="220.0" prefHeight="38.0" prefWidth="230.0" promptText="Precio" />

     */



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

}
