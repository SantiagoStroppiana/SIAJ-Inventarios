package siaj.inventarios.rutas;

import io.javalin.Javalin;
import siaj.inventarios.controller.CategoriaController;
import siaj.inventarios.controller.ProductoController;
import siaj.inventarios.model.Categoria;
import siaj.inventarios.model.Producto;

import java.util.List;

public class RutasCategoria {

    private CategoriaController categoriaController;

    public RutasCategoria() {}

    public RutasCategoria(CategoriaController categoriaController) {
        this.categoriaController = categoriaController;
    }
    public void rutaCategoria(Javalin app) {

        app.get("/api/categorias", ctx -> {
            List<Categoria> mostrar = categoriaController.mostrarCategorias();
            ctx.json(mostrar);
        });
/*
        app.post("/api/crearProducto", ctx -> {
            Producto producto = ctx.bodyAsClass(Producto.class);
            String respuesta = productoController.crearProducto(producto);
            ctx.json(respuesta);
        });

        app.post("/api/modificarProducto", ctx -> {
            Producto producto = ctx.bodyAsClass(Producto.class);
            String respuesta = productoController.modificarProducto(producto);
            ctx.json(respuesta);
        });
    }*/




}}
