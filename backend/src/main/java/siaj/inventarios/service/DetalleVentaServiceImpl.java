package siaj.inventarios.service;


import siaj.inventarios.dao.*;
import siaj.inventarios.dto.DetalleVentaDTO;
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
    public void registrarDetalle(DetalleVentaDTO dto) {
        Venta venta = ventaDAO.obtenerPorId(dto.getVentaId());
        if (venta == null) {
            throw new RuntimeException("Venta no encontrada con ID: " + dto.getVentaId());
        }

        Producto producto = productoDAO.obtenerPorId(dto.getProductoId());
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado con ID: " + dto.getProductoId());
        }

        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(BigDecimal.valueOf(dto.getPrecioUnitario()));

        detalleVentaDAO.agregarDetalle(detalle);
    }




    @Override
    public List<DetalleVenta> obtenerPorVenta(int ventaId) {
        return detalleVentaDAO.obtenerPorVentaId(ventaId);
    }
}