package siaj.inventarios.controller;

import siaj.inventarios.dto.LoginResponseDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.dto.UsuarioDTO;
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

    public LoginResponseDTO login(String email, String password) {
        return usuarioService.login(email, password);
    }

    public MensajesResultados actualizarRol(int idUsuario, String nuevoRol) {

        return usuarioService.actualizarRol(idUsuario, nuevoRol);
    }

    public List<UsuarioDTO> listarUsuariosDTO() {
    List<Usuario> usuarios = usuarioService.listarUsuarios(); // o como lo estés obteniendo

    return usuarios.stream()
            .map(usuario -> new UsuarioDTO(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getEmail(),
                    (usuario.getRolId() != null) ? usuario.getRolId().getNombre() : "Sin rol"
            ))
            .toList();
}

    public MensajesResultados cambiarPassWord(String oldPassword, String newPassword) { return usuarioService.cambiarPassword(oldPassword, newPassword); }
}
