package siaj.inventarios.rutas;

import io.javalin.Javalin;
import siaj.inventarios.controller.ProveedorController;
import siaj.inventarios.controller.VentaController;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.dto.VentaDTO;
import siaj.inventarios.model.Proveedor;
import siaj.inventarios.model.Venta;

import java.util.List;



public class RutasVenta {
    private VentaController ventaController;

    public RutasVenta() {}


    public RutasVenta(VentaController ventaController) {
        this.ventaController = ventaController;
    }

    public void rutaVenta(Javalin app) {

        app.get("/api/ventas", ctx -> {
            List<VentaDTO> mostrar = ventaController.getVentas();
            ctx.json(mostrar);
        });

        app.post("/api/crearVenta", ctx -> {
            Venta venta = ctx.bodyAsClass(Venta.class);
            MensajesResultados respuesta = ventaController.crearVenta(venta);
            ctx.json(respuesta);
        });

        app.put("/api/modificarVenta", ctx -> {
            Venta venta = ctx.bodyAsClass(Venta.class);
            MensajesResultados respuesta = ventaController.actualizarVenta(venta);
            ctx.json(respuesta);
        });
    }


}
