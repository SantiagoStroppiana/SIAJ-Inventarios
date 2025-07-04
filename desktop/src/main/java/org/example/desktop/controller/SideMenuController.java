package org.example.desktop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import org.example.desktop.model.Usuario;
import org.example.desktop.util.StageManager;
import org.example.desktop.util.UserSession;

import java.util.Optional;

public class SideMenuController {

    @FXML
    private Button btnUsuarios;
    @FXML
    private Button btnOrdenCompra;
    @FXML
    private Button btnVentas;

    @FXML
    public void initialize() {
        Usuario usuario = UserSession.getUsuarioActual();
        if (usuario != null && usuario.getRol() != null) {
            String rol = usuario.getRol().getNombre();
            if (!rol.equals("Administrador")) {
                btnUsuarios.setVisible(false);
                btnUsuarios.setManaged(false);
                btnOrdenCompra.setVisible(false);
                btnOrdenCompra.setManaged(false);
                btnVentas.setVisible(true);
                btnVentas.setManaged(true);
            } else {
                btnUsuarios.setVisible(true);
                btnUsuarios.setManaged(true);
                btnOrdenCompra.setVisible(true);
                btnOrdenCompra.setManaged(true);
                btnVentas.setVisible(false);
                btnVentas.setManaged(false);
            }

        }
    }

    public void irMenuPrincipal(ActionEvent actionEvent) {
        StageManager.loadScene("/org/example/desktop/menu-view.fxml", 1600, 900);
    }

    public void irProductos(ActionEvent actionEvent){
        StageManager.loadScene("/org/example/desktop/productos-view.fxml", 1600, 900);
    }

    public void irProveedores(ActionEvent actionEvent){
        StageManager.loadScene("/org/example/desktop/proveedores-view.fxml", 1600, 900);
    }

    public void irUsuarios(ActionEvent actionEvent){
        StageManager.loadScene("/org/example/desktop/usuarios-view.fxml", 1600, 900);
    }

    public void irReportes(ActionEvent actionEvent){
        StageManager.loadScene("/org/example/desktop/menu-view.fxml", 1600, 900);
    }

    public void irPuntoVenta(ActionEvent actionEvent){
        StageManager.loadScene("/org/example/desktop/punto-venta-view.fxml", 1600, 900);
    }

    public void irOrdenCompra(ActionEvent actionEvent){
        StageManager.loadScene("/org/example/desktop/orden-compra-view.fxml", 1600, 900);
    }

    public void logout(ActionEvent actionEvent) {
        if (!UserSession.haySesionActiva()) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar sesión");
        alert.setHeaderText("¿Estás seguro que querés cerrar sesión?");
        alert.setContentText("Perderás el acceso a la sesión actual.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            UserSession.cerrarSesion();
            StageManager.loadScene("/org/example/desktop/login-view.fxml", 700, 500);
        }
    }
}
