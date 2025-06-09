package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.model.Usuario;
import org.example.desktop.util.VariablesEntorno;

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
    @FXML private TableColumn<Usuario, Void> accionColumn;


    @FXML
    public void mostrarUsuarios() {

        try{
          HttpRequest request = HttpRequest.newBuilder()
                  .uri(URI.create(VariablesEntorno.getServerURL() + "/api/usuarios"))
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
            notificar("Error Crítico", e.getMessage(), false);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidoColumn.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolColumn.setCellValueFactory(new PropertyValueFactory<>("nombreRol"));
        columanAccion();
        mostrarUsuarios();

    }

    public void columanAccion(){
        Callback<TableColumn<Usuario, Void>, TableCell<Usuario, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Usuario, Void> call(final TableColumn<Usuario, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("CAMBIAR ROL");
                    {
                        btn.setOnAction(event -> {
                            Usuario usuario = getTableView().getItems().get(getIndex());
                            int id = usuario.getId();

                            Usuario usuarioRequest = new Usuario();
                            usuarioRequest.setId(id);

                            try{
                                String json = gson.toJson(usuarioRequest);

                                HttpRequest request = HttpRequest.newBuilder()
                                        .uri(URI.create(VariablesEntorno.getServerURL() + "/api/actualizarRol"))
                                        .header("Content-Type", "application/json")
                                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                                        .build();

                                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                                mostrarUsuarios();

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                            System.out.println("USUARIO"+ id);


                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        }else{
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        accionColumn.setCellFactory(cellFactory);
    }

    private void notificar(String titulo, String mensaje, boolean exito){
        Platform.runLater(() -> {
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
        });
    }



}
