package org.example.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("productos-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(),  700, 500);
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        stage.setTitle("SIAJ Inventarios");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}