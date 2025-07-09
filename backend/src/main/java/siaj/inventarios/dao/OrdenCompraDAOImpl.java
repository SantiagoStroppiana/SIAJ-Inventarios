package siaj.inventarios.dao;

import org.hibernate.Session;
import siaj.inventarios.model.OrdenCompra;
import siaj.inventarios.model.Proveedor;
import siaj.inventarios.util.HibernateUtil;

import java.util.List;

public class OrdenCompraDAOImpl implements OrdenCompraDAO {
    @Override
    public List<OrdenCompra> obtenerTodasLasOrdenCompras() {
        List<OrdenCompra> ordenCompras;

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            ordenCompras = session.createQuery("FROM OrdenCompra o", OrdenCompra.class).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error al listar Venta: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar las ordenes de compras", e);
        }

        return ordenCompras;

    }

    @Override
    public OrdenCompra agregarOrdenCompra(OrdenCompra ordenCompra) {
        Session session = HibernateUtil.getSession();

        try {
            session.beginTransaction();
            session.persist(ordenCompra);
            session.getTransaction().commit();
            return ordenCompra;

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Error al agregar orden de compra: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public OrdenCompra buscarPorId(int id) {
        Session session = HibernateUtil.getSession();
        try {
            return session.get(OrdenCompra.class, id);
        } finally {
            session.close();
        }
    }
}
