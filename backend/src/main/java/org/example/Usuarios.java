package org.example;

import io.javalin.Javalin;
import siaj.inventarios.controller.UsuarioController;
import siaj.inventarios.dao.UsuarioDAOImpl;
import siaj.inventarios.rutas.RutasUsuario;
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
    }
}
