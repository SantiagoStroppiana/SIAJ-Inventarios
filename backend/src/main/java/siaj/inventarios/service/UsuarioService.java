package siaj.inventarios.service;

import siaj.inventarios.dto.LoginResponseDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Usuario;
import java.util.List;
public interface UsuarioService {

//    MensajesResultados login(String email, String password);
    LoginResponseDTO login(String email, String password);
    MensajesResultados registrarUsuario(Usuario usuario);
    MensajesResultados actualizarRolAdmin(int idUsuario);
    List<Usuario> listarUsuarios();
    MensajesResultados cambiarPassword(String oldPassword, String newPassword);
}
