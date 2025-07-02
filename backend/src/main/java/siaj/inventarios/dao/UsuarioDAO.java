package siaj.inventarios.dao;

import jakarta.validation.constraints.NotBlank;
import siaj.inventarios.model.Usuario;

import java.util.List;

public interface UsuarioDAO {
    void registrarUsuario(Usuario usuario);
    void actualizarRolAdmin(int idUsuario);
    List<Usuario> listarUsuarios();
    Usuario buscarUsuarioPorEmail(@NotBlank String email);
    void cambiarPassword(String oldPassword,String newPassword);
}
