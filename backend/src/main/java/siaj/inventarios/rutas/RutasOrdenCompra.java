package siaj.inventarios.rutas;
import io.javalin.Javalin;
import siaj.inventarios.controller.OrdenCompraController;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.OrdenCompra;

import java.util.List;

public class RutasOrdenCompra {
    private OrdenCompraController ordenCompraController;

    public RutasOrdenCompra(OrdenCompraController ordenCompraController) {
        this.ordenCompraController = ordenCompraController;
    }
    public RutasOrdenCompra() {}

    public void rutaOrdenCompra(Javalin app) {

        app.get("/api/OrdenCompras", ctx -> {
            List<OrdenCompra> mostrar = ordenCompraController.listarOrdenCompra();
            ctx.json(mostrar);
        });

        app.post("/api/crearOrdenCompra", ctx -> {
            OrdenCompra ordenCompra = ctx.bodyAsClass(OrdenCompra.class);
            OrdenCompra respuesta = ordenCompraController.agregarOrdenCompra(ordenCompra);
            ctx.json(respuesta);
        });

        /*app.put("/api/modificarCategoria", ctx -> {
            Categoria categoria = ctx.bodyAsClass(Categoria.class);
            MensajesResultados respuesta = categoriaController.modificarCategoria(categoria);
            ctx.json(respuesta);
        });*/
    }
}
