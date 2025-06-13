package siaj.inventarios.rutas;

import io.javalin.Javalin;
import siaj.inventarios.controller.CategoriaController;
import siaj.inventarios.controller.ProductoController;
import siaj.inventarios.dto.MensajesResultados;
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

        app.post("/api/crearCategoria", ctx -> {
            Categoria categoria = ctx.bodyAsClass(Categoria.class);
            MensajesResultados respuesta = categoriaController.crearCategoria(categoria);
            ctx.json(respuesta);
        });

        app.put("/api/modificarCategoria", ctx -> {
            Categoria categoria = ctx.bodyAsClass(Categoria.class);
            MensajesResultados respuesta = categoriaController.modificarCategoria(categoria);
            ctx.json(respuesta);
        });
    }




}
