package org.example;

import io.javalin.Javalin;
import siaj.inventarios.controller.ProductoController;
import siaj.inventarios.controller.UsuarioController;
import siaj.inventarios.dao.ProductoDAO;
import siaj.inventarios.dao.ProductoDAOImpl;
import siaj.inventarios.dao.UsuarioDAOImpl;
import siaj.inventarios.rutas.RutasProducto;
import siaj.inventarios.rutas.RutasUsuario;
import siaj.inventarios.service.ProductoService;
import siaj.inventarios.service.ProductoServiceImpl;
import siaj.inventarios.service.UsuarioService;
import siaj.inventarios.dao.UsuarioDAO;
import siaj.inventarios.service.UsuarioServiceImpl;

public class Usuarios {

    public static void main(String[] args) {

        Javalin app = Javalin.create().start(7070);

        UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
        UsuarioService usuarioService = new UsuarioServiceImpl(usuarioDAO);
        UsuarioController usuarioController = new UsuarioController(usuarioService);
        RutasUsuario rutasUsuario = new RutasUsuario(usuarioController);
        rutasUsuario.rutaUsuario(app);

        ProductoDAO productosDAO = new ProductoDAOImpl();
        ProductoService productoService = new ProductoServiceImpl(productosDAO);
        ProductoController productoController = new ProductoController(productoService);
        RutasProducto rutasProducto = new RutasProducto(productoController);
        rutasProducto.rutaProducto(app);

    }
}
