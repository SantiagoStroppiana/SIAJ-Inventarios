package siaj.inventarios.dao;

import siaj.inventarios.dto.DetalleVentaDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.DetalleVenta;

import java.util.List;

public interface DetalleVentaDAO  {


    void agregar(DetalleVenta detalleVenta);
    List<DetalleVenta> obtenerPorVentaId(int ventaId);
    MensajesResultados agregarDetalle(DetalleVenta dto);
    List <DetalleVentaDTO> obtenerDetalles();
}
