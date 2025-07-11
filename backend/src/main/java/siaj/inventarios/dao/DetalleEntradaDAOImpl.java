package siaj.inventarios.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import siaj.inventarios.dto.DetalleEntradaDTO;
import siaj.inventarios.dto.DetalleVentaDTO;
import siaj.inventarios.model.DetalleEntrada;
import siaj.inventarios.model.DetalleVenta;
import siaj.inventarios.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

public class DetalleEntradaDAOImpl implements DetalleEntradaDAO {
    public void guardar(DetalleEntrada detalle) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(detalle);
            tx.commit();
        }
    }

    public List<DetalleEntradaDTO> listarDetalleEntradas(){

        List<DetalleEntrada> detalleEntradas;

        List<DetalleEntradaDTO> detalleEntradaDTOS = new ArrayList<DetalleEntradaDTO>();

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            detalleEntradas = session.createQuery("FROM DetalleEntrada", DetalleEntrada.class).getResultList();


            for (DetalleEntrada detalle : detalleEntradas) {
                DetalleEntradaDTO detalleDTO = new DetalleEntradaDTO();
                detalleDTO.setEntradaId(detalle.getEntrada().getId());
                detalleDTO.setCantidad(detalle.getCantidad());
                detalleDTO.setProductoId(detalle.getProducto().getId());
                detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario().doubleValue());
                detalleEntradaDTOS.add(detalleDTO);


            }
            session.getTransaction().commit();
        } catch (Exception e) {

            System.err.println("Error al listar detalles: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar detalles", e);
        }


        return detalleEntradaDTOS;



    }
}
