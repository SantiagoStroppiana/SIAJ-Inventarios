package org.example.desktop.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.desktop.model.DetalleVenta;

import java.util.List;

public class DetalleVentaController {

    @FXML private TableView<DetalleVenta> tablaDetalles;
    @FXML private TableColumn<DetalleVenta, String> productoColumn;
    @FXML private TableColumn<DetalleVenta, Integer> cantidadColumn;
    @FXML private TableColumn<DetalleVenta, Double> precioUnitarioColumn;

    private List<DetalleVenta> detalles;

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
        tablaDetalles.getItems().setAll(detalles);
    }

    @FXML
    public void initialize() {
        productoColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProducto().getNombre()));
        cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        precioUnitarioColumn.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        tablaDetalles.setFixedCellSize(30);
        int maxVisibleRows = 6;

        tablaDetalles.prefHeightProperty().bind(
                Bindings.when(Bindings.size(tablaDetalles.getItems()).lessThanOrEqualTo(maxVisibleRows))
                        .then(Bindings.size(tablaDetalles.getItems()).multiply(50).add(30))
                        .otherwise(maxVisibleRows * 30 + 30)
        );

    }

    @FXML
    public void onCerrar() {
        Stage stage = (Stage) tablaDetalles.getScene().getWindow();
        stage.close();
    }
}
