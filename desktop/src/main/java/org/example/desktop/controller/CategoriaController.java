package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.desktop.model.Categoria;
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.util.VariablesEntorno;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

import static org.example.desktop.util.NotificationManager.notificarError;
import static org.example.desktop.util.NotificationManager.notificarExito;

public class CategoriaController implements Initializable {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtBuscarCategoria;
    @FXML private Label lblCategoriaNombre;
    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria, String> nombreColumn;
    @FXML private TableColumn<Categoria, String> descripcionColumn;
    @FXML private Button btnAgregar;
    @FXML private Button btnModificar;

    private Categoria[] categoriasOriginales;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        btnAgregar.setOnAction(event -> crearCategoria());
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

            String filtro = newValue.toLowerCase().trim();

            if (filtro.isEmpty()) {
                tablaCategorias.getItems().setAll(categoriasOriginales);
            } else {
                tablaCategorias.getItems().setAll(
                        java.util.Arrays.stream(categoriasOriginales)
                                .filter(c -> c.getNombre().toLowerCase().contains(filtro)
                                        || c.getDescripcion().toLowerCase().contains(filtro))
                                .toList()
                );
            }
        });
    }

    @FXML
    public void mostrarCategorias() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/categorias"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();

                categoriasOriginales = gson.fromJson(responseBody, Categoria[].class);

                tablaCategorias.getItems().clear();
                tablaCategorias.getItems().addAll(categoriasOriginales);

            } else {
                notificarError("Error al obtener categorías del servidor");
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificarError("Error Crítico " + e.getMessage());
        }
    }

    @FXML
    public void crearCategoria() {
        try {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            if (nombre.isEmpty()) {
                notificarError("Campo requerido\", \"El nombre es obligatorio.");
                txtNombre.requestFocus();
                return;
            }

            if (descripcion.isEmpty()) {
                notificarError("Campo requerido\", \"La descripcion es obligatorio.");
                txtDescripcion.requestFocus();
                return;
            }
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            categoria.setDescripcion(descripcion);
            lblCategoriaNombre.setText(categoria.getNombre());
            String json = gson.toJson(categoria);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/crearCategoria"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                if (responseBody.trim().startsWith("{")) {
                    MensajesResultados resultado = gson.fromJson(responseBody, MensajesResultados.class);
                    if (resultado.isExito()) {
                        mostrarCategorias();
                        limpiarCampos();
                        notificarExito("Categoría creada " + resultado.getMensaje());
                    } else {
                        notificarError("Error categoria creada " + resultado.getMensaje());
                    }
                } else {
                    mostrarCategorias();
                    limpiarCampos();
//                    notificarExito("Categoría creada exitosamente");
                }
            } else {
                notificarError("Error al crear la categoría. Código: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificarError("Error Crítico " + e.getMessage());
        }
    }

    @FXML
    public void verCategoria() throws IOException {
        Categoria categoria = tablaCategorias.getSelectionModel().getSelectedItem();

        if (categoria == null) {
            notificarError("Debe seleccionar una categoría en la tabla.");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/desktop/categoria-detalle-view.fxml"));
            Parent root = fxmlLoader.load();

            CategoriaDetalleController controller = fxmlLoader.getController();
            controller.setCategoria(categoria);
            controller.cargarCategoria();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 800, 550));
            stage.setTitle("Detalle de Categoría");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnCloseRequest(event -> mostrarCategorias());
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            notificarError("No se pudo abrir la ventana de detalle: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtDescripcion.clear();
        txtNombre.requestFocus();
    }

}