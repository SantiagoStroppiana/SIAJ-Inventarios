package org.example.desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.desktop.model.Usuario;
import org.example.desktop.util.StageManager;
import org.example.desktop.util.UserSession;

public class MenuController {

    @FXML private Label labelBienvenida;


    @FXML
    public void initialize() {
        Usuario usuario = UserSession.getUsuarioActual();
        if (usuario != null && usuario.getRol() != null) {
            String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
            labelBienvenida.setText("Bienvenido, " + nombreCompleto + " (" + usuario.getRol().getNombre() + ")");
        } else {
            labelBienvenida.setText("Bienvenido");
        }
    }


    @FXML
    public void irPuntoDeVenta() {
        StageManager.loadScene("/org/example/desktop/punto-venta-view.fxml" , 1600, 900);
    }

    @FXML
    public void irInventario() {
        StageManager.loadScene("/org/example/desktop/inventario-view.fxml" , 1600, 900);
    }

    @FXML
    public void irProveedores() {
        StageManager.loadScene("/org/example/desktop/proveedores-view.fxml" , 1600, 900);
    }

    @FXML
    public void irReportes() {
        StageManager.loadScene("/org/example/desktop/reportes-view.fxml" , 1600, 900);
    }
}
