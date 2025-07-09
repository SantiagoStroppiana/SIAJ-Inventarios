package siaj.inventarios.service;

import siaj.inventarios.dto.DetalleOrdenCompraDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.DetalleOrdenCompra;

import java.util.List;

public interface DetalleOrdenCompraService {

    MensajesResultados registrarDetalle(DetalleOrdenCompraDTO detalle);

    List<DetalleOrdenCompraDTO> obtenerDetalles();
}
