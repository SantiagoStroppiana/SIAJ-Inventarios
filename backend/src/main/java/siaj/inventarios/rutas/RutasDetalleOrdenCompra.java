package siaj.inventarios.rutas;

import io.javalin.Javalin;
import siaj.inventarios.controller.DetalleOrdenCompraController;
import siaj.inventarios.dto.DetalleOrdenCompraDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.DetalleOrdenCompra;

import java.util.List;

public class RutasDetalleOrdenCompra {
    public static DetalleOrdenCompraController detalleOrdenCompraController;

    public RutasDetalleOrdenCompra() {}
    public RutasDetalleOrdenCompra(DetalleOrdenCompraController detalleOrdenCompraController) {
        this.detalleOrdenCompraController = detalleOrdenCompraController;
    }

    public static void rutaDetalleOrdenCompra(Javalin app) {
        app.post("/api/crear-detalle-orden", ctx -> {
            DetalleOrdenCompraDTO detalle = ctx.bodyAsClass(DetalleOrdenCompraDTO.class);
            MensajesResultados respuesta = detalleOrdenCompraController.crear(detalle);
            ctx.json(respuesta);
        });

        app.get("/api/detalles-orden", ctx -> {
            List<DetalleOrdenCompraDTO> mostrar = detalleOrdenCompraController.obtenerDetalles();
            ctx.json(mostrar);
        });
    }

}
