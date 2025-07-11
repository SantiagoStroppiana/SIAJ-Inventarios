package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import org.example.desktop.model.Producto;
import org.example.desktop.util.VariablesEntorno;
import org.example.desktop.dto.DetalleVentaDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GraficoController {

    private static final Logger LOGGER = Logger.getLogger(GraficoController.class.getName());
    private static final int MAX_PRODUCTOS_MOSTRAR = 15;
    private static final String PRODUCTO_NO_ESPECIFICADO = "Producto No Especificado";
    private static final String PRODUCTO_DESCONOCIDO = "Producto Desconocido";

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private Button actualizarButton;

    @FXML
    private ProgressIndicator progressIndicator;

    private final HttpClient httpClient;
    private final Gson gson;

    public GraficoController() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    @FXML
    public void initialize() {
        configurarGrafico();
        configurarComponentes();
        cargarDatosVentasAsync();
    }

    private void configurarGrafico() {
        CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();

        xAxis.setLabel("🛍️ Productos");
        yAxis.setLabel("📊 Cantidad Vendida");

        //yAxis.setTickLabelRotation(0);
        xAxis.setAutoRanging(true);
        xAxis.setTickLabelRotation(45); // En lugar de 45



        barChart.setTitle("🏆 Top " + MAX_PRODUCTOS_MOSTRAR + " Productos Más Vendidos");

        barChart.setLegendVisible(false);
        barChart.setAnimated(true);

    }

    private void configurarComponentes() {
        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        if (actualizarButton != null) {
            actualizarButton.setOnAction(event -> actualizarDatos());
        }
    }

    private void cargarDatosVentasAsync() {
        Task<VentasData> task = new Task<VentasData>() {
            @Override
            protected VentasData call() throws Exception {
                return obtenerDatosVentas();
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    try {
                        VentasData data = getValue();
                        procesarYMostrarVentas(data);
                        ocultarIndicadorCarga();
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error al procesar datos de ventas", e);
                        mostrarError("Error de procesamiento", "Error al procesar los datos: " + e.getMessage());
                        ocultarIndicadorCarga();
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Throwable exception = getException();
                    LOGGER.log(Level.SEVERE, "Error al cargar datos de ventas", exception);
                    mostrarError("Error de conexión", "No se pudieron cargar los datos: " + exception.getMessage());
                    ocultarIndicadorCarga();
                });
            }
        };

        mostrarIndicadorCarga();
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    Producto[] productos;
    DetalleVentaDTO[] detallesVenta;
    private VentasData obtenerDatosVentas() throws Exception {
        String baseUrl = VariablesEntorno.getServerURL();

// Primero obtenemos los productos
        productos = obtenerProductos(baseUrl);

// Luego los detalles de venta
        detallesVenta = obtenerDetallesVenta(baseUrl);


        LOGGER.info(String.format("Datos cargados - Productos: %d, Detalles venta: %d",
                productos.length, detallesVenta.length));

        return new VentasData(productos, detallesVenta);
    }

    private Producto[] obtenerProductos(String baseUrl) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/productos"))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        validarRespuestaHttp(response, "productos");

        return gson.fromJson(response.body(), Producto[].class);
    }

    private DetalleVentaDTO[] obtenerDetallesVenta(String baseUrl) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/detalle-ventas"))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        validarRespuestaHttp(response, "detalles de venta");

        return gson.fromJson(response.body(), DetalleVentaDTO[].class);
    }

    private void validarRespuestaHttp(HttpResponse<String> response, String tipo) throws Exception {
        if (response.statusCode() != 200) {
            throw new Exception(String.format("Error HTTP %d al obtener %s", response.statusCode(), tipo));
        }

        if (response.body() == null || response.body().trim().isEmpty()) {
            throw new Exception(String.format("Respuesta vacía al obtener %s", tipo));
        }
    }

    private void procesarYMostrarVentas(VentasData data) {
        Map<String, Integer> ventasPorProducto = calcularVentasPorProducto(data);

        if (ventasPorProducto.isEmpty()) {
            mostrarMensajeNoHayDatos();
            return;
        }

        actualizarGrafico(ventasPorProducto);

        // Log de estadísticas
        List<String> productosConVentas = ventasPorProducto.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> entry.getKey() + " (" + entry.getValue() + " unidades)")
                .collect(Collectors.toList());

        //LOGGER.info(String.format("Gráfico actualizado - %d productos con ventas: %s",
        //        productosConVentas.size(), productosConVentas));
    }

    private Map<String, Integer> calcularVentasPorProducto(VentasData data) {
        Map<Integer, String> nombresProductos = Arrays.stream(data.productos)
                .collect(Collectors.toMap(Producto::getId, Producto::getNombre));

        Map<String, Integer> ventasPorProducto = new HashMap<>();
        int detallesConProblemas = 0;

        for (DetalleVentaDTO detalle : data.detallesVenta) {
            if (detalle.getProductoId() != 0) {
                String nombreProducto = nombresProductos.getOrDefault(
                        detalle.getProductoId(), PRODUCTO_DESCONOCIDO);
                ventasPorProducto.merge(nombreProducto, detalle.getCantidad(), Integer::sum);
            } else {
                detallesConProblemas++;
                ventasPorProducto.merge(PRODUCTO_NO_ESPECIFICADO, detalle.getCantidad(), Integer::sum);
            }
        }

        if (detallesConProblemas > 0) {
            LOGGER.warning(String.format("Se encontraron %d detalles con producto ID = 0", detallesConProblemas));
        }

        return ventasPorProducto;
    }

    private void actualizarGrafico(Map<String, Integer> ventasPorProducto) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventas por Producto");

        List<Map.Entry<String, Integer>> topVentas = ventasPorProducto.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(MAX_PRODUCTOS_MOSTRAR)
                .collect(Collectors.toList());

        for (Map.Entry<String, Integer> entry : topVentas) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(data);
        }

        barChart.getData().clear();
        barChart.getData().add(series);

        // Aplicar estilos a las barras después de que se rendericen
        Platform.runLater(() -> {
            barChart.applyCss();
            barChart.layout();
            aplicarEstilosBarras(series);
        });

    }

    private void aplicarEstilosBarras(XYChart.Series<String, Number> series) {
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<String, Number> data = series.getData().get(i);
            if (data.getNode() != null) {
                // Usar colores sólidos en lugar de gradientes para evitar ClassCastException
                String[] colores = {"#2196F3", "#4CAF50", "#FF9800", "#9C27B0", "#F44336", "#00BCD4"};
                String color = colores[i % colores.length];
                data.getNode().setStyle("-fx-bar-fill: " + color + ";");
            }
        }
    }

    private void mostrarMensajeNoHayDatos() {
        barChart.getData().clear();
        barChart.setTitle("No hay datos de ventas disponibles");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sin datos");
        alert.setHeaderText(null);
        alert.setContentText("No se encontraron datos de ventas para mostrar.");
        alert.showAndWait();
    }

    private void mostrarIndicadorCarga() {
        if (progressIndicator != null) {
            progressIndicator.setVisible(true);
        }
        if (actualizarButton != null) {
            actualizarButton.setDisable(true);
        }
    }

    private void ocultarIndicadorCarga() {
        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }
        if (actualizarButton != null) {
            actualizarButton.setDisable(false);
        }
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
        cargarDatosVentasAsync();
    }

    // Clase interna para encapsular los datos
    private static class VentasData {
        final Producto[] productos;
        final DetalleVentaDTO[] detallesVenta;

        VentasData(Producto[] productos, DetalleVentaDTO[] detallesVenta) {
            this.productos = productos;
            this.detallesVenta = detallesVenta;
        }
    }
}