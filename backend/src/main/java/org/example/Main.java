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

        UsuarioService usuarioService = new UsuarioServiceImpl(new UsuarioDAOImpl());

        Javalin app = Javalin.create().start(7000);

        app.get("/usuarios", ctx -> {
            List<Usuario> usuarios = usuarioService.listarUsuarios();
            for (Usuario u : usuarios) {
                System.out.println("Nombre: " + u.getNombre() + " - Email: " + u.getEmail());
            }
            ctx.result("Consulta realizada sobre Usuarios. Ver consola.");
        });

        ProductoService productoService = new ProductoServiceImpl(new ProductoDAOImpl());

         app.get("/productos", ctx -> {
            List<Producto> productos = productoService.listarProductos();
            for (Producto p : productos) {
                System.out.println("Nombre: " + p.getNombre());
            }
            ctx.result("Consulta realizada sobre Productos. Ver consola.");
        });
    }
}