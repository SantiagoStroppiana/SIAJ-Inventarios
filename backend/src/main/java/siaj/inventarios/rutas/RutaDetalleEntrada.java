package siaj.inventarios.rutas;

import io.javalin.Javalin;
import siaj.inventarios.controller.CategoriaController;
import siaj.inventarios.controller.DetalleEntradaController;
import siaj.inventarios.dto.DetalleEntradaDTO;
import siaj.inventarios.model.DetalleEntrada;
import siaj.inventarios.model.Entrada;
import siaj.inventarios.model.Producto;

import java.math.BigDecimal;
import java.util.List;

public class RutaDetalleEntrada {

    private DetalleEntradaController detalleEntradaController;

    public RutaDetalleEntrada() {}

    public RutaDetalleEntrada(DetalleEntradaController detalleEntradaController) {
        this.detalleEntradaController = detalleEntradaController;
    }


    public void rutaDetalleEntrada(Javalin app) {
        app.post("/api/crear-detalle-entrada", ctx -> {
            detalleEntradaController.registrarDetalleEntrada(ctx);
        });
        app.get("/api/detalles-entradas", ctx -> {
            List<DetalleEntradaDTO> mostrar = detalleEntradaController.listarDetalleEntradas();
            ctx.json(mostrar);
        });
    }


}
