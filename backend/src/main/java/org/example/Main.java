package org.example;

import io.javalin.Javalin;
import siaj.inventarios.dao.ProductoDAOImpl;
import siaj.inventarios.dao.UsuarioDAOImpl;
import siaj.inventarios.model.Producto;
import siaj.inventarios.model.Usuario;
import siaj.inventarios.service.ProductoService;
import siaj.inventarios.service.ProductoServiceImpl;
import siaj.inventarios.service.UsuarioService;
import siaj.inventarios.service.UsuarioServiceImpl;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        BackendServer.iniciar();

    }
}