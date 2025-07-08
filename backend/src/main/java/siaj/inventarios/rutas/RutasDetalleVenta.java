package siaj.inventarios.rutas;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import siaj.inventarios.controller.DetalleVentaController;
import siaj.inventarios.dto.DetalleVentaDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.dto.VentaDTO;
import siaj.inventarios.model.DetalleVenta;

import java.util.List;

public class RutasDetalleVenta {
    private static DetalleVentaController detalleVentaController;

    public RutasDetalleVenta() { }

    public RutasDetalleVenta(DetalleVentaController detalleVentaController) {
        this.detalleVentaController = detalleVentaController;
    }

    public static void rutaDetalleVenta(Javalin app) {
        app.post("/api/crear-detalle-venta", ctx -> {
            DetalleVenta detalle = ctx.bodyAsClass(DetalleVenta.class);
            MensajesResultados respuesta = detalleVentaController.crear(detalle);
            ctx.json(respuesta);

        });

        app.get("/api/detalle-ventas", ctx -> {
            List<DetalleVentaDTO> mostrar = detalleVentaController.obtenerDetalles();
            ctx.json(mostrar);
        });

       /* app.get("/api/detalle-venta/venta/:ventaId", ctx -> {
            int ventaId = Integer.parseInt(ctx.pathParam("ventaId"));
            ctx.json(detalleVentaController.obtenerPorVenta(ventaId));
        });*/
    }
}