package siaj.inventarios.dao;
import org.hibernate.Session;

import siaj.inventarios.model.Proveedor;
import siaj.inventarios.util.HibernateUtil;

import java.util.List;

public class ProveedorDAOImpl implements ProveedorDAO{
    public ProveedorDAOImpl() {
    }

    @Override
    public void registrarProveedor(Proveedor proveedor) {

        Session session = HibernateUtil.getSession();

        try {
            session.beginTransaction();
            session.persist(proveedor);
            session.getTransaction().commit();

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Erorr al registrar proveedor" + e.getMessage());
        } finally {
            session.close();
        }

    }

    @Override
    public void actualizarProveedor(Proveedor proveedor) {

        Session session = HibernateUtil.getSession();

        try {
            session.beginTransaction();
            session.merge(proveedor);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Error al actualizar proveedor" + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<Proveedor> listarproveedores() {
        List<Proveedor> proveedores;

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            proveedores = session.createQuery("FROM Proveedor u", Proveedor.class).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error al listar proveedores: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar proveedores", e);
        }

        return proveedores;
    }


    @Override
    public Proveedor buscarProveedorPorEmail(String email) {

        Session session = HibernateUtil.getSession();
        Proveedor proveedor = null;

        try {
            session.beginTransaction();
            proveedor = session.createQuery("FROM Proveedor u WHERE u.email = :email", Proveedor.class)
                    .setParameter("email", email)
                    .uniqueResult();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Erorr al buscar proveedor por email" + e.getMessage());
        } finally {
            session.close();
        }
        return proveedor;
    }


}
