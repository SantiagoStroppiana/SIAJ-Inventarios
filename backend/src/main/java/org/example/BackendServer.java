package org.example;

import io.javalin.Javalin;
import siaj.inventarios.controller.CategoriaController;
import siaj.inventarios.controller.ProductoController;
import siaj.inventarios.controller.UsuarioController;
import siaj.inventarios.dao.*;
import siaj.inventarios.rutas.RutasCategoria;
import siaj.inventarios.rutas.RutasProducto;
import siaj.inventarios.rutas.RutasUsuario;
import siaj.inventarios.service.*;

public class BackendServer {

    public static void iniciar(){

        Javalin app = Javalin.create().start(7000);

        configurarUsuarios(app);
        configurarProductos(app);
        configurarCategorias(app);

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



}