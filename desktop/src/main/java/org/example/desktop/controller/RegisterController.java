package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.dto.UsuarioDTO;
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.model.Usuario;
import org.example.desktop.util.StageManager;
import org.example.desktop.util.UserSession;
import org.example.desktop.util.VariablesEntorno;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.example.desktop.util.NotificationManager.notificarError;
import static org.example.desktop.util.NotificationManager.notificarExito;


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
            btnVolver.setManaged(true);
            btnVolver.setVisible(true);
        }else{
            btnLogin.setVisible(true);
            btnLogin.setManaged(true);
            btnVolver.setManaged(false);
            btnVolver.setVisible(false);
        }

    }

    @FXML
    public void volverAtras(ActionEvent event) {
        if(UserSession.getUsuarioActual() != null) {
            StageManager.loadScene("/org/example/desktop/usuarios-view.fxml", 1600, 900);
        }else{
            StageManager.loadScene("/org/example/desktop/login-view.fxml", 900, 600);
        }
    }

    @FXML
    public void registrarse(javafx.event.ActionEvent actionEvent) {

        if(email.getText().isEmpty() || nombre.getText().isEmpty() || apellido.getText().isEmpty() || password.getText().isEmpty()) {
            notificarError("Por favor, complete todos los campos");
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
                                notificarExito(resultado.getMensaje());
                                limpiarCampos();
                                UsuarioDTO usuarioDTO = UserSession.getUsuarioActual();

                                if (usuarioDTO != null) {
                                    StageManager.loadScene("/org/example/desktop/usuarios-view.fxml", 1600, 900);
                                }else{
                                    irALogin(actionEvent);
                                }

                            } else {
                                notificarError(resultado.getMensaje());
                            }
                        } else {
                            notificarError("Error respuesta el servidor");
                        }


                    } catch (Exception e){
                        e.printStackTrace();
                        notificarError("Error critico al registrar" + e.getMessage());
                    }

                });


            } catch (Exception e) {
                e.printStackTrace();
                notificarError("Error critico " + e.getMessage());
            }

        }).start();

    }

    private void limpiarCampos() {
        nombre.setText("");
        apellido.setText("");
        email.setText("");
        password.setText("");
    }

    public void irALogin(javafx.event.ActionEvent actionEvent) {
        try{
            StageManager.loadScene("/org/example/desktop/login-view.fxml", 900, 600);
        }catch (Exception e){
            e.printStackTrace();
            notificarError("No se pudo cargar la pantalla de iniciar sesion");
        }
    }

}
