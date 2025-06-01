package org.example.desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
//import org.example.desktop.util.StageManager;

public class MenuController {

    @FXML private Button puntoDeVenta;
    @FXML private Button inventario;
    @FXML private Button proveedores;
    @FXML private Button reportes;

    @FXML
    public void irPuntoDeVenta() {
//        StageManager.loadScene("/org/example/desktop/punto-venta-view.fxml");
    }

    @FXML
    public void irInventario() {
//        StageManager.loadScene("/org/example/desktop/inventario-view.fxml");
    }

    @FXML
    public void irProveedores() {
//        StageManager.loadScene("/org/example/desktop/proveedores-view.fxml");
    }

    @FXML
    public void irReportes() {
//        StageManager.loadScene("/org/example/desktop/reportes-view.fxml");
    }
}
