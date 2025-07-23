package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
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

public class CategoriaController implements Initializable {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    // Campos FXML
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtBuscarCategoria;
    @FXML private Label lblCategoriaNombre;
    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria, String> nombreColumn; // Corregido: era Producto
    @FXML private TableColumn<Categoria, String> descripcionColumn; // Corregido: era Producto
    @FXML private Button btnAgregar;
    @FXML private Button btnModificar;

    // Variable para filtro
    private Categoria[] categoriasOriginales;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar columnas de la tabla
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Configurar eventos de botones
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
                System.out.println("Respuesta del backend: " + responseBody);

                categoriasOriginales = gson.fromJson(responseBody, Categoria[].class);

                tablaCategorias.getItems().clear();
                tablaCategorias.getItems().addAll(categoriasOriginales);

                System.out.println("Categorías cargadas: " + categoriasOriginales.length);
            } else {
                notificar("Error de conexión", "Error al obtener categorías del servidor", false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error Crítico", e.getMessage(), false);
        }
    }

    @FXML
    public void crearCategoria() {
        try {
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            // Validaciones
            if (nombre.isEmpty()) {
                notificar("Campo requerido", "El nombre es obligatorio.", false);
                txtNombre.requestFocus();
                return;
            }

            if (descripcion.isEmpty()) {
                notificar("Campo requerido", "La descripción es obligatoria.", false);
                txtDescripcion.requestFocus();
                return;
            }

            // Crear objeto categoria
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            categoria.setDescripcion(descripcion);
            lblCategoriaNombre.setText(categoria.getNombre());
            String json = gson.toJson(categoria);

            // Enviar petición POST
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/crearCategoria"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            System.out.println("Código de estado: " + response.statusCode());
            System.out.println("Respuesta del servidor: " + responseBody);

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                if (responseBody.trim().startsWith("{")) {
                    MensajesResultados resultado = gson.fromJson(responseBody, MensajesResultados.class);

                    if (resultado.isExito()) {
                        mostrarCategorias();
                        limpiarCampos();
                        notificar("Categoría creada", resultado.getMensaje(), true);
                    } else {
                        notificar("Error al crear categoría", resultado.getMensaje(), false);
                    }
                } else {
                    // Si no es JSON, asumir que es éxito
                    mostrarCategorias();
                    limpiarCampos();
                    notificar("Categoría creada", "Categoría creada exitosamente", true);
                }
            } else {
                notificar("Error del servidor", "Error al crear la categoría. Código: " + response.statusCode(), false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error crítico", "Error inesperado: " + e.getMessage(), false);
        }
    }

    @FXML
    public void verCategoria() throws IOException {
        Categoria categoria = tablaCategorias.getSelectionModel().getSelectedItem();

        if (categoria == null) {
            notificar("Seleccionar categoría", "Debe seleccionar una categoría en la tabla.", false);
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
            stage.setTitle("Detalle de Categoría"); // Corregido el typo
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnCloseRequest(event -> mostrarCategorias());
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            notificar("Error", "No se pudo abrir la ventana de detalle: " + e.getMessage(), false);
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtDescripcion.clear();
        txtNombre.requestFocus();
    }

    private void notificar(String titulo, String mensaje, boolean exito) {
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
}