package siaj.inventarios.controller;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Usuario;
import siaj.inventarios.service.UsuarioService;
import java.util.List;


public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController() {}

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public MensajesResultados registrarUsuario(Usuario usuario) {
        return usuarioService.registrarUsuario(usuario);
    }

    public MensajesResultados login(String email, String password) {
        return usuarioService.login(email, password);
    }

    public MensajesResultados actualizarRol(int idUsuario) { return usuarioService.actualizarRolAdmin(idUsuario); }

    public List<Usuario> listarUsuarios (){ return usuarioService.listarUsuarios(); }
}
