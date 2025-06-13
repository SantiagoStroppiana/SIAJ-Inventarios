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
import org.example.desktop.model.Proveedor;
import org.example.desktop.model.Proveedor;
import org.example.desktop.util.VariablesEntorno;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProveedorDetalleController {

    private Proveedor proveedor;

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
    @FXML private TextField txtRazonSocial;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private Button modificar;
    @FXML private Button guardar;
    @FXML private SplitMenuButton menuEstado;
    private Proveedor proveedorSeleccionado = null;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private Boolean estadoSeleccionado = null;




    public void cargarProveedor() {


        txtRazonSocial.setText(proveedor.getRazonSocial());
        txtDireccion.setText(proveedor.getDireccion());
        txtTelefono.setText(proveedor.getTelefono());
        txtEmail.setText(proveedor.getEmail());
        menuEstado.setText(proveedor.getEstado() ? "Activo" : "Inactivo");


        // TextFields
        txtRazonSocial.setEditable(false);
        txtRazonSocial.setDisable(true);
        txtRazonSocial.setMouseTransparent(true);
        txtRazonSocial.setFocusTraversable(false);

        txtDireccion.setEditable(false);
        txtDireccion.setDisable(true);
        txtDireccion.setMouseTransparent(true);
        txtDireccion.setFocusTraversable(false);

        txtTelefono.setEditable(false);
        txtTelefono.setDisable(true);
        txtTelefono.setMouseTransparent(true);
        txtTelefono.setFocusTraversable(false);

        txtEmail.setEditable(false);
        txtEmail.setDisable(true);
        txtEmail.setMouseTransparent(true);
        txtEmail.setFocusTraversable(false);

        // SplitMenuButton
        menuEstado.setDisable(true);
        menuEstado.setMouseTransparent(true);
        menuEstado.setFocusTraversable(false);


        mostrarProveedores();
        mostrarEstado();
        

        modificar.setOnAction(event -> {habilitarEdicion();});
        guardar.setOnAction(event -> {modificarProveedor();});
    }

    public void habilitarEdicion() {
        if (txtRazonSocial.isEditable() && txtDireccion.isEditable() && txtTelefono.isEditable() && txtEmail.isEditable()) {
         /*   txtTelefono.setEditable(false);
            txtRazonSocial.setEditable(false);
            txtDireccion.setEditable(false);
            txtEmail.setEditable(false); */
        } else {
// TextFields
            txtRazonSocial.setEditable(true);
            txtRazonSocial.setDisable(false);
            txtRazonSocial.setMouseTransparent(false);
            txtRazonSocial.setFocusTraversable(true);

            txtDireccion.setEditable(true);
            txtDireccion.setDisable(false);
            txtDireccion.setMouseTransparent(false);
            txtDireccion.setFocusTraversable(true);

            txtTelefono.setEditable(true);
            txtTelefono.setDisable(false);
            txtTelefono.setMouseTransparent(false);
            txtTelefono.setFocusTraversable(true);

            txtEmail.setEditable(true);
            txtEmail.setDisable(false);
            txtEmail.setMouseTransparent(false);
            txtEmail.setFocusTraversable(true);

// SplitMenuButton
            menuEstado.setDisable(false);
            menuEstado.setMouseTransparent(false);
            menuEstado.setFocusTraversable(true);

        }
    }

    public void inhabilitarEdicion() {
// TextFields
        txtRazonSocial.setEditable(false);
        txtRazonSocial.setDisable(true);
        txtRazonSocial.setMouseTransparent(true);
        txtRazonSocial.setFocusTraversable(false);

        txtDireccion.setEditable(false);
        txtDireccion.setDisable(true);
        txtDireccion.setMouseTransparent(true);
        txtDireccion.setFocusTraversable(false);

        txtTelefono.setEditable(false);
        txtTelefono.setDisable(true);
        txtTelefono.setMouseTransparent(true);
        txtTelefono.setFocusTraversable(false);

        txtEmail.setEditable(false);
        txtEmail.setDisable(true);
        txtEmail.setMouseTransparent(true);
        txtEmail.setFocusTraversable(false);

// SplitMenuButton
        menuEstado.setDisable(true);
        menuEstado.setMouseTransparent(true);
        menuEstado.setFocusTraversable(false);

    }


    public void modificarProveedor(){
        try {


            String razonSocial = txtRazonSocial.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();


            if (razonSocial.isEmpty() || direccion.isEmpty() ||  telefono.isEmpty() || email.isEmpty()) {
                notificar("Campos incompletos", "Todos los campos son obligatorios.", false);
                return;
            }


            Proveedor proveedor2 = proveedor;

            proveedor2.setRazonSocial(razonSocial);
            proveedor2.setDireccion(direccion);
            proveedor2.setTelefono(telefono);
            proveedor2.setEmail(email);

            proveedor2.setActivo(menuEstado.getText().equals("Activo") ? true : false);


            String json = gson.toJson(proveedor2);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/modificarProveedor"))
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
                    notificar("Proveedor modificado", resultado.getMensaje(), true);
                    System.out.println("Proveedor modificado" + resultado.getMensaje());
                    inhabilitarEdicion();
                    // limpiarCampos();

                } else {
                    notificar("Error al modificar proveedor", resultado.getMensaje(), false);
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

                <TextField fx:id="txtRazonSocial" layoutX="330.0" layoutY="83.0" prefHeight="38.0" prefWidth="230.0" promptText="SKU" />
                <TextField fx:id="txtDireccion" layoutX="330.0" layoutY="129.0" prefHeight="38.0" prefWidth="230.0" promptText="Nombre proveedor" />
                <TextField fx:id="txtTelefono" layoutX="330.0" layoutY="174.0" prefHeight="38.0" prefWidth="230.0" promptText="Stock" />
                <TextField fx:id="txtEmail" layoutX="330.0" layoutY="220.0" prefHeight="38.0" prefWidth="230.0" promptText="Precio" />

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
