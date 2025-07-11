package siaj.inventarios.dao;

import org.hibernate.Session;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.dto.OrdenCompraDTO;
import siaj.inventarios.model.OrdenCompra;
import siaj.inventarios.model.Producto;
import siaj.inventarios.util.HibernateUtil;

import java.time.ZoneId;
import java.util.List;

public class OrdenCompraDAOImpl implements OrdenCompraDAO {
    @Override
    public List<OrdenCompraDTO> obtenerTodasLasOrdenCompras() {
        List<OrdenCompraDTO> ordenCompraDTOs;

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            List<OrdenCompra> ordenCompras = session.createQuery("FROM OrdenCompra o", OrdenCompra.class).getResultList();

            ordenCompraDTOs = ordenCompras.stream()
                    .map(orden -> new OrdenCompraDTO(
                            orden.getId(),
                            orden.getProveedor().getId(),
                            orden.getMedioPago().getId(),
                            orden.getTotal(),
                            orden.getFechaPago().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                            orden.getEstado().toString()
                    ))
                    .toList();

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error al listar OrdenCompra: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar las ordenes de compras", e);
        }

        return ordenCompraDTOs;
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

    public void modificarOrdenCompra (OrdenCompra ordenCompra){

        Session session = HibernateUtil.getSession();

        try{
            session.beginTransaction();
            session.merge(ordenCompra);
            session.getTransaction().commit();


        }catch (Exception e) {

            session.getTransaction().rollback();

        }finally {
            session.close();
        }

    }
}
