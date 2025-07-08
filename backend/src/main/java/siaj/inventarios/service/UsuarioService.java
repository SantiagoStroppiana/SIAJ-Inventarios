package siaj.inventarios.service;

import siaj.inventarios.dto.LoginResponseDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.dto.UsuarioForgetPasswordDTO;
import siaj.inventarios.dto.UsuarioPasswordDTO;
import siaj.inventarios.model.Usuario;
import java.util.List;
public interface UsuarioService {

    LoginResponseDTO login(String email, String password);
    MensajesResultados registrarUsuario(Usuario usuario);
    MensajesResultados actualizarRol(int idUsuario, String nuevoRol);
    List<Usuario> listarUsuarios();
    MensajesResultados cambiarPassword(UsuarioPasswordDTO usuarioPasswordDTO);
    MensajesResultados olvidePassword(UsuarioForgetPasswordDTO UsuarioForgetPasswordDTO);
}
