package siaj.inventarios.rutas;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import siaj.inventarios.controller.DetalleVentaController;
import siaj.inventarios.dto.DetalleVentaDTO;
import siaj.inventarios.model.DetalleVenta;

public class RutasDetalleVenta {
    private static DetalleVentaController detalleVentaController;

    public RutasDetalleVenta() { }

    public RutasDetalleVenta(DetalleVentaController detalleVentaController) {
        this.detalleVentaController = detalleVentaController;
    }

    public static void rutaDetalleVenta(Javalin app) {
        app.post("/api/detalle-venta", ctx -> {
            DetalleVentaDTO detalle = ctx.bodyAsClass(DetalleVentaDTO.class);
            detalleVentaController.crear(detalle);
            ctx.status(HttpStatus.CREATED);
        });

        app.get("/api/detalle-venta/venta/:ventaId", ctx -> {
            int ventaId = Integer.parseInt(ctx.pathParam("ventaId"));
            ctx.json(detalleVentaController.obtenerPorVenta(ventaId));
        });
    }
}