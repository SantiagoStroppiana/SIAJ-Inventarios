package siaj.inventarios.service;


import org.hibernate.Session;
import siaj.inventarios.dao.*;
import siaj.inventarios.dto.DetalleVentaDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.dto.VentaDTO;
import siaj.inventarios.model.DetalleVenta;
import siaj.inventarios.model.Producto;
import siaj.inventarios.model.Venta;
import siaj.inventarios.util.HibernateUtil;

import java.math.BigDecimal;
import java.util.List;

public class DetalleVentaServiceImpl implements DetalleVentaService {

    private final DetalleVentaDAO detalleVentaDAO = new DetalleVentaDAOImpl();
    private VentaDAO ventaDAO = new VentaDAOImpl();
    private ProductoDAO productoDAO = new ProductoDAOImpl();


    public DetalleVentaServiceImpl(DetalleVentaDAO detalleVentaDAO) {
    }

    /*@Override
    public MensajesResultados registrarDetalle(DetalleVenta detalleVenta) {
        Venta venta = new Venta();
        venta.setId(detalleVenta.getId());
       // Venta venta = ventaDAO.obtenerPorId(detalleVenta.getVenta().getId());
        if (venta == null) {
            throw new RuntimeException("Venta no encontrada con ID: " + detalleVenta.getVenta().getId());
        }
        Producto producto = new Producto();
        producto.setId(detalleVenta.getProducto().getId());

        //Producto producto = productoDAO.obtenerPorId(detalleVenta.getProducto().getId());
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado con ID: " + detalleVenta.getProducto().getId());
        }

        descontarStock(producto, detalleVenta.getCantidad());

        return detalleVentaDAO.agregarDetalle(detalleVenta);
    }*/


    @Override
    public MensajesResultados registrarDetalle(DetalleVenta detalleVenta) {
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();

            Venta venta = session.get(Venta.class, detalleVenta.getVenta().getId());
            Producto producto = session.get(Producto.class, detalleVenta.getProducto().getId());

            if (venta == null || producto == null) {
                return new MensajesResultados(false, "No se puede registrar la venta porque no hay producto seleccionado");
            }

            detalleVenta.setVenta(venta);
            detalleVenta.setProducto(producto);

            int stockNuevo = producto.getStock() - detalleVenta.getCantidad();
            if (stockNuevo <= 0) {
                return new MensajesResultados(false, "El producto no tiene el suficiente stock");
            }
            producto.setStock(stockNuevo);
            session.merge(producto);

            session.persist(detalleVenta);

            session.getTransaction().commit();
            return new MensajesResultados(true, "Detalle de venta guardado con éxito");

        } catch (Exception e) {
            session.getTransaction().rollback();
            return new MensajesResultados(false, e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List <DetalleVentaDTO> obtenerDetalles (){

        return detalleVentaDAO.obtenerDetalles();

    }

    @Override
    public List<DetalleVenta> obtenerPorVenta(int ventaId) {
        return detalleVentaDAO.obtenerPorVentaId(ventaId);
    }


    @Override
    public void descontarStock (Producto producto, int cantidad){
        producto.setStock(producto.getStock() - cantidad);
        productoDAO.modificarProducto(producto);
    }

}

