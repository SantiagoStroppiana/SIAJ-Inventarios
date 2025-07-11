package org.example;

import io.javalin.Javalin;
import siaj.inventarios.controller.*;
import siaj.inventarios.dao.*;
import siaj.inventarios.rutas.*;
import siaj.inventarios.service.*;

public class BackendServer {

    public static void iniciar(){

//        Javalin app = Javalin.create().start(7000);

        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7000"));
        Javalin app = Javalin.create().start(port);

        configurarUsuarios(app);
        configurarProductos(app);
        configurarCategorias(app);
        configurarProveedores(app);
        configurarVentas(app);
        configurarDetallesVentas(app);
        configurarMedioPagos(app);
        configurarOrdenCompra(app);
        configurarDetalleOrdenCompra(app);
        configurarEntrada(app);
        configurarDetalleEntrada(app);

    }

    public static void configurarUsuarios(Javalin app) {
        UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
        UsuarioService usuarioService = new UsuarioServiceImpl(usuarioDAO);
        UsuarioController usuarioController = new UsuarioController(usuarioService);
        new RutasUsuario(usuarioController).rutaUsuario(app);
    }

    public static void configurarProductos(Javalin app) {
        ProductoDAO productoDAO = new ProductoDAOImpl();
        ProductoService productoService = new ProductoServiceImpl(productoDAO);
        ProductoController productoController = new ProductoController(productoService);
        new RutasProducto(productoController).rutaProducto(app);
    }

    public static void configurarCategorias(Javalin app) {
        CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
        CategoriaService categoriaService = new CategoriaServiceImpl(categoriaDAO);
        CategoriaController categoriaController = new CategoriaController(categoriaService);
        new RutasCategoria(categoriaController).rutaCategoria(app);
    }

    public static void configurarProveedores(Javalin app) {
        ProveedorDAO proveedorDAO = new ProveedorDAOImpl();
        ProveedorService proveedorService = new ProveedorServiceImpl(proveedorDAO);
        ProveedorController proveedorController = new ProveedorController(proveedorService);
        new RutasProveedor(proveedorController).rutaProveedor(app);
    }

    public static void configurarVentas(Javalin app) {
        VentaDAO ventaDAO = new VentaDAOImpl();
       VentaService ventaService = new VentaServiceImpl(ventaDAO);
       VentaController ventaController = new VentaController(ventaService);
        new RutasVenta(ventaController).rutaVenta(app);
    }

    public static void configurarDetallesVentas(Javalin app) {
        DetalleVentaDAO detalleVentaDAO = new DetalleVentaDAOImpl();
        DetalleVentaService detalleVentaService = new DetalleVentaServiceImpl(detalleVentaDAO);
        DetalleVentaController detalleVentaController = new DetalleVentaController(detalleVentaService);
        new RutasDetalleVenta(detalleVentaController).rutaDetalleVenta(app);
    }


    public static void configurarMedioPagos(Javalin app) {
        MedioPagoDAO medioPagoDAO = new MedioPagoDAOImpl();
        MedioPagoService medioPagoService = new MedioPagoServiceImpl(medioPagoDAO);
        MedioPagoController medioPagoController = new MedioPagoController(medioPagoService);
        new RutasMedioPago(medioPagoController).rutaMedioPagos(app);
    }


    public static void configurarOrdenCompra(Javalin app) {
        OrdenCompraDAO ordenCompraDAO = new OrdenCompraDAOImpl();
        OrdenCompraService ordenCompraService = new OrdenCompraServiceImpl(ordenCompraDAO);
        OrdenCompraController ordenCompraController = new OrdenCompraController(ordenCompraService);
        new RutasOrdenCompra(ordenCompraController).rutaOrdenCompra(app);
    }

    public static void configurarDetalleOrdenCompra(Javalin app) {
        DetalleOrdenCompraDAO detalleOrdenCompraDAO = new DetalleOrdenCompraDAOImpl();
        DetalleOrdenCompraService detalleOrdenCompraService = new DetalleOrdenCompraServiceImpl(detalleOrdenCompraDAO);
        DetalleOrdenCompraController detalleOrdenCompraController = new DetalleOrdenCompraController(detalleOrdenCompraService);
        new RutasDetalleOrdenCompra(detalleOrdenCompraController).rutaDetalleOrdenCompra(app);
    }

    public static void configurarEntrada(Javalin app) {
        EntradaDAO entradaDAO = new EntradaDAOImpl();
        EntradaService entradaService = new EntradaServiceImpl(entradaDAO);
        EntradaController entradaController = new EntradaController(entradaService);
        new RutasEntrada(entradaController).rutaEntradas(app);
    }

    public static void configurarDetalleEntrada(Javalin app) {
        DetalleEntradaDAO detalleEntradaDAO = new DetalleEntradaDAOImpl();
        DetalleEntradaService detalleEntradaService = new DetalleEntradaServiceImpl(detalleEntradaDAO);
        DetalleEntradaController detalleEntradaController = new DetalleEntradaController(detalleEntradaService);
        new RutaDetalleEntrada(detalleEntradaController).rutaDetalleEntrada(app);
    }

}