package siaj.inventarios.rutas;
import io.javalin.Javalin;
import siaj.inventarios.controller.ProveedorController;
import siaj.inventarios.controller.UsuarioController;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Proveedor;
import siaj.inventarios.model.Usuario;

import java.util.List;


public class RutasProveedor {

    private ProveedorController proveedorController;

    public RutasProveedor() {}

    public RutasProveedor(ProveedorController proveedorController) {
        this.proveedorController = proveedorController;
    }

    public void rutaProveedor(Javalin app) {

        app.get("/api/proveedores", ctx -> {
            List<Proveedor> mostrar = proveedorController.mostrarProveedores();
            ctx.json(mostrar);
        });

        app.post("/api/crearProveedor", ctx -> {
            Proveedor proveedor = ctx.bodyAsClass(Proveedor.class);
            MensajesResultados respuesta = proveedorController.registrarProveedor(proveedor);
            ctx.json(respuesta);
        });

        app.post("/api/actualizarProveedor", ctx -> {
            Proveedor proveedor = ctx.bodyAsClass(Proveedor.class);
            MensajesResultados respuesta = proveedorController.actualizarProveedor(proveedor);
            ctx.json(respuesta);
        });
    }



}