package siaj.inventarios.dao;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import siaj.inventarios.model.Usuario;

import java.util.List;

public interface UsuarioDAO {
    Usuario buscarPorEmailyPassword(@Email String email, @NotBlank String password);
    void registrarUsuario(Usuario usuario);
    void actualizarUsuario(Usuario usuario);
    void eliminarUsuario(Usuario usuario);
    List<Usuario> listarUsuarios();
    Usuario buscarUsuarioPorNombre(@NotBlank String nombre);
}
