package siaj.inventarios.dao;

import jakarta.validation.constraints.NotBlank;
import siaj.inventarios.dto.UsuarioPasswordDTO;
import siaj.inventarios.model.Usuario;

import java.util.List;

public interface UsuarioDAO {
    void registrarUsuario(Usuario usuario);
    void actualizarRol(int idUsuario, String nuevoRol);
    List<Usuario> listarUsuarios();
    Usuario buscarUsuarioPorEmail(@NotBlank String email);
    void cambiarPassword(int idUsuario, String oldPassword,String newPassword);
    void olvidePassword(String email, String newPassword, String repetirPassword);
}
