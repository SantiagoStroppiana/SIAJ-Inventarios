package org.example.desktop.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.desktop.dto.UsuarioDTO;
import org.example.desktop.model.Producto;
import org.example.desktop.model.Usuario;
import org.example.desktop.util.StageManager;
import org.example.desktop.util.UserSession;

import java.io.IOException;

public class MenuController {

    @FXML private Label labelBienvenida;
    @FXML private Button boton;
    @FXML private ImageView icono;
    @FXML private Label texto;

    private final UsuarioDTO usuario = UserSession.getUsuarioActual();

    @FXML
    public void initialize() {
        configurarBoton();
        if(usuario != null){
            String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
            labelBienvenida.setText("Bienvenido, " + nombreCompleto + " (" + usuario.getNombreRol() + ")");

        }
    }

    private void configurarBoton(){

        if (usuario.getNombreRol().equalsIgnoreCase("Administrador")) {
            boton.setOnAction(event -> irOrdenCompra());
            texto.setText("Orden Compra");
            icono.setImage(new Image(getClass().getResourceAsStream("/org/example/desktop/images/mdi--text-box-edit.png")));
        }else if (usuario.getNombreRol().equalsIgnoreCase("Vendedor")) {
            boton.setOnAction(event ->  irPuntoDeVenta());
            texto.setText("Punto de Venta");
            icono.setImage(new Image(getClass().getResourceAsStream("/org/example/desktop/images/majesticons--shopping-cart.png")));
        }

    }


    @FXML
    public void irPerfilUsuario() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/desktop/perfil-usuario-view.fxml"));

        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 800, 550));
        stage.setTitle("Perfil Usuario");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    public void irPuntoDeVenta() {
        StageManager.loadScene("/org/example/desktop/punto-venta-view.fxml" , 1600, 900);
    }

    @FXML
    public void irOrdenCompra() {
        StageManager.loadScene("/org/example/desktop/punto-venta-view.fxml" , 1600, 900);
    }

    @FXML
    public void irInventario() {
        StageManager.loadScene("/org/example/desktop/inventario-view.fxml" , 1600, 900);
    }

    @FXML
    public void irProveedores() {
        StageManager.loadScene("/org/example/desktop/proveedores-view.fxml" , 1600, 900);
    }

    @FXML
    public void irReportes() {
        StageManager.loadScene("/org/example/desktop/graficos-ventas-view.fxml" , 1600, 900);
    }
}
