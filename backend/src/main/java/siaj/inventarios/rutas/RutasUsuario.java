package siaj.inventarios.rutas;

import io.javalin.Javalin;
import siaj.inventarios.controller.UsuarioController;
import siaj.inventarios.dto.*;
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

        app.post("/api/login", ctx -> {
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            LoginResponseDTO respuesta = usuarioController.login(usuario.getEmail(), usuario.getPassword());
            ctx.json(respuesta);
        });

        app.put("/api/actualizarRol", ctx -> {
            UsuarioRolDTO usuarioRolDTO = ctx.bodyAsClass(UsuarioRolDTO.class);

            int id = usuarioRolDTO.getId();
            String nuevoRol = usuarioRolDTO.getNuevoRol();

            MensajesResultados respuesta = usuarioController.actualizarRol(id, nuevoRol);
            ctx.json(respuesta);
        });

        app.get("/api/usuarios", ctx -> {
            List<UsuarioDTO> mostrar = usuarioController.listarUsuariosDTO();
            ctx.json(mostrar);
        });

        app.put("/api/cambiarPassword", ctx -> {
            UsuarioPasswordDTO usuarioPasswordDTO = ctx.bodyAsClass(UsuarioPasswordDTO.class);
            ctx.json(usuarioController.cambiarPassWord(usuarioPasswordDTO));
        });


    }
}
