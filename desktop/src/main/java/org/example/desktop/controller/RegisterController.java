package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.model.Usuario;

import java.net.URI;
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

    @FXML
    public void registrarse(javafx.event.ActionEvent actionEvent) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            Gson gson = new Gson();

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre.getText());
            usuario.setApellido(apellido.getText());
            usuario.setEmail(email.getText());
            usuario.setPassword(password.getText());

            String json = gson.toJson(usuario);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:7070/api/register"))
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
                    alerta("Registro exitoso", resultado.getMensaje(), Alert.AlertType.INFORMATION);
                } else {
                    alerta("Error al registrar el usuario", resultado.getMensaje(), Alert.AlertType.ERROR);
                }
            } else {
                alerta("Respuesta inesperada", responseBody, Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            alerta("Error al procesar el registro", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void alerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
