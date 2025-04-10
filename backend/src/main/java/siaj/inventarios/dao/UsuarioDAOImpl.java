package siaj.inventarios.dao;

import org.hibernate.Session;

//import org.mindrot.jbcrypt.BCrypt;
import siaj.inventarios.model.Usuario;
import siaj.inventarios.util.HibernateUtil;

import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO{

    @Override
    public Usuario buscarPorEmailyPassword(String email, String password) {

      Session session = HibernateUtil.getSession();
      Usuario usuario = null;

        try {
            session.beginTransaction();

            usuario = session.createQuery("FROM Usuario  u WHERE u.email = :email  AND u.password = :password", Usuario.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .uniqueResult();
        }finally {
            session.close();
        }
        return usuario;
    }

    @Override
    public void registrarUsuario(Usuario usuario) {

    }

    @Override
    public void actualizarUsuario(Usuario usuario) {

    }

    @Override
    public void eliminarUsuario(Usuario usuario) {

    }

    @Override
    public List<Usuario> listarUsuarios() {
        return List.of();
    }

    @Override
    public Usuario buscarUsuarioPorNombre(String nombre) {
        return null;
    }

}
