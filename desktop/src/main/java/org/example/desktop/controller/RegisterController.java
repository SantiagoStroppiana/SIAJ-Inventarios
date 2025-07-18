package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.dto.UsuarioDTO;
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.model.Usuario;
import org.example.desktop.util.StageManager;
import org.example.desktop.util.UserSession;
import org.example.desktop.util.VariablesEntorno;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class RegisterController {

    @FXML
    private TextField nombre;
    @FXML
    private TextField apellido;
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private Label lblCuenta;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnVolver;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {

        UsuarioDTO usuarioDTO = UserSession.getUsuarioActual();
        if(usuarioDTO != null && usuarioDTO.getNombreRol().equals("Administrador")) {
            btnLogin.setVisible(false);
            btnLogin.setManaged(false);
        }else{
            btnLogin.setVisible(false);
            btnLogin.setManaged(true);
        }

    }

    @FXML
    public void volverAtras(ActionEvent event) {
        if(UserSession.getUsuarioActual() != null) {
            StageManager.loadScene("/org/example/desktop/usuarios-view.fxml", 1600, 900);
        }else{
            StageManager.loadScene("/org/example/desktop/login-view.fxml", 700, 500);
        }
    }

    @FXML
    public void registrarse(javafx.event.ActionEvent actionEvent) {

        if(email.getText().isEmpty() || nombre.getText().isEmpty() || apellido.getText().isEmpty() || password.getText().isEmpty()) {
            notificar("Error", "Por favor, complete todos los campos", false);
            return;
        }



        new Thread(() -> {

            try {
                Usuario usuario = new Usuario();
                usuario.setNombre(nombre.getText());
                usuario.setApellido(apellido.getText());
                usuario.setEmail(email.getText());
                usuario.setPassword(password.getText());

                String json = gson.toJson(usuario);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(VariablesEntorno.getServerURL() + "/api/register"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String responseBody = response.body();

                Platform.runLater(() -> {
                    try{
                        if (responseBody.trim().startsWith("{")) {
                            MensajesResultados resultado = gson.fromJson(responseBody, MensajesResultados.class);

                            if (resultado.isExito()) {
                                notificar("Registro exitoso", resultado.getMensaje(), true);
                                limpiarCampos();
                                UsuarioDTO usuarioDTO = UserSession.getUsuarioActual();

                                if (usuarioDTO != null) {
                                    StageManager.loadScene("/org/example/desktop/usuarios-view.fxml", 1600, 900);
                                }else{
                                    irALogin(actionEvent);
                                }

                            } else {
                                notificar("Error al registrarse", resultado.getMensaje(), false);
                            }
                        } else {
                            notificar("Respuesta del servidor incorrecta", responseBody, false);
                        }


                    } catch (Exception e){
                        e.printStackTrace();
                        notificar("Error", "Error al registrar el usuario", false);
                    }



                });


            } catch (Exception e) {
                e.printStackTrace();
                notificar("Error critico", e.getMessage(), false);
            }

        }).start();

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
            }else{
                notificacion.showError();
            }
        });
    }

    private void limpiarCampos() {
        nombre.setText("");
        apellido.setText("");
        email.setText("");
        password.setText("");
    }

    public void irALogin(javafx.event.ActionEvent actionEvent) {
        try{
            StageManager.loadScene("/org/example/desktop/login-view.fxml", 700, 500);
        }catch (Exception e){
            e.printStackTrace();
            notificar("Error", "No se pudo cargar la pantalla de productos: " + e.getMessage(), false);
        }
    }

}
