package siaj.inventarios.dao;

import org.hibernate.Session;

import siaj.inventarios.model.Usuario;
import siaj.inventarios.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO{

    public UsuarioDAOImpl() {
    }

    @Override
    public void registrarUsuario(Usuario usuario) {

        Session session = HibernateUtil.getSession();

        try{
            session.beginTransaction();
            session.persist(usuario);
            session.getTransaction().commit();

        }catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Erorr al registrar usuario" + e.getMessage());
        }finally {
            session.close();
        }

    }

    @Override
    public void actualizarUsuario(Usuario usuario) {

        Session session = HibernateUtil.getSession();

        try{
            session.beginTransaction();
            session.merge(usuario);
            session.getTransaction().commit();
        }catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Erorr al actualizar usuario" + e.getMessage());
        }finally {
            session.close();
        }
    }
    @Override
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios;

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            usuarios = session.createQuery("FROM Usuario u", Usuario.class).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            // Siempre conviene loguear o imprimir la traza para debug
            System.err.println("Error al listar usuarios: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar usuarios", e);
        }

        return usuarios;
    }


    @Override
    public Usuario buscarUsuarioPorEmail(String email) {

        Session session = HibernateUtil.getSession();
        Usuario usuario = null;

        try {
            session.beginTransaction();
            usuario = session.createQuery("FROM Usuario u WHERE u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .uniqueResult();
            session.getTransaction().commit();
        }catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Erorr al buscar usuario por email" + e.getMessage());
        } finally {
            session.close();
        }
        return usuario;
    }

}
