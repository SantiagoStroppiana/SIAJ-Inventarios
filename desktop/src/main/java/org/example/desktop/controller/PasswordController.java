package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    @FXML private VBox emailBox;
    @FXML private VBox oldPasswordBox;
    @FXML private VBox newPasswordBox;
    @FXML private VBox repetirPasswordBox;

    @FXML private Label labelOldPassword;
    @FXML private Label labelNewPassword;
    @FXML private Label labelRepetirPassword;

    @FXML private TextField oldPassword;
    @FXML private TextField newPassword;
    @FXML private TextField repetirPassword;
    @FXML private TextField email;

    @FXML private Button btnCambiarPassword;
    @FXML private Button btnOlvidePassword;

    @FXML
    public void initialize() {
        UsuarioDTO usuario = UserSession.getUsuarioActual();

        if (usuario != null) {
            emailBox.setVisible(false); emailBox.setManaged(false);
            repetirPasswordBox.setVisible(false); repetirPasswordBox.setManaged(false);
            btnOlvidePassword.setVisible(false); btnOlvidePassword.setManaged(false);
        } else {
            btnCambiarPassword.setVisible(false); btnCambiarPassword.setManaged(false);
            oldPasswordBox.setVisible(false); oldPasswordBox.setManaged(false);

            labelNewPassword.setText("Nueva Contraseña");
            newPassword.setPromptText("Ingresar nueva contraseña");

            labelRepetirPassword.setText("Repetir Contraseña");
            repetirPassword.setPromptText("Repetir nueva contraseña");
        }
    }

    @FXML public void olvidePassword(ActionEvent event) {

        String email = this.email.getText();
        String newPas = oldPassword.getText();
        String repetirPas = repetirPassword.getText();

        if (newPas.isEmpty() || repetirPas.isEmpty() && newPas.length() < 6 || repetirPas.length() < 6) {
            notificar("Campos vacíos", "Debes completar ambos campos de contraseña.", false);
            return;
        }

        try {
            UsuarioForgetPasswordDTO usuarioForgetPasswordDTO = new UsuarioForgetPasswordDTO();
            usuarioForgetPasswordDTO.setEmail(email);
            usuarioForgetPasswordDTO.setNewPassword(newPas);
            usuarioForgetPasswordDTO.setConfirmPassword(repetirPas);

            String json = gson.toJson(usuarioForgetPasswordDTO);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/olvidePassword"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


            if (response.statusCode() == 200) {
                notificar("Éxito", "Contraseña cambiada con éxito", true);
            } else {
                notificar("Error", "No se pudo cambiar la contraseña", false);
            }
        }catch (Exception e) {
            notificar("Error", "No se pudo actualizar la contraseña", false);
        }

    }


    @FXML
    public void cambiarPassword(ActionEvent event) {
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

        if (UserSession.getUsuarioActual() != null) {
            StageManager.loadScene("/org/example/desktop/menu-view.fxml" , 1600, 900);
        }else{
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
