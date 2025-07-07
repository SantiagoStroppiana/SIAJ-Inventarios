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

    @FXML
    private TextField oldPassword;
    @FXML
    private TextField newPassword;

    @FXML
    public void olvidePassword(ActionEvent event) {
        UsuarioDTO usuario = UserSession.getUsuarioActual();

        if (usuario != null) {
            String oldPass = oldPassword.getText();
            String newPass = newPassword.getText();

            if (oldPass.isEmpty() || newPass.isEmpty()) {
                notificar("Campos vacíos", "Debes completar ambos campos de contraseña.", false);
                return;
            }

            try {
                UsuarioPasswordDTO usuarioPasswordDTO = new UsuarioPasswordDTO();
                usuarioPasswordDTO.setId(usuario.getId());
                usuarioPasswordDTO.setOldPassword(oldPass);
                usuarioPasswordDTO.setNewPassword(newPass);

                String json = gson.toJson(usuarioPasswordDTO);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(VariablesEntorno.getServerURL() + "/api/cambiarPassword"))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    notificar("Éxito", "Contraseña cambiada con éxito", true);
                } else {
                    notificar("Error", "No se pudo cambiar la contraseña", false);
                }

            } catch (Exception e) {
                notificar("Error", "No se pudo cambiar la contraseña", false);
            }
        }
    }


    @FXML
    public void volver(ActionEvent event) {
        StageManager.loadScene("/org/example/desktop/menu-view.fxml" , 1600, 900);
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
