package siaj.inventarios.dao;

import siaj.inventarios.dto.DetalleEntradaDTO;
import siaj.inventarios.model.DetalleEntrada;

import java.util.List;

public interface DetalleEntradaDAO {
    void guardar(DetalleEntrada detalle);
    List<DetalleEntradaDTO> listarDetalleEntradas();
}
