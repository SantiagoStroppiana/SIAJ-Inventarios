package siaj.inventarios.service;

import siaj.inventarios.dto.DetalleVentaDTO;
import siaj.inventarios.model.DetalleVenta;

import java.util.List;

public interface DetalleVentaService {
    void registrarDetalle(DetalleVentaDTO detalleVenta);
    List<DetalleVenta> obtenerPorVenta(int ventaId);
}