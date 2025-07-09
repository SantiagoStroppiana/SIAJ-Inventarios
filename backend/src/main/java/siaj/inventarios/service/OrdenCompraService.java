package siaj.inventarios.service;

import siaj.inventarios.dto.OrdenCompraDTO;
import siaj.inventarios.model.OrdenCompra;

import java.util.List;

public interface OrdenCompraService {
    List<OrdenCompra> obtenerTodasLasOrdenCompras();
    OrdenCompraDTO agregarOrdenCompraDesdeDTO(OrdenCompraDTO ordenCompraDTO);
    OrdenCompra buscarPorId(int id);
}
