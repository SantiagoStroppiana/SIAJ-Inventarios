package siaj.inventarios.dao;

import org.hibernate.Session;
import siaj.inventarios.dto.DetalleOrdenCompraDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.DetalleOrdenCompra;
import siaj.inventarios.util.HibernateUtil;

import java.util.List;

public class DetalleOrdenCompraDAOImpl implements DetalleOrdenCompraDAO {

    @Override
    public DetalleOrdenCompraDTO agregarDetalle(DetalleOrdenCompra detalle) {
        Session session = HibernateUtil.getSession();

        try {
            session.beginTransaction();
            session.persist(detalle);
            session.getTransaction().commit();
            return new DetalleOrdenCompraDTO(detalle.getId(),detalle.getOrdenCompra().getId(),detalle.getProducto().getId(),detalle.getCantidad(),detalle.getPrecioUnitario().doubleValue());
        } catch (Exception e) {
            session.getTransaction().rollback();

        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public List<DetalleOrdenCompraDTO> obtenerDetalles() {
        return List.of();
    }

}
