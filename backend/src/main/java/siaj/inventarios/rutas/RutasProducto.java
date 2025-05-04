package siaj.inventarios.rutas;
import io.javalin.Javalin;
import siaj.inventarios.controller.ProductoController;
import siaj.inventarios.controller.UsuarioController;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Producto;
import siaj.inventarios.model.Usuario;

import java.util.List;


public class RutasProducto {

    private ProductoController productoController;

    public RutasProducto() {}

    public RutasProducto(ProductoController productoController) {
        this.productoController = productoController;
    }

    public void rutaProducto(Javalin app) {

        app.get("/api/productos", ctx -> {
            List<Producto> mostrar = productoController.mostrarProductos();
            ctx.json(mostrar);
        });

        app.post("/api/crearProducto", ctx -> {
            Producto producto = ctx.bodyAsClass(Producto.class);
            String respuesta = productoController.crearProducto(producto);
            ctx.json(respuesta);
        });

        app.post("/api/modificarProducto", ctx -> {
            Producto producto = ctx.bodyAsClass(Producto.class);
            String respuesta = productoController.modificarProducto(producto);
            ctx.json(respuesta);
        });
    }



}