package siaj.inventarios.service;

import org.mindrot.jbcrypt.BCrypt;
import siaj.inventarios.dao.UsuarioDAO;
import siaj.inventarios.dao.UsuarioDAOImpl;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Rol;
import siaj.inventarios.model.Usuario;

import java.util.List;


public class UsuarioServiceImpl implements UsuarioService{

    private final UsuarioDAO usuarioDAO;

    public UsuarioServiceImpl(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }


    @Override
    public MensajesResultados login(String email, String password) {

        if(email == null || password == null || email.isEmpty() || password.isEmpty()){
            return  new MensajesResultados(false, "Email o password no pueden estar vacio");
        }

        Usuario usuario = usuarioDAO.buscarUsuarioPorEmail(email);

        if(usuario == null){
            return  new MensajesResultados(false, "Usuario no encontrado");
        }

        if (!BCrypt.checkpw(password, usuario.getPassword())) {
            return new MensajesResultados(false, "Contraseña incorrecta");
        }

        return new MensajesResultados(true, "Inicio de sesion correcta");

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
            usuario.setIdRol(rolPorDefecto);

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
    public MensajesResultados actualizarRolAdmin(int idUsuario) {

        try{

            usuarioDAO.actualizarRolAdmin(idUsuario);
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

}
