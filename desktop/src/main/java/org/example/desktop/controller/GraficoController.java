package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import org.example.desktop.model.DetalleVenta;
import org.example.desktop.model.Producto;
import org.example.desktop.util.VariablesEntorno;
import org.example.desktop.dto.DetalleVentaDTO;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class GraficoController {

    @FXML
    private BarChart<String, Number> barChart;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        configurarGrafico();
        cargarDatosVentas();
    }

    private void configurarGrafico() {
        CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();

        xAxis.setLabel("Productos");
        yAxis.setLabel("Cantidad Vendida");

        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
    }

    @FXML
    private void cargarDatosVentas() {
        try {
            // 1. Obtener todos los productos
            HttpRequest productosRequest = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/productos"))
                    .GET()
                    .build();

            HttpResponse<String> productosResponse = httpClient.send(productosRequest, HttpResponse.BodyHandlers.ofString());
            Producto[] productos = gson.fromJson(productosResponse.body(), Producto[].class);

            // 2. Obtener todos los detalles de venta
            HttpRequest ventasRequest = HttpRequest.newBuilder()
                    .uri(URI.create(VariablesEntorno.getServerURL() + "/api/detalle-ventas"))
                    .GET()
                    .build();

            HttpResponse<String> ventasResponse = httpClient.send(ventasRequest, HttpResponse.BodyHandlers.ofString());
            DetalleVentaDTO[] detallesVenta = gson.fromJson(ventasResponse.body(), DetalleVentaDTO[].class);

            // Verificación de datos
            System.out.println("Total productos: " + productos.length);
            System.out.println("Total detalles venta: " + detallesVenta.length);

            int detallesConProblemas = 0;
            for (DetalleVentaDTO detalle : detallesVenta) {
                if (detalle.getProductoId() == 0) {
                    detallesConProblemas++;
                }
            }
            System.out.println("Detalles con producto null: " + detallesConProblemas);

            // 3. Procesar y mostrar los datos
            mostrarVentasEnGrafico(productos, detallesVenta);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar ventas", "No se pudieron obtener los datos de ventas: " + e.getMessage());
        }
    }

    private void mostrarVentasEnGrafico(Producto[] productos, DetalleVentaDTO[] detallesVenta) {
        // Mapa para acumular las cantidades vendidas por producto
        Map<String, Integer> ventasPorProducto = new HashMap<>();

        // Mapa para relacionar IDs de productos con sus nombres
        Map<Integer, String> nombresProductos = new HashMap<>();
        for (Producto producto : productos) {
            nombresProductos.put(producto.getId(), producto.getNombre());
        }

        // Sumar las cantidades vendidas por producto (con validación de null)
        for (DetalleVentaDTO detalle : detallesVenta) {
            // Verificar que el producto no sea null
            if (detalle.getProductoId() != 0) {
                String nombreProducto = nombresProductos.getOrDefault(detalle.getProductoId(), "Producto Desconocido");
                int cantidad = detalle.getCantidad();
                ventasPorProducto.merge(nombreProducto, cantidad, Integer::sum);
            } else {
                System.err.println("DetalleVenta con ID " + detalle.getId() + " tiene producto null");
                // Opcional: Contabilizar como "Producto No Especificado"
                ventasPorProducto.merge("Producto No Especificado", detalle.getCantidad(), Integer::sum);
            }
        }

        // Crear serie de datos para el gráfico
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventas");

        // Ordenar por cantidad descendente y agregar al gráfico
        ventasPorProducto.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(15) // Mostrar solo los 15 productos más vendidos
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                });

        // Actualizar el gráfico
        barChart.getData().clear();
        barChart.getData().add(series);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void actualizarDatos() {
        cargarDatosVentas();
    }
}