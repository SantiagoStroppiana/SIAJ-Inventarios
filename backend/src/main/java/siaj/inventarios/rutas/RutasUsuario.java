package siaj.inventarios.rutas;

import io.javalin.Javalin;
import siaj.inventarios.controller.UsuarioController;
import siaj.inventarios.dto.LoginResponseDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Usuario;

import java.util.List;

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

//        app.post("/api/login", ctx -> {
//           Usuario usuario = ctx.bodyAsClass(Usuario.class);
//           MensajesResultados respuesta = usuarioController.login(usuario.getEmail(), usuario.getPassword());
//           ctx.json(respuesta);
//        });

        app.post("/api/login", ctx -> {
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            LoginResponseDTO respuesta = usuarioController.login(usuario.getEmail(), usuario.getPassword());
            ctx.json(respuesta);
        });

        app.put("/api/actualizarRol", ctx -> {
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            MensajesResultados respuesta = usuarioController.actualizarRol(usuario.getId());
            ctx.json(respuesta);
        });

        app.get("/api/usuarios", ctx -> {
            List<Usuario> mostrar = usuarioController.listarUsuarios();
            ctx.json(mostrar);
        });

    }
}
