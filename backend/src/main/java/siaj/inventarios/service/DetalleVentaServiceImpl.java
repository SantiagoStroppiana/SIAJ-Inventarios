package siaj.inventarios.service;


import siaj.inventarios.dao.*;
import siaj.inventarios.dto.DetalleVentaDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.dto.VentaDTO;
import siaj.inventarios.model.DetalleVenta;
import siaj.inventarios.model.Producto;
import siaj.inventarios.model.Venta;

import java.math.BigDecimal;
import java.util.List;

public class DetalleVentaServiceImpl implements DetalleVentaService {

    private final DetalleVentaDAO detalleVentaDAO = new DetalleVentaDAOImpl();
    private VentaDAO ventaDAO = new VentaDAOImpl();
    private ProductoDAO productoDAO = new ProductoDAOImpl();


    public DetalleVentaServiceImpl(DetalleVentaDAO detalleVentaDAO) {
    }

    @Override
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



        return detalleVentaDAO.agregarDetalle(detalleVenta);
    }

    @Override
    public List <DetalleVentaDTO> obtenerDetalles (){

        return detalleVentaDAO.obtenerDetalles();

    }

    @Override
    public List<DetalleVenta> obtenerPorVenta(int ventaId) {
        return detalleVentaDAO.obtenerPorVentaId(ventaId);
    }
}