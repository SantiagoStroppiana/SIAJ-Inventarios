package org.example.desktop.controller;

import com.google.gson.Gson;
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
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.model.Usuario;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class RegisterController {

    @FXML
    private Button registro;
    @FXML
    private Button iniciarSesion;
    @FXML
    private TextField nombre;
    @FXML
    private TextField apellido;
    @FXML
    private TextField email;
    @FXML
    private TextField password;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML
    public void registrarse(javafx.event.ActionEvent actionEvent) {
        try {
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre.getText());
            usuario.setApellido(apellido.getText());
            usuario.setEmail(email.getText());
            usuario.setPassword(password.getText());

            String json = gson.toJson(usuario);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:7000/api/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            System.out.println("Código de estado: " + response.statusCode());
            System.out.println("Respuesta del servidor: " + response.body());
            System.out.println("Datos enviados al servidor: " + json);

            if (responseBody.trim().startsWith("{")) {
                MensajesResultados resultado = gson.fromJson(responseBody, MensajesResultados.class);

                if (resultado.isExito()) {
                    notificar("Registro exitoso", resultado.getMensaje(), true);
                    limpiarCampos();
                    irALogin(actionEvent);
                } else {
                    notificar("Error al registrarse", resultado.getMensaje(), false);
                }
            } else {
                notificar("Respuesta del servidor incorrecta", responseBody, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error critico", e.getMessage(), false);
        }
    }

    private void notificar(String titulo, String mensaje, boolean exito){
        Notifications notificacion = Notifications.create()
                .title(titulo)
                .text(mensaje)
                .position(Pos.TOP_CENTER)
                .hideAfter(Duration.seconds(4));
        if (exito) {
            notificacion.showInformation();
        }else{
            notificacion.showError();
        }
    }

    private void limpiarCampos() {
        nombre.setText("");
        apellido.setText("");
        email.setText("");
        password.setText("");
    }

    public void irALogin(javafx.event.ActionEvent actionEvent) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/desktop/login-view.fxml"));

            URL resourceUrl = getClass().getResource("/org/example/desktop/login-view.fxml");
            System.out.println("URL del recurso: " + resourceUrl);
            if (resourceUrl == null) {
                System.out.println("¡No se pudo encontrar el recurso!");
            }

            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
