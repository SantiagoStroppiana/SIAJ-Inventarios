package org.example.desktop;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.example.desktop.controller.LoginController;
import org.example.desktop.util.StageManager;

import java.io.IOException;

public class HelloApplication extends Application {

    private static final double LOGIN_WIDTH = 700;
    private static final double LOGIN_HEIGHT = 500;

    //COMENTAR Y MODIFICAR EL OTRO
    @Override
    public void start(Stage stage) throws  IOException{
        StageManager.setPrimaryStage(stage);
        StageManager.loadScene("/org/example/desktop/login-view.fxml", LOGIN_WIDTH, LOGIN_HEIGHT);
    }

//    @Override
//    public void start(Stage stage) throws  IOException{
//        StageManager.setPrimaryStage(stage);
    // En las XXXX pongan el nombre de la vista .fxml
//        StageManager.loadScene("/org/example/desktop/XXXX");
//    }

    public static void main(String[] args) {
        launch();
    }
}