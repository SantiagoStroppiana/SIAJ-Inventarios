package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.desktop.dto.UsuarioDTO;
import org.example.desktop.util.UserSession;

import java.io.IOException;
import java.net.http.HttpClient;

public class PefilUserController {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML private Label lblNombre;
    @FXML private Label lblRol;
    @FXML private Label lblApellido;
    @FXML private Label lblEmail;

    public void initialize(){

        UsuarioDTO usuario = UserSession.getUsuarioActual();
        if (usuario != null){
            lblNombre.setText(usuario.getNombre());
            lblApellido.setText(usuario.getApellido());
            lblEmail.setText(usuario.getEmail());
            lblRol.setText(usuario.getNombreRol());
        }
    }

    @FXML
    public void cambiarPassword(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/desktop/cambiar-password-view.fxml"));
        Parent root = fxmlLoader.load();

        Stage passwordStage = new Stage();
        passwordStage.setScene(new Scene(root, 700, 500));
        passwordStage.setTitle("Cambiar Contraseña");
        passwordStage.setResizable(false);
        passwordStage.initStyle(StageStyle.UTILITY);

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        passwordStage.setX(currentStage.getX() + (currentStage.getWidth() - 700) / 2);
        passwordStage.setY(currentStage.getY() + (currentStage.getHeight() - 500) / 2);

        passwordStage.show();
    }


}
