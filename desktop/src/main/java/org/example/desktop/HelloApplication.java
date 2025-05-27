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

import java.io.IOException;

public class HelloApplication extends Application {

    private static final double LOGIN_WIDTH = 700;
    private static final double LOGIN_HEIGHT = 500;
    private static final double MAIN_WIDTH = 1200;
    private static final double MAIN_HEIGHT = 800;


    @Override
    public void start(Stage stage) throws  IOException{

        try{
            login(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void login(Stage stage) throws IOException {

        try{
            FXMLLoader fxmlloader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlloader.load(), LOGIN_WIDTH, LOGIN_HEIGHT);


//            Object controller = fxmlloader.getController();
//            if(controller instanceof LoginController){
//                ((LoginController) controller).setMainStage(stage);
//                ((LoginController) controller).setApplication(this);
//            }

            LoginController controller = fxmlloader.getController();
            controller.setMainStage(stage);
            controller.setApplication(this);


            stage.setTitle("SIAJ Inventarios-Login");
            stage.setScene(scene);
            stage.setResizable(false);

            stage.show();
            centrarEscena(stage);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void menuPrincipal(Stage stage) throws IOException {
        try{
            FXMLLoader fxmlloader = new FXMLLoader(HelloApplication.class.getResource("productos-view.fxml"));
            Scene scene = new Scene(fxmlloader.load(), MAIN_WIDTH, MAIN_HEIGHT);

            stage.setTitle("SIAJ Inventarios-Menu Principal");
            stage.setScene(scene);
            stage.setResizable(false);

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmación de Cierre");
                    alert.setHeaderText("¿Estás seguro de que deseas cerrar la aplicación?");

                    ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

                    if (result == ButtonType.CANCEL) {
                        event.consume();
                    }
                }
            });

            stage.show();
            centrarEscena(stage);

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void cambiarVista(Stage stage, String fxmlFile, String titulo){
        try{
            FXMLLoader fxmlloader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlloader.load(), MAIN_WIDTH, MAIN_HEIGHT);
            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.setResizable(false);

            centrarEscena(stage);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void centrarEscena(Stage stage) throws IOException {
        stage.centerOnScreen();
    }



    public static void main(String[] args) {
        launch();
    }
}