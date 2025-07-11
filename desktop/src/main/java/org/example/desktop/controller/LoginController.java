package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.dto.UsuarioDTO;
import org.example.desktop.model.LoginResponse;
import org.example.desktop.util.StageManager;
import org.example.desktop.util.UserSession;
import org.example.desktop.util.VariablesEntorno;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginController {

    @FXML
    private TextField email;
    @FXML
    private TextField password;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML
    public void olvidePassword(ActionEvent event) {
        StageManager.loadScene("/org/example/desktop/cambiar-password-view.fxml", 700, 500);
    }

    @FXML
    public void iniciarSesion(javafx.event.ActionEvent actionEvent) {

        if (email.getText().isEmpty() || password.getText().isEmpty()) {
            notificar("Error", "Por favor, complete todos los campos", false);
            return;
        }

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setEmail(email.getText());
        usuarioDTO.setPassword(password.getText());

        new Thread(() -> {
            try {
                String json = gson.toJson(usuarioDTO);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(VariablesEntorno.getServerURL() + "/api/login"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String responseBody = response.body();

                Platform.runLater(() -> {
                    try {
                        if (responseBody.trim().startsWith("{")) {
                            LoginResponse resultado = gson.fromJson(responseBody, LoginResponse.class);

                            if (resultado.isSuccess()) {
                                UsuarioDTO usuarioLogueado = resultado.getUsuario();
                                UserSession.iniciarSesion(usuarioLogueado);

                                System.out.println("JSON de respuesta LOGIN: " + responseBody);

                                notificar("Iniciar sesión exitoso", resultado.getMessage(), true);
                                StageManager.loadScene("/org/example/desktop/menu-view.fxml", 1600, 900);
                            } else {
                                notificar("Incorrecto", resultado.getMessage(), false);
                            }

                        } else {
                            notificar("Error", responseBody, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        notificar("Error", "Error al procesar la respuesta: " + e.getMessage(), false);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                final String errorMsg = e.getMessage();
                Platform.runLater(() -> {
                    notificar("Error Crítico", errorMsg, false);
                });
            }
        }).start();
    }

    private void notificar(String titulo, String mensaje, boolean exito) {
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

    public void irARegistro(javafx.event.ActionEvent actionEvent) {
        try {
            StageManager.loadScene("/org/example/desktop/register-view.fxml", 700, 650);
        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error", "No se pudo cargar la pantalla de registro: " + e.getMessage(), false);
        }
    }
}
