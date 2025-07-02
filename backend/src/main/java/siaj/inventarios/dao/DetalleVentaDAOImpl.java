package siaj.inventarios.dao;

import org.hibernate.Session;
import siaj.inventarios.model.DetalleVenta;
import siaj.inventarios.util.HibernateUtil;
import org.hibernate.Transaction;

import java.util.List;



public class DetalleVentaDAOImpl implements DetalleVentaDAO {

    @Override
    public void agregarDetalle(DetalleVenta detalle) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSession()) {
            transaction = session.beginTransaction();
            session.persist(detalle);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            throw new RuntimeException("Error al guardar detalle de venta");
        }
    }

    @Override
    public void agregar(DetalleVenta detalleVenta) {

    }

    @Override
    public List<DetalleVenta> obtenerPorVentaId(int ventaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM DetalleVenta WHERE venta.id = :ventaId", DetalleVenta.class)
                    .setParameter("ventaId", ventaId)
                    .getResultList();
        }
    }

    public DetalleVentaDAOImpl() {
    }
}
