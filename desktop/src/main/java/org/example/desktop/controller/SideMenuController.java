package org.example.desktop.controller;

import javafx.event.ActionEvent;
import org.example.desktop.util.StageManager;

public class SideMenuController {


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

    public void logout(ActionEvent actionEvent){

    }

}
