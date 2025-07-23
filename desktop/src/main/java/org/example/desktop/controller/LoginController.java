package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.example.desktop.dto.UsuarioDTO;
import org.example.desktop.model.LoginResponse;
import org.example.desktop.util.StageManager;
import org.example.desktop.util.UserSession;
import org.example.desktop.util.VariablesEntorno;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.example.desktop.util.NotificationManager.notificarError;
import static org.example.desktop.util.NotificationManager.notificarExito;

public class LoginController {

    @FXML
    private TextField email;
    @FXML
    private TextField password;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML
    public void olvidePassword(ActionEvent event) {
        StageManager.loadScene("/org/example/desktop/cambiar-password-view.fxml", 900, 600);
    }

    @FXML
    public void iniciarSesion(javafx.event.ActionEvent actionEvent) {

        if (email.getText().isEmpty() || password.getText().isEmpty()) {
            notificarError("Por favor, complete todos los campos");
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
                                notificarExito("Has iniciado sesión correctamente. Redirigiendo al dashboard...");
                                StageManager.loadScene("/org/example/desktop/menu-view.fxml", 1600, 900);
                            } else {
                                notificarError("Error de autenticación, verifique los campos");
                            }

                        } else {
                            notificarError("Error de autenticación");

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        notificarError("Error al procesar la respuesta: " + e.getMessage());

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                final String errorMsg = e.getMessage();
                Platform.runLater(() -> {
                    notificarError("Error critico: " + errorMsg);
                });
            }
        }).start();
    }

    public void irARegistro(javafx.event.ActionEvent actionEvent) {
        try {
            StageManager.loadScene("/org/example/desktop/register-view.fxml", 900, 650);
        } catch (Exception e) {
            e.printStackTrace();
            notificarError("No se pudo cargar la pantalla de registro: " + e.getMessage());
        }
    }
}
