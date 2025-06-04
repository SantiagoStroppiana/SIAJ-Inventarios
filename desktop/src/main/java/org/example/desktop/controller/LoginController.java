package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.HelloApplication;
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.model.Usuario;
import org.example.desktop.util.StageManager;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginController {

    @FXML
    private TextField email;
    @FXML
    private TextField password;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML
    public void iniciarSesion(javafx.event.ActionEvent actionEvent) {

        if(email.getText().isEmpty() || password.getText().isEmpty()){
            notificar("Error", "Por favor, complete todos los campos", false);
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email.getText());
        usuario.setPassword(password.getText());

        new Thread(() -> {
            try {
                String json = gson.toJson(usuario);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:7000/api/login"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String responseBody = response.body();

                Platform.runLater(() -> {
                    try {
                        if(responseBody.trim().startsWith("{")){
                            MensajesResultados resultado = gson.fromJson(responseBody, MensajesResultados.class);
                            if (resultado.isExito()) {
                                notificar("Iniciar sesión exitoso", resultado.getMensaje(), true);

                                StageManager.loadScene("/org/example/desktop/productos-view.fxml", 1200, 800);

                            } else {
                                notificar("Incorrecto", resultado.getMensaje(), false);
                            }
                        } else {
                            notificar("Error", responseBody, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        notificar("Error", "Error al procesar la respuesta: " + e.getMessage(), false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                final String errorMsg = e.getMessage();
                Platform.runLater(() -> {
                    notificar("Error Crítico", errorMsg, false);
                });
            }
        }).start();
    }

    private void notificar(String titulo, String mensaje, boolean exito){
        Platform.runLater(() -> {
            Notifications notificacion = Notifications.create()
                    .title(titulo)
                    .text(mensaje)
                    .position(Pos.TOP_CENTER)
                    .hideAfter(Duration.seconds(4));
            if(exito){
                notificacion.showInformation();
            }else {
                notificacion.showError();
            }
        });
    }


    public void irARegistro(javafx.event.ActionEvent actionEvent) {
        try{
            StageManager.loadScene("/org/example/desktop/register-view.fxml", 700, 650);
        }catch (Exception e){
            e.printStackTrace();
            notificar("Error", "No se pudo cargar la pantalla de registro: " + e.getMessage(), false);
        }
    }




}
