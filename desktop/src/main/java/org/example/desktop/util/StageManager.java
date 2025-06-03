package org.example.desktop.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class StageManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void loadScene(String fxml, double width, double height) {
        try{
            FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();

            primaryStage.setOnCloseRequest(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmación de cierre ");
                alert.setHeaderText("¿Estás seguro de que deseas cerrar la aplicación?");
                ButtonType ok = alert.showAndWait().orElse(ButtonType.CANCEL);
                if (ok == ButtonType.CANCEL) {
                    event.consume();
                }
            });

            primaryStage.show();
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

}
