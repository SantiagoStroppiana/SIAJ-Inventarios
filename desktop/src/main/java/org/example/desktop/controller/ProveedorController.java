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
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.model.Producto;
import org.example.desktop.model.Proveedor;
import org.example.desktop.util.VariablesEntorno;


import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.ResourceBundle;

public class ProveedorController implements Initializable {


    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    Proveedor[] proveedoresOriginales;


    @FXML private TextField txtBuscarProveedor;
    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, Integer> idColumn;
    @FXML private TableColumn<Proveedor, String> razonSocialColumn;
    @FXML private TableColumn<Proveedor, Integer> telefonoColumn;
    @FXML private TableColumn<Proveedor, String> direccionColumn;
    @FXML private TableColumn<Proveedor, Boolean> activoColumn;
    @FXML private TableColumn<Proveedor, String> cuitColumn;
    @FXML private TableColumn<Proveedor, Date> fecha_altaColumn;
    @FXML private TableColumn<Proveedor, String> emailColumn;


    @FXML private Button agregar;
    @FXML private Button actualizar;
    @FXML private Button desactivar;
    @FXML private Button ver;

    @FXML
    public void mostrarProveedores() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/proveedores"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);

            String responseBody = response.body();
            System.out.println(responseBody);

            proveedoresOriginales = gson.fromJson(responseBody, Proveedor[].class);

            tablaProveedores.getItems().clear();
            tablaProveedores.getItems().addAll(proveedoresOriginales);

            for (Proveedor p : proveedoresOriginales) {
                System.out.println("Proveedor: " + p.getRazonSocial() + ", Teléfono: " + p.getTelefono());
            }

            System.out.println("Respuesta del backend:");
            System.out.println(responseBody);




        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error Crítico", e.getMessage(), false);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        razonSocialColumn.setCellValueFactory(new PropertyValueFactory<>("razonSocial"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        cuitColumn.setCellValueFactory(new PropertyValueFactory<>("cuit"));
        activoColumn.setCellValueFactory(new PropertyValueFactory<>("activo"));
        //fecha_altaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha_alta"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));




        /*razonSocialColumn.setCellValueFactory(cellData -> {
            Proveedor proveedor = cellData.getValue();
            return new SimpleStringProperty(
                    proveedor.getRazonSocial() != null ? String.valueOf(proveedor.getId()) : "Sin Razon Social"
            );
        });*/

