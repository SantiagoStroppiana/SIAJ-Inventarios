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
import org.example.desktop.model.Categoria;
import org.example.desktop.model.Proveedor;
import org.example.desktop.util.VariablesEntorno;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CategoriaDetalleController {

    private Categoria categoria;

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private Button modificar;
    @FXML private Button guardar;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private Boolean estadoSeleccionado = null;




    public void cargarCategoria() {


        txtNombre.setText(categoria.getNombre());
        txtDescripcion.setText(categoria.getDescripcion());


        // TextFields
        txtNombre.setEditable(false);
        txtNombre.setDisable(true);
        txtNombre.setMouseTransparent(true);
        txtNombre.setFocusTraversable(false);

        // TextFields
        txtDescripcion.setEditable(false);
        txtDescripcion.setDisable(true);
        txtDescripcion.setMouseTransparent(true);
        txtDescripcion.setFocusTraversable(false);



        modificar.setOnAction(event -> {habilitarEdicion();});
        guardar.setOnAction(event -> {modificarCategoria();});
    }

    public void habilitarEdicion() {
        if (txtNombre.isEditable() && txtDescripcion.isEditable()) {

        } else {
            // TextFields
            txtNombre.setEditable(true);
            txtNombre.setDisable(false);
            txtNombre.setMouseTransparent(false);
            txtNombre.setFocusTraversable(true);

            // TextFields
            txtDescripcion.setEditable(true);
            txtDescripcion.setDisable(false);
            txtDescripcion.setMouseTransparent(false);
            txtDescripcion.setFocusTraversable(true);
        }
    }

    public void inhabilitarEdicion() {
        // TextFields
        txtNombre.setEditable(false);
        txtNombre.setDisable(true);
        txtNombre.setMouseTransparent(true);
        txtNombre.setFocusTraversable(false);

        // TextFields
        txtDescripcion.setEditable(false);
        txtDescripcion.setDisable(true);
        txtDescripcion.setMouseTransparent(true);
        txtDescripcion.setFocusTraversable(false);
    }


    public void modificarCategoria(){
        try {

            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            Categoria categoria2 = new Categoria();
            categoria2.setId(categoria.getId());
            categoria2.setNombre(nombre);
            categoria2.setDescripcion(descripcion);

            String json = gson.toJson(categoria2);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/modificarCategoria"))
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
                    notificar("Categoria modificado", resultado.getMensaje(), true);
                    System.out.println("Categoria modificado" + resultado.getMensaje());
                    inhabilitarEdicion();
                    // limpiarCampos();

                } else {
                    notificar("Error al modificar categoria", resultado.getMensaje(), false);
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
    public void mostrarEstado() {

        try {

            String [] estados ={"Activo","Inactivo"};

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





    }*/

}
