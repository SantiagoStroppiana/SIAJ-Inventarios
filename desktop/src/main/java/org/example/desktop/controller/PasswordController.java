package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.dto.UsuarioDTO;
import org.example.desktop.dto.UsuarioForgetPasswordDTO;
import org.example.desktop.dto.UsuarioPasswordDTO;
import org.example.desktop.util.StageManager;
import org.example.desktop.util.UserSession;
import org.example.desktop.util.VariablesEntorno;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PasswordController {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML private VBox olvideBox;
    @FXML private VBox cambiarBox;

    @FXML private TextField email;
    @FXML private PasswordField nuevaPassword;
    @FXML private PasswordField confirmarPassword;

    @FXML private PasswordField oldPassword;
    @FXML private PasswordField newPassword;

    @FXML private Button btnCambiarPassword;
    @FXML private Button btnOlvidePassword;

    @FXML
    public void initialize() {
        boolean logueado = UserSession.getUsuarioActual() != null;

        olvideBox.setVisible(!logueado);
        olvideBox.setManaged(!logueado);

        cambiarBox.setVisible(logueado);
        cambiarBox.setManaged(logueado);
    }

    @FXML
    public void cambiarPassword(ActionEvent event) {
        UsuarioDTO usuario = UserSession.getUsuarioActual();

        if (usuario != null) {
            String oldPass = oldPassword.getText();
            String newPass = newPassword.getText();

            if (oldPass.isBlank() || newPass.isBlank()) {
                notificar("Campos vacíos", "Debes completar ambos campos de contraseña.", false);
                return;
            }

            try {
                UsuarioPasswordDTO dto = new UsuarioPasswordDTO();
                dto.setId(usuario.getId());
                dto.setOldPassword(oldPass);
                dto.setNewPassword(newPass);

                String json = gson.toJson(dto);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(VariablesEntorno.getServerURL() + "/api/cambiarPassword"))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    notificar("Éxito", "Contraseña cambiada con éxito.", true);
                } else {
                    notificar("Error", "No se pudo cambiar la contraseña.", false);
                }

            } catch (Exception e) {
                notificar("Error", "Hubo un problema con la solicitud.", false);
            }
        }
    }

    @FXML
    public void olvidePassword(ActionEvent event) {
        String correo = email.getText();
        String nueva = nuevaPassword.getText();
        String repetir = confirmarPassword.getText();

        if (correo.isBlank() || nueva.isBlank() || repetir.isBlank()) {
            notificar("Campos vacíos", "Debes completar todos los campos.", false);
            return;
        }

        if (!nueva.equals(repetir)) {
            notificar("Error", "Las contraseñas no coinciden.", false);
            return;
        }

        try {
            UsuarioForgetPasswordDTO dto = new UsuarioForgetPasswordDTO();
            dto.setEmail(correo);
            dto.setNewPassword(nueva);
            dto.setConfirmPassword(repetir);

            String json = gson.toJson(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/olvidePassword"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                notificar("Éxito", "Contraseña actualizada correctamente.", true);
            } else {
                notificar("Error", "No se pudo actualizar la contraseña.", false);
            }

        } catch (Exception e) {
            notificar("Error", "Hubo un problema con la solicitud.", false);
        }
    }

    @FXML
    public void volver(ActionEvent event) {
        if (UserSession.getUsuarioActual() != null) {
            StageManager.loadScene("/org/example/desktop/menu-view.fxml", 1600, 900);
        } else {
            StageManager.loadScene("/org/example/desktop/login-view.fxml", 700, 500);
        }
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
}
