package siaj.inventarios.rutas;

import io.javalin.Javalin;
import siaj.inventarios.controller.MedioPagoController;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Categoria;
import siaj.inventarios.model.MedioPago;

import java.util.List;

public class RutasMedioPago {

    private MedioPagoController medioPagoController;

    public RutasMedioPago(){}

    public RutasMedioPago(MedioPagoController medioPagoController) {
        this.medioPagoController = medioPagoController;
    }


    public void rutaMedioPagos(Javalin app) {

        app.get("/api/medioPagos", ctx -> {
            List<MedioPago> mostrar = medioPagoController.listarMedioPago();
            ctx.json(mostrar);
        });

        app.post("/api/crearMedioPago", ctx -> {
            MedioPago mediopago = ctx.bodyAsClass(MedioPago.class);
            MensajesResultados respuesta = medioPagoController.crearMedioPago(mediopago);
            ctx.json(respuesta);
        });

        /*app.put("/api/modificarCategoria", ctx -> {
            Categoria categoria = ctx.bodyAsClass(Categoria.class);
            MensajesResultados respuesta = categoriaController.modificarCategoria(categoria);
            ctx.json(respuesta);
        });*/
    }
}
