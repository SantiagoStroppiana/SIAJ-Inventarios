package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.desktop.model.Usuario;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class UsuarioController implements Initializable {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> idColumn;
    @FXML private TableColumn<Usuario, String> nombreColumn;
    @FXML private TableColumn<Usuario, String> apellidoColumn;
    @FXML private TableColumn<Usuario, String> emailColumn;
    @FXML private TableColumn<Usuario, String> rolColumn;


    @FXML
    public void mostrarUsuarios() {

        try{
          HttpRequest request = HttpRequest.newBuilder()
                  .uri(URI.create("http://localhost:7000/api/usuarios"))
                  .GET()
                  .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            Usuario[] usuarios = gson.fromJson(responseBody, Usuario[].class);

            tablaUsuarios.getItems().clear();
            tablaUsuarios.getItems().addAll(usuarios);


            for (Usuario u : usuarios){
                System.out.println(u.getNombre() + " " + u.getRolId());
            }

            System.out.println("Respuesta del backend:");
            System.out.println(responseBody);

            for (Usuario u : usuarios){
                System.out.println(u.getNombre() + " " + u.getRolId().getNombre());
            }

        } catch (Exception e) {
            e.printStackTrace();
//            notificar("Error Crítico", e.getMessage(), false);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidoColumn.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolColumn.setCellValueFactory(new PropertyValueFactory<>("nombreRol"));

        mostrarUsuarios();

    }




}
