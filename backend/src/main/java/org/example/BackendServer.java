package org.example;

import io.javalin.Javalin;
import siaj.inventarios.controller.*;
import siaj.inventarios.dao.*;
import siaj.inventarios.rutas.*;
import siaj.inventarios.service.*;

public class BackendServer {

    public static void iniciar(){

        Javalin app = Javalin.create().start(7000);

        configurarUsuarios(app);
        configurarProductos(app);
        configurarCategorias(app);
        configurarProveedores(app);
        configurarVentas(app);


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

}