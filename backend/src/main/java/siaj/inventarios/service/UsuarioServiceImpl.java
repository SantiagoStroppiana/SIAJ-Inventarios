package siaj.inventarios.service;

import org.mindrot.jbcrypt.BCrypt;
import siaj.inventarios.dao.UsuarioDAO;
import siaj.inventarios.dao.UsuarioDAOImpl;
import siaj.inventarios.dto.*;
import siaj.inventarios.model.Rol;
import siaj.inventarios.model.Usuario;

import java.util.List;


public class UsuarioServiceImpl implements UsuarioService{

    private final UsuarioDAO usuarioDAO;

    public UsuarioServiceImpl(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public LoginResponseDTO login(String email, String password) {

        if (email == null || password == null) {
            return new LoginResponseDTO(false, "Email o password no pueden estar vacios", null);
        }

        Usuario usuario = usuarioDAO.buscarUsuarioPorEmail(email);

        if (usuario == null) {
            return new LoginResponseDTO(false, "Usuario no encontrado", null);
        }

        if (!BCrypt.checkpw(password, usuario.getPassword())) {
            return new LoginResponseDTO(false, "Password incorrecta", null);
        }

        UsuarioDTO usuarioDTO = new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getNombreRol()
        );

        return new LoginResponseDTO(true, "Usuario encontrado", usuarioDTO);

    }

    @Override
    public MensajesResultados registrarUsuario(Usuario usuario) {

        if (usuario.getEmail() == null || !usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return new MensajesResultados(false, "El email no es valido");
        }

        if(usuario.getPassword() == null || usuario.getPassword().length() < 6) {
            return new MensajesResultados(false, "Password debe ser mayor que 6 caracteres");
        }


        Usuario existe = usuarioDAO.buscarUsuarioPorEmail(usuario.getEmail());
        if (existe != null) {
            return new MensajesResultados(false, "Usuario ya existe");
        }

        try {

            Rol rolPorDefecto = new Rol();
            rolPorDefecto.setId(2);
            usuario.setRolId(rolPorDefecto);

            String paswordEncriptada = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
            usuario.setPassword(paswordEncriptada);

            usuarioDAO.registrarUsuario(usuario);
            return new MensajesResultados(true, "Usuario registrado correctamente");

        }catch(Exception e){
            System.out.println("Error al registrar Usuario" + e.getMessage());
            return new MensajesResultados(false, "Error al registrar Usuario" + e.getMessage());
        }

    }

    @Override
    public MensajesResultados actualizarRol(int idUsuario, String nuevoRol) {

        try{
            usuarioDAO.actualizarRol(idUsuario, nuevoRol);
            return new MensajesResultados(true, "Usuario actualizado correctamente");
        }catch (Exception e){
            System.out.println("Error al actualizar rol admin" + e.getMessage());
            return new MensajesResultados(false, "Error al actualizar rol admin" + e.getMessage());
        }
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listarUsuarios();
    }

    @Override
    public MensajesResultados cambiarPassword(UsuarioPasswordDTO usuarioPasswordDTO) {
        if (usuarioPasswordDTO.getOldPassword() == null || usuarioPasswordDTO.getOldPassword().isBlank()
                || usuarioPasswordDTO.getNewPassword() == null || usuarioPasswordDTO.getNewPassword().isBlank()) {
            return new MensajesResultados(false, "Los campos de contraseña no pueden estar vacíos.");
        }

        try {
            usuarioDAO.cambiarPassword(usuarioPasswordDTO.getId(), usuarioPasswordDTO.getOldPassword(), usuarioPasswordDTO.getNewPassword());
            return new MensajesResultados(true, "Contraseña actualizada correctamente.");
        } catch (Exception e) {
            return new MensajesResultados(false, e.getMessage());
        }
    }

    @Override
    public MensajesResultados olvidePassword(UsuarioForgetPasswordDTO usuarioForgetPasswordDTO){

        if (usuarioForgetPasswordDTO.getNewPassword() == null || usuarioForgetPasswordDTO.getNewPassword().isBlank() ||
                usuarioForgetPasswordDTO.getConfirmPassword() == null || usuarioForgetPasswordDTO.getConfirmPassword().isBlank()) {
            return new MensajesResultados(false, "Los campos no pueden estar vacíos");
        }

        if (usuarioForgetPasswordDTO.getNewPassword().length() < 6 || usuarioForgetPasswordDTO.getConfirmPassword().length() < 6) {
            return new MensajesResultados(false, "La contraseña debe tener al menos 6 caracteres");
        }

        try{
            usuarioDAO.olvidePassword(usuarioForgetPasswordDTO.getEmail(), usuarioForgetPasswordDTO.getNewPassword(), usuarioForgetPasswordDTO.getConfirmPassword());
            return new MensajesResultados(true, "Password actualizada correctamente");
        }catch (Exception e){
            return new MensajesResultados(false, e.getMessage());
        }

    }

}
