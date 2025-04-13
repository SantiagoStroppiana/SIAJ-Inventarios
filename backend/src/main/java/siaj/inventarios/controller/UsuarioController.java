package siaj.inventarios.controller;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Usuario;
import siaj.inventarios.service.UsuarioService;

public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public MensajesResultados registrarUsuario(Usuario usuario) {
        return usuarioService.registrarUsuario(usuario);
    }

    public MensajesResultados login(String email, String password) {
        return usuarioService.login(email, password);
    }

    

}
