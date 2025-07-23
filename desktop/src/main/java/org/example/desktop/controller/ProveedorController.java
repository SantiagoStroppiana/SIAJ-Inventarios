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
import org.example.desktop.model.Proveedor;
import org.example.desktop.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.ResourceBundle;

import static org.example.desktop.util.NotificationManager.notificarError;
import static org.example.desktop.util.NotificationManager.notificarExito;

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
    @FXML private Button btnConsultarPadron;

    @FXML
    public void mostrarProveedores() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/proveedores"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();

            proveedoresOriginales = gson.fromJson(responseBody, Proveedor[].class);

            tablaProveedores.getItems().clear();
            tablaProveedores.getItems().addAll(proveedoresOriginales);


        } catch (Exception e) {
            e.printStackTrace();
            notificarError("Error Crítico" + e.getMessage());
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        razonSocialColumn.setCellValueFactory(new PropertyValueFactory<>("razonSocial"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        cuitColumn.setCellValueFactory(new PropertyValueFactory<>("cuit"));
//        activoColumn.setCellValueFactory(new PropertyValueFactory<>("activo"));
        activoColumn.setCellValueFactory(new PropertyValueFactory<>("activo"));

        activoColumn.setCellFactory(column -> new TableCell<Proveedor, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String estado = activo ? "Activo" : "Inactivo";
                    setText(estado);
                    if (activo) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });

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
        btnConsultarPadron.setOnAction(event -> ConsultarDatosProveedor());
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

            if (responseBody.trim().startsWith("{")) {
                MensajesResultados resultado = gson.fromJson(responseBody, MensajesResultados.class);

                if (resultado.isExito()) {
                    notificarExito("Proveedor actualizado exitosamente");
                    // limpiarCampos();
                } else {
                    notificarError("Error al modificar proveedor " + resultado.getMensaje());
                }
            } else {
                notificarError("Respuesta del servidor incorrecta");
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificarError("Error Crítico" + e.getMessage());
        }


        mostrarProveedores();
    }
    @FXML
    public void crearProveedor() {
        try {

            String razonsocial = txtRazonSocial.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String cuit = txtCUIT.getText().trim();
            boolean activo = true;

            if (/*id == null ||*/ razonsocial.isEmpty() ||  telefono.isEmpty() || email.isEmpty() || direccion.isEmpty() /*|| fecha_alta.isEmpty()*/) {
                notificarError("Todos los campos son obligatorios");
                return;
            }


            String razonSocialstr;
            try {
                razonSocialstr = razonsocial;
                if (razonsocial.isEmpty()) {
                    notificarError("La Razon Social no puede estar vacia");
                    return;
                }

            } catch (NumberFormatException e) {
                notificarError("La razon social debe tener un texto válido");
                return;
            }
            Proveedor proveedor = new Proveedor();

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

            if (responseBody.trim().startsWith("{")) {
                MensajesResultados resultado = gson.fromJson(responseBody, MensajesResultados.class);

                if (resultado.isExito()) {

                    mostrarProveedores();
                    notificarExito("Proveedor creado exitosamente");
                    // limpiarCampos(); // si tenés esta función activa
                } else {
                    notificarError("Error al crear proveedor " + resultado.getMensaje());
                }

            } else {
                notificarError("Respuesta del servidor incorrecta");
            }

        } catch (Exception e) {
            e.printStackTrace();
            notificarError("Error Crítico" + e.getMessage());
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
            notificarError("Seleccione un proveedor en la tabla.");
        } else {


            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/desktop/proveedor-detalle-view.fxml"));


            Parent root = fxmlLoader.load();

            ProveedorDetalleController controller = fxmlLoader.getController();
            controller.setProveedor(proveedor);
            controller.cargarProveedor();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 800, 750));
            stage.setTitle("Detalle de Proveedor");
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana anterior si querés
            stage.setOnCloseRequest(event -> {mostrarProveedores();});
            stage.showAndWait();

        }

    }

    public void ConsultarDatosProveedor () {
        try {
            String cuit;
            cuit = txtCUIT.getText();
            if (cuit.length() != 11 || !cuit.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "CUIT inválido: debe tener 11 dígitos numéricos.");
                notificarError("CUIT inválido: debe tener 11 dígitos numéricos");
                return;
            }



            if (validarCUIT(cuit)) {
                notificarExito("CUIT válido");
            } else {
                notificarError("No es un CUIT válido");
                return;
            }

            String service = "ws_sr_padron_a13";
            String ambiente = "Produccion";

            String soapResponse = LoginTicketRequest.generarTicketProduccion(service,ambiente);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document soapDoc = dbf.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(soapResponse.getBytes("UTF-8")));

            NodeList retList = soapDoc.getElementsByTagNameNS(
                    "http://wsaa.view.sua.dvadac.desein.afip.gov", "loginCmsReturn");

            String loginTicketResponseXml;

            if (retList.getLength() > 0 && retList.item(0) != null) {
                // ✅ Caso normal: loginCmsReturn existe
                loginTicketResponseXml = retList.item(0).getTextContent();
            } else {
                // ✅ Caso alternativo: ya vino el XML directo
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(soapDoc), new StreamResult(writer));
                loginTicketResponseXml = writer.toString();

                // Validación extra (opcional)
                if (!loginTicketResponseXml.contains("<loginTicketResponse")) {
                    throw new RuntimeException("No se encontró loginCmsReturn ni loginTicketResponse en la respuesta SOAP.");
                }
            }


            Document innerDoc;

            if (retList.getLength() > 0 && retList.item(0) != null) {
                // Caso con loginCmsReturn
                String innerXml = retList.item(0).getTextContent();
                innerDoc = dbf.newDocumentBuilder()
                        .parse(new ByteArrayInputStream(innerXml.getBytes("UTF-8")));
            } else {
                // Caso sin loginCmsReturn, ya vino directamente el loginTicketResponse
                innerDoc = soapDoc; // ya es el XML correcto
            }

            Element creds = (Element) innerDoc.getElementsByTagName("credentials").item(0);
            String token = creds.getElementsByTagName("token").item(0).getTextContent();
            String sign  = creds.getElementsByTagName("sign").item(0).getTextContent();


            // 3) Llamada al padrón
            String TU_CUIT = VariablesEntorno.getCUIT(); // Cambiar por el propio si es necesario
            Object resultado = PadronClient.consultarCUIT(token, sign, TU_CUIT, cuit);

            if (resultado instanceof Persona) {
                Persona p = (Persona) resultado;
                txtRazonSocial.setText(p.getApellido() +  ", " + p.getNombre());
                txtDireccion.setText(p.getDomicilioReal());


            } else {
                Empresa e = (Empresa) resultado;
                txtRazonSocial.setText(e.getRazonSocial());
                txtDireccion.setText(e.getDomicilioFiscal());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static boolean validarCUIT(String cuit) {
        if (cuit == null) return false;
        String clean = cuit.replaceAll("\\D", ""); // elimina guiones
        if (clean.length() != 11) return false;

        int[] pesos = {5,4,3,2,7,6,5,4,3,2};
        int suma = 0;
        for (int i = 0; i < 10; i++) {
            suma += Character.getNumericValue(clean.charAt(i)) * pesos[i];
        }
        int resto = suma % 11;
        int dv = Character.getNumericValue(clean.charAt(10));

        int dvCalc;
        if (resto == 0) {
            dvCalc = 0;
        } else if (resto == 1) {
            dvCalc = 9; // para CUIT argentinos, se usa DV=9 cuando resto es 1 :contentReference[oaicite:1]{index=1}
        } else {
            dvCalc = 11 - resto;
        }
        return dvCalc == dv;
    }

}