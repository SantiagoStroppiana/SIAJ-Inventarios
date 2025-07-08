package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.desktop.dto.UsuarioDTO;
import org.example.desktop.util.StageManager;
import org.example.desktop.util.UserSession;

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

    @FXML public void cambiarPassword(ActionEvent event){
        StageManager.loadScene("/org/example/desktop/cambiar-password-view.fxml", 700, 500);
    }


}
