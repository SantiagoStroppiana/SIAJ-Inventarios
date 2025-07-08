package siaj.inventarios.dao;

import org.hibernate.Session;
import siaj.inventarios.dto.DetalleVentaDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.DetalleVenta;
import siaj.inventarios.model.Producto;
import siaj.inventarios.util.HibernateUtil;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;



public class DetalleVentaDAOImpl implements DetalleVentaDAO {

    @Override
    public MensajesResultados agregarDetalle(DetalleVenta detalle) {
        Session session = HibernateUtil.getSession();

        try {
            session.beginTransaction();
            session.persist(detalle);
            session.getTransaction().commit();
            return new MensajesResultados(true, "Detalle agregado exitosamente");
        } catch (Exception e) {
            session.getTransaction().rollback();
            //e.printStackTrace();
           return new MensajesResultados(false, "Error al agregar detalle"+ e.getMessage());
        }finally {
            session.close();
        }
    }

    @Override
    public List<DetalleVentaDTO> obtenerDetalles() {
        List<DetalleVenta> detalleVentas;

        List<DetalleVentaDTO> detalleVentasDTO = new ArrayList<DetalleVentaDTO>();

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            detalleVentas = session.createQuery("FROM DetalleVenta", DetalleVenta.class).getResultList();


            for (DetalleVenta detalle : detalleVentas) {
                DetalleVentaDTO detalleDTO = new DetalleVentaDTO();
                detalleDTO.setId(detalle.getId());
                detalleDTO.setCantidad(detalle.getCantidad());
                detalleDTO.setVentaId(detalle.getVenta().getId());
                detalleDTO.setProductoId(detalle.getProducto().getId());
                detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario().doubleValue());
                detalleVentasDTO.add(detalleDTO);


            }
            session.getTransaction().commit();
        } catch (Exception e) {

            System.err.println("Error al listar detalles: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar detalles", e);
        }


        return detalleVentasDTO;
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
