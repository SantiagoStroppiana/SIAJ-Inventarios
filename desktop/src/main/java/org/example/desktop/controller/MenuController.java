package org.example.desktop.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.desktop.dto.DetalleVentaDTO;
import org.example.desktop.dto.UsuarioDTO;
import org.example.desktop.model.Producto;
import org.example.desktop.util.StageManager;
import org.example.desktop.util.UserSession;
import org.example.desktop.util.VariablesEntorno;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MenuController {

    private static final Logger LOGGER = Logger.getLogger(MenuController.class.getName());
    private static final int MAX_PRODUCTOS_MOSTRAR = 5; // Para el pie chart del menú principal
    private static final String PRODUCTO_NO_ESPECIFICADO = "Producto No Especificado";
    private static final String PRODUCTO_DESCONOCIDO = "Producto Desconocido";

    @FXML private Label labelBienvenida;
    @FXML private Button boton;
    @FXML private ImageView icono;
    @FXML private Label texto;

    @FXML private PieChart miniPieChart;
    @FXML private Label lblMiniTotalVentas;
    @FXML private Label lblMiniTopProducto;

    private final UsuarioDTO usuario = UserSession.getUsuarioActual();
    private final HttpClient httpClient;
    private final Gson gson;

    public MenuController() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    @FXML private Label lblUnidadesStock;
    @FXML private Label lblValorInventario;
    @FXML private Label lblProductosCriticos;

    private void infoGeneral() {
        Task<Producto[]> task = new Task<>() {
            @Override
            protected Producto[] call() throws Exception {
                String baseUrl = VariablesEntorno.getServerURL();
                return obtenerProductos(baseUrl);
            }

            @Override
            protected void succeeded() {
                Producto[] productos = getValue();

                int stockTotal = Arrays.stream(productos)
                        .mapToInt(Producto::getStock)
                        .sum();

                BigDecimal valorTotal = Arrays.stream(productos)
                        .map(p -> p.getPrecioCosto().multiply(BigDecimal.valueOf(p.getStock())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                long productosCriticos = Arrays.stream(productos)
                        .filter(p -> p.getStock() <= 5)
                        .count();

                Platform.runLater(() -> {
                    NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "PY"));
                    lblUnidadesStock.setText(stockTotal + " unidades");
                    lblValorInventario.setText("₲ " + nf.format(valorTotal));
                    lblProductosCriticos.setText(productosCriticos + " productos");
                });
            }

            @Override
            protected void failed() {
                LOGGER.log(Level.WARNING, "Error al cargar datos generales del inventario", getException());
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


    @FXML
    public void initialize() {
        configurarBoton();
        if(usuario != null){
            String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
            labelBienvenida.setText("Bienvenido, " + nombreCompleto + " (" + usuario.getNombreRol() + ")");
        }

        configurarGraficoTorta();
        cargarDatosVentasAsync();
        infoGeneral();
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

    private void configurarGraficoTorta() {
        if (miniPieChart != null) {
            miniPieChart.setTitle("🥧 TOP 5 Productos Más Vendidos");
            miniPieChart.setLegendVisible(true);
            miniPieChart.setLabelsVisible(false);
            miniPieChart.setAnimated(true);
            miniPieChart.setClockwise(true);
        }

        if (lblMiniTotalVentas != null) {
            lblMiniTotalVentas.setText("0");
        }
        if (lblMiniTopProducto != null) {
            lblMiniTopProducto.setText("-");
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
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Error al procesar datos de ventas para el menú", e);
                        mostrarGraficoVacio("Error al cargar datos");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Throwable exception = getException();
                    LOGGER.log(Level.WARNING, "Error al cargar datos de ventas para el menú", exception);
                    mostrarGraficoVacio("Sin conexión");
                });
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private VentasData obtenerDatosVentas() throws Exception {
        String baseUrl = VariablesEntorno.getServerURL();

        Producto[] productos = obtenerProductos(baseUrl);

        DetalleVentaDTO[] detallesVenta = obtenerDetallesVenta(baseUrl);

        LOGGER.info(String.format("Datos cargados para menú - Productos: %d, Detalles venta: %d",
                productos.length, detallesVenta.length));

        return new VentasData(productos, detallesVenta);
    }

    private Producto[] obtenerProductos(String baseUrl) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/productos"))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        validarRespuestaHttp(response, "productos");

        return gson.fromJson(response.body(), Producto[].class);
    }

    private DetalleVentaDTO[] obtenerDetallesVenta(String baseUrl) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/detalle-ventas"))
                .timeout(Duration.ofSeconds(15))
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
            mostrarGraficoVacio("Sin ventas registradas");
            return;
        }

        actualizarGraficoTorta(ventasPorProducto);
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

    private void actualizarGraficoTorta(Map<String, Integer> ventasPorProducto) {
        if (miniPieChart == null) return;

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        List<Map.Entry<String, Integer>> topVentas = ventasPorProducto.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(MAX_PRODUCTOS_MOSTRAR)
                .collect(Collectors.toList());

        int totalVentas = 0;
        String topProducto = "";
        int maxVentas = 0;

        // Crear datos del gráfico de torta
        for (Map.Entry<String, Integer> entry : topVentas) {
            String nombreProducto = entry.getKey();
            int cantidadVendida = entry.getValue();

            pieChartData.add(new PieChart.Data(nombreProducto, cantidadVendida));
            totalVentas += cantidadVendida;

            if (cantidadVendida > maxVentas) {
                maxVentas = cantidadVendida;
                topProducto = nombreProducto;
            }
        }

        miniPieChart.setData(pieChartData);

        if (lblMiniTotalVentas != null) {
            lblMiniTotalVentas.setText(String.valueOf(totalVentas));
        }
        if (lblMiniTopProducto != null) {
            lblMiniTopProducto.setText(topProducto);
        }

        final int finalTotalVentas = totalVentas;
        for (PieChart.Data data : pieChartData) {
            data.getNode().setOnMouseEntered(e -> {
                double porcentaje = (data.getPieValue() / finalTotalVentas) * 100;
                miniPieChart.setTitle(String.format("%s: %.1f%% (%d unidades)",
                        data.getName(), porcentaje, (int)data.getPieValue()));
            });

            data.getNode().setOnMouseExited(e -> {
                miniPieChart.setTitle("🥧 TOP 5 Productos Más Vendidos");
            });
        }

        LOGGER.info(String.format("Gráfico de torta actualizado - Total ventas: %d, Top producto: %s",
                totalVentas, topProducto));
    }

    private void mostrarGraficoVacio(String mensaje) {
        if (miniPieChart == null) return;

        ObservableList<PieChart.Data> emptyData = FXCollections.observableArrayList();
        emptyData.add(new PieChart.Data(mensaje, 1));
        miniPieChart.setData(emptyData);
        miniPieChart.setTitle(mensaje);

        if (lblMiniTotalVentas != null) {
            lblMiniTotalVentas.setText("0");
        }
        if (lblMiniTopProducto != null) {
            lblMiniTopProducto.setText("-");
        }
    }
    public void refrescarDatos() {
        cargarDatosVentasAsync();
    }

    @FXML
    public void irPerfilUsuario() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/desktop/perfil-usuario-view.fxml"));

        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 800, 750));
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
        StageManager.loadScene("/org/example/desktop/orden-compra-view.fxml" , 1600, 900);
    }

    @FXML
    public void irInventario() {
        StageManager.loadScene("/org/example/desktop/productos-view.fxml" , 1600, 900);
    }

    @FXML
    public void irProveedores() {
        StageManager.loadScene("/org/example/desktop/proveedores-view.fxml" , 1600, 900);
    }

    @FXML
    public void irReportes() {
        StageManager.loadScene("/org/example/desktop/graficos-ventas-view.fxml" , 1600, 900);
    }

    private static class VentasData {
        final Producto[] productos;
        final DetalleVentaDTO[] detallesVenta;

        VentasData(Producto[] productos, DetalleVentaDTO[] detallesVenta) {
            this.productos = productos;
            this.detallesVenta = detallesVenta;
        }
    }
}