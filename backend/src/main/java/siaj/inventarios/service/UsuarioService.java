package siaj.inventarios.service;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Usuario;

public interface UsuarioService {

    MensajesResultados login(String email, String password);
    MensajesResultados registrarUsuario(Usuario usuario);
    MensajesResultados actualizarUsuario(Usuario usuario);

}