        agregar.setOnAction(event -> crearProveedor());
        desactivar.setOnAction(event -> cambiarEstado());
        actualizar.setOnAction(event -> mostrarProveedores());
        ver.setOnAction(event -> {
            try {
                verProveedor();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        mostrarProveedores();
        txtBuscarProveedor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (proveedoresOriginales == null) return;

            String filtro = newValue.toLowerCase();

            tablaProveedores.getItems().setAll(
                    java.util.Arrays.stream(proveedoresOriginales)
                            .filter(p -> p.getRazonSocial().toLowerCase().contains(filtro)
                                    || p.getEmail().toLowerCase().contains(filtro) || p.getDireccion().toLowerCase().contains(filtro))
                            .toList()
            );
        });


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


    @FXML private TextField txtRazonSocial;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtCUIT;
    @FXML private TextField txtActivo;
    @FXML private TextField txtFecha_Alta;
    @FXML private TextField txtId;
    @FXML private TextField txtEmail;
    @FXML private SplitMenuButton menuProveedor;
    @FXML private SplitMenuButton menuActivo;
    @FXML private SplitMenuButton menuTelefono;


    @FXML
    public void cambiarEstado() {
        Proveedor proveedor = tablaProveedores.getSelectionModel().getSelectedItem();

        try {
            boolean estado = proveedor.isActivo();

            if (estado) {
                proveedor.setActivo(false);
            } else {
                proveedor.setActivo(true);
            }

            String json = gson.toJson(proveedor);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/modificarProveedor"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            System.out.println("Código de estado: " + response.statusCode());
            System.out.println("Respuesta del servidor: " + response.body());
            System.out.println("Datos enviados al servidor: " + json);

            if (responseBody.trim().startsWith("{")) {
                MensajesResultados resultado = gson.fromJson(responseBody, MensajesResultados.class);

                if (resultado.isExito()) {
                    notificar("Proveedor modificado", resultado.getMensaje(), true);
                    // limpiarCampos();

                } else {
                    notificar("Error al modificar proveedor", resultado.getMensaje(), false);
                }
            } else {
                notificar("Respuesta del servidor incorrecta", responseBody, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error critico", e.getMessage(), false);
        }


        mostrarProveedores();
    }
    @FXML
    public void crearProveedor() {
        try {


            //Integer id = Integer.valueOf(txtId.getText().trim());
            String razonsocial = txtRazonSocial.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String cuit = txtCUIT.getText().trim();
            boolean activo = true;
            //String fecha_alta = txtFecha_Alta.getText().trim();



            if (/*id == null ||*/ razonsocial.isEmpty() ||  telefono.isEmpty() || email.isEmpty() || direccion.isEmpty() /*|| fecha_alta.isEmpty()*/) {
                notificar("Campos incompletos", "Todos los campos son obligatorios.", false);
                return;
            }


            /*int idstr;
            try {
                idstr = Integer.parseInt(String.valueOf(id));
                if (id < 0) {
                    notificar("ID inválido", "El ID no puede ser negativo.", false);
                    return;
                }
            } catch (NumberFormatException e) {
                notificar("Error de formato", "El ID debe ser un número entero.", false);
                return;
            }*/

            String razonSocialstr;
            try {
                razonSocialstr = razonsocial;
                if (razonsocial.isEmpty()) {
                    notificar("Razon Social inválido", "La Razon Social no puede estar vacia.", false);
                    return;
                }

            } catch (NumberFormatException e) {
                notificar("Error de formato", "La Razon social debe tener un texto válido.", false);
                return;
            }
            Proveedor proveedor = new Proveedor();

            //proveedor.setId(id);

            proveedor.setRazonSocial(razonsocial);

            proveedor.setTelefono(telefono);

            proveedor.setDireccion(direccion);

            proveedor.setCuit(cuit);
            proveedor.setActivo(true);

            proveedor.setEmail(email);

            proveedor.setFecha_alta(System.currentTimeMillis());


            String json = gson.toJson(proveedor);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/crearProveedor"))
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
                    // Agregar el proveedor directamente a la tabla

                  /*  Thread.sleep(10000);
                    mostrarProveedores();
*/
                    mostrarProveedores();
                    notificar("Proveedor creado", resultado.getMensaje(), true);
                    // limpiarCampos(); // si tenés esta función activa
                } else {
                    notificar("Error al crear proveedor", resultado.getMensaje(), false);
                }

            } else {
                notificar("Respuesta del servidor incorrecta", responseBody, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error critico", e.getMessage(), false);
        }
    }

   /*
    private void limpiarCampos() {
        nombre.setText("");
        apellido.setText("");
        email.setText("");
        password.setText("");
    }*/


    @FXML
    public void verProveedor() throws IOException {
        Proveedor proveedor = tablaProveedores.getSelectionModel().getSelectedItem();

        if (proveedor == null) {
            notificar("Seleccionar proveedor", "Debe seleccionar un proveedor en la tabla.", false);
        } else {


            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/desktop/proveedor-detalle-view.fxml"));


            Parent root = fxmlLoader.load();

            ProveedorDetalleController controller = fxmlLoader.getController();
            controller.setProveedor(proveedor);
            controller.cargarProveedor();

            Stage stage = new Stage(); // Esto NO usa StageManager
            stage.setScene(new Scene(root, 800, 550));
            stage.setTitle("Detalle de Proveedor");
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana anterior si querés
            stage.setOnCloseRequest(event -> {mostrarProveedores();});
            stage.showAndWait();

        }

    }

    public void modificarProveedor(int id){
        try {



            String razonsocial = txtRazonSocial.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
            String direccion = txtDireccion.getText().trim();


            if (razonsocial.isEmpty() || telefono.isEmpty() ||  email.isEmpty() || direccion.isEmpty()) {
                notificar("Campos incompletos", "Todos los campos son obligatorios.", false);
                return;
            }




            String razonsocialStr;
            try {
                razonsocialStr = razonsocial;
                if (razonsocial.isEmpty()) {
                    notificar("Razon Social inválido", "La Razon Social no puede estar vacia.", false);
                    return;
                }

            } catch (NumberFormatException e) {
                notificar("Error de formato", "La Razon social debe tener un texto válido.", false);
                return;
            }
            Proveedor proveedor = new Proveedor();

            proveedor.setId(id);

            proveedor.setRazonSocial(razonsocial);

            proveedor.setTelefono(telefono);

            proveedor.setDireccion(direccion);

            proveedor.setActivo(true);

            proveedor.setEmail(email);





            String json = gson.toJson(proveedor);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/actualizarProveedor"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            System.out.println("Código de estado: " + response.statusCode());
            System.out.println("Respuesta del servidor: " + response.body());
            System.out.println("Datos enviados al servidor: " + json);

            if (responseBody.trim().startsWith("{")) {
                MensajesResultados resultado = gson.fromJson(responseBody, MensajesResultados.class);

                if (resultado.isExito()) {
                    // Agregar el proveedor directamente a la tabla

                  /*  Thread.sleep(10000);
                    mostrarProveedores();
*/
                    notificar("Proveedor modificado!", resultado.getMensaje(), true);
                    // limpiarCampos(); // si tenés esta función activa
                } else {
                    notificar("Error al modificar proveedor", resultado.getMensaje(), false);
                }

            } else {
                notificar("Respuesta del servidor incorrecta", responseBody, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificar("Error critico", e.getMessage(), false);
        }
    }

}