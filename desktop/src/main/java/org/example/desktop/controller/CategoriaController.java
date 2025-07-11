package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.model.Categoria;
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.model.Producto;
import org.example.desktop.util.VariablesEntorno;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class CategoriaController implements Initializable {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtBuscarCategoria;
    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Producto, String> nombreColumn;
    @FXML private TableColumn<Producto, String> descripcionColumn;
    @FXML private Button btnAgregar;
    @FXML private Button btnActualizar;
    @FXML private Button btnDesactivar;
    @FXML private Button btnModificar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        btnAgregar.setOnAction(event -> crearCategoria());
        //desactivar.setOnAction(event -> cambiarEstado());
        btnActualizar.setOnAction(event -> mostrarCategorias());
        btnModificar.setOnAction(event -> {
            try {
                verCategoria();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        mostrarCategorias();
        txtBuscarCategoria.textProperty().addListener((observable, oldValue, newValue) -> {
            if (categoriasOriginales == null) return;

            String filtro = newValue.toLowerCase();

            tablaCategorias.getItems().setAll(
                    java.util.Arrays.stream(categoriasOriginales)
                            .filter(c -> c.getNombre().toLowerCase().contains(filtro)
                                    || c.getDescripcion().toLowerCase().contains(filtro))
                            .toList()
            );
        });

    }
    private Categoria[] categoriasOriginales;
    @FXML
    public void mostrarCategorias() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/categorias"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);

            String responseBody = response.body();
            System.out.println(responseBody);

            categoriasOriginales = gson.fromJson(responseBody, Categoria[].class);

            tablaCategorias.getItems().clear();
            tablaCategorias.getItems().addAll(categoriasOriginales);

            for (Categoria c : categoriasOriginales) {
                System.out.println("Categoria: " + c.getNombre());
            }

            System.out.println("Respuesta del backend:");
            System.out.println(responseBody);



        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error Crítico", e.getMessage(), false);
        }

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
            } else {
                notificacion.showError();
            }
        });
    }



    @FXML
    public void crearCategoria() {
        try {


            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();


            if (nombre.isEmpty() || descripcion.isEmpty()) {
                notificar("Campos incompletos", "Todos los campos son obligatorios.", false);
                return;
            }

            Categoria categoria = new Categoria();

            categoria.setNombre(nombre);
            categoria.setDescripcion(descripcion);

            String json = gson.toJson(categoria);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/crearCategoria"))
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
                    // Agregar el producto directamente a la tabla

                  /*  Thread.sleep(10000);
                    mostrarProductos();
*/
                    mostrarCategorias();

                    notificar("Categoria creado", resultado.getMensaje(), true);
                    // limpiarCampos(); // si tenés esta función activa
                } else {
                    notificar("Error crear categoria", resultado.getMensaje(), false);
                }

            } else {
                notificar("Respuesta del servidor incorrecta", responseBody, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error critico", e.getMessage(), false);
        }
    }

    @FXML
    public void verCategoria() throws IOException {
        Categoria categoria = tablaCategorias.getSelectionModel().getSelectedItem();

        if (categoria == null) {
            notificar("Seleccionar categoria", "Debe seleccionar un categoria en la tabla.", false);
        } else {


            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/desktop/categoria-detalle-view.fxml"));


            Parent root = fxmlLoader.load();

            CategoriaDetalleController controller = fxmlLoader.getController();
            controller.setCategoria(categoria);
            controller.cargarCategoria();

            Stage stage = new Stage(); // Esto NO usa StageManager
            stage.setScene(new Scene(root, 800, 550));
            stage.setTitle("Detalle de cCtegoria");
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana anterior si querés
            stage.setOnCloseRequest(event -> {mostrarCategorias();});
            stage.showAndWait();

        }

    }

}
