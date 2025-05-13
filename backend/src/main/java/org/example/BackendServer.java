package org.example;

import io.javalin.Javalin;
import siaj.inventarios.controller.ProductoController;
import siaj.inventarios.controller.UsuarioController;
import siaj.inventarios.dao.ProductoDAO;
import siaj.inventarios.dao.ProductoDAOImpl;
import siaj.inventarios.dao.UsuarioDAO;
import siaj.inventarios.dao.UsuarioDAOImpl;
import siaj.inventarios.rutas.RutasProducto;
import siaj.inventarios.rutas.RutasUsuario;
import siaj.inventarios.service.ProductoService;
import siaj.inventarios.service.ProductoServiceImpl;
import siaj.inventarios.service.UsuarioService;
import siaj.inventarios.service.UsuarioServiceImpl;

public class BackendServer {

    public static void iniciar(){

        Javalin app = Javalin.create().start(7000);

        configurarUsuarios(app);
        configurarProductos(app);

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



}