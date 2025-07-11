package siaj.inventarios.service;

import siaj.inventarios.dto.DetalleEntradaDTO;
import siaj.inventarios.model.DetalleEntrada;

import java.util.List;

public interface DetalleEntradaService {
    DetalleEntrada registrarDetalle(DetalleEntradaDTO dto);
    List<DetalleEntradaDTO> listarDetalleEntradas();
}
