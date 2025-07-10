package siaj.inventarios.rutas;

import io.javalin.Javalin;
import siaj.inventarios.controller.EntradaController;
import siaj.inventarios.controller.OrdenCompraController;
import siaj.inventarios.dto.OrdenCompraDTO;
import siaj.inventarios.model.Entrada;
import siaj.inventarios.model.OrdenCompra;

import java.util.List;

public class RutasEntrada {



    private EntradaController entradaController;

    public RutasEntrada(EntradaController entradaController) {
        this.entradaController = entradaController;
    }
    public RutasEntrada() {}

    public void rutaEntradas(Javalin app) {

        app.get("/api/entradas", ctx -> {
            List<Entrada> mostrar = entradaController.listarEntradas();
            ctx.json(mostrar);
        });

        /*app.post("/api/crearOrdenCompra", ctx -> {
            OrdenCompra ordenCompra = ctx.bodyAsClass(OrdenCompra.class);
            OrdenCompra respuesta = ordenCompraController.agregarOrdenCompra(ordenCompra);
            ctx.json(respuesta);
        });*/

        app.post("/api/crear-entrada", ctx -> {
            Entrada entrada = ctx.bodyAsClass(Entrada.class);
            Entrada respuesta = entradaController.agregarEntrada(entrada);
            ctx.json(respuesta);
        });

        /*app.put("/api/modificarCategoria", ctx -> {
            Categoria categoria = ctx.bodyAsClass(Categoria.class);
            MensajesResultados respuesta = categoriaController.modificarCategoria(categoria);
            ctx.json(respuesta);
        });*/
    }





}
