package siaj.inventarios.rutas;

import io.javalin.Javalin;
import siaj.inventarios.controller.UsuarioController;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Usuario;

public class RutasUsuario {

    private UsuarioController usuarioController;

    public RutasUsuario() {}

    public RutasUsuario(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }

    public void rutaUsuario(Javalin app) {

        app.post("/api/register", ctx -> {
           Usuario usuario = ctx.bodyAsClass(Usuario.class);
            MensajesResultados respuesta = usuarioController.registrarUsuario(usuario);
            ctx.json(respuesta);
        });

        app.post("/api/login", ctx -> {
           Usuario usuario = ctx.bodyAsClass(Usuario.class);
           MensajesResultados respuesta = usuarioController.login(usuario.getEmail(), usuario.getPassword());
           ctx.json(respuesta);
        });

    }
}
