package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.example.desktop.model.MensajesResultados;
import org.example.desktop.model.Proveedor;
import org.example.desktop.util.VariablesEntorno;


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

    @FXML private TextField txtBuscarProveedor;
    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, Integer> idColumn;
    @FXML private TableColumn<Proveedor, String> razonSocialColumn;
    @FXML private TableColumn<Proveedor, Integer> telefonoColumn;
    @FXML private TableColumn<Proveedor, String> direccionColumn;
    @FXML private TableColumn<Proveedor, Boolean> activoColumn;
    @FXML private TableColumn<Proveedor, Date> fecha_altaColumn;
    @FXML private TableColumn<Proveedor, String> emailColumn;


    @FXML private Button agregar;
    @FXML private Button actualizar;

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

            Proveedor[] proveedores = gson.fromJson(responseBody, Proveedor[].class);

            tablaProveedores.getItems().clear();
            tablaProveedores.getItems().addAll(proveedores);

            for (Proveedor p : proveedores) {
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
        actualizar.setOnAction(event -> cambiarEstado(1));

        mostrarProveedores();
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
    @FXML private TextField txtActivo;
    @FXML private TextField txtFecha_Alta;
    @FXML private TextField txtId;
    @FXML private TextField txtEmail;
    @FXML private SplitMenuButton menuProveedor;
    @FXML private SplitMenuButton menuActivo;
    @FXML private SplitMenuButton menuTelefono;


    @FXML
    public void cambiarEstado(int id) {
        try {
            Proveedor proveedor = tablaProveedores.getItems().get(id);
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

            proveedor.setActivo(true);

            proveedor.setEmail(email);

            proveedor.setFecha_alta(System.currentTimeMillis());

            /*
            Producto producto = new Producto();
            producto.setSku(txtSku.getText());
            producto.setNombre(txtNombre.getText());
            producto.setStock(Integer.parseInt(txtStock.getText()));
            producto.setPrecio(BigDecimal.valueOf(Double.parseDouble(txtPrecio.getText())));
            //producto.setCategoria(producto.getCategoria());
            producto.setActivo(true);
            producto.setProveedorid(new Proveedor(1,null,null,null,null,true));
            producto.setImg("");

            */

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