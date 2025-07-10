package siaj.inventarios.dao;

import siaj.inventarios.dto.DetalleOrdenCompraDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.DetalleOrdenCompra;

public interface DetalleOrdenCompraDAO {

    DetalleOrdenCompraDTO agregarDetalle(DetalleOrdenCompra detalle);
}
