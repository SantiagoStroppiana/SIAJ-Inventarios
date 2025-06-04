package org.example.desktop.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.desktop.model.Producto;

public class ProductoDetalleController {

    private Producto producto;

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    @FXML private TextField txtSku;
    @FXML private TextField txtNombre;
    @FXML private TextField txtStock;
    @FXML private TextField txtPrecio;
    @FXML private Button modificar;

    public void cargarProducto() {
        txtSku.setText(producto.getSku());
        txtNombre.setText(producto.getNombre());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtStock.setText(String.valueOf(producto.getStock()));

        txtStock.setEditable(false);
        txtSku.setEditable(false);
        txtNombre.setEditable(false);
        txtPrecio.setEditable(false);

        modificar.setOnAction(event -> {habilitarEdicion();});
    }

    public void habilitarEdicion() {
        if (txtSku.isEditable() && txtNombre.isEditable() && txtStock.isEditable() && txtPrecio.isEditable()) {
            txtStock.setEditable(false);
            txtSku.setEditable(false);
            txtNombre.setEditable(false);
            txtPrecio.setEditable(false);
        } else {
            txtStock.setEditable(true);
            txtSku.setEditable(true);
            txtNombre.setEditable(true);
            txtPrecio.setEditable(true);
        }
    }
    /*

                <TextField fx:id="txtSku" layoutX="330.0" layoutY="83.0" prefHeight="38.0" prefWidth="230.0" promptText="SKU" />
                <TextField fx:id="txtNombre" layoutX="330.0" layoutY="129.0" prefHeight="38.0" prefWidth="230.0" promptText="Nombre producto" />
                <TextField fx:id="txtStock" layoutX="330.0" layoutY="174.0" prefHeight="38.0" prefWidth="230.0" promptText="Stock" />
                <TextField fx:id="txtPrecio" layoutX="330.0" layoutY="220.0" prefHeight="38.0" prefWidth="230.0" promptText="Precio" />

     */

}
