package siaj.inventarios.service;

import siaj.inventarios.dto.DetalleVentaDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.DetalleVenta;
import siaj.inventarios.model.Producto;

import java.util.List;

public interface DetalleVentaService {
    MensajesResultados registrarDetalle(DetalleVenta detalleVenta);
    List<DetalleVenta> obtenerPorVenta(int ventaId);
    List <DetalleVentaDTO> obtenerDetalles();
    void descontarStock(Producto producto , int cantidad);

}