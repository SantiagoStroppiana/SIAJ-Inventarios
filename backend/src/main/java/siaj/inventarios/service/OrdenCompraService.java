package siaj.inventarios.service;

import siaj.inventarios.model.OrdenCompra;

import java.util.List;

public interface OrdenCompraService {
    List<OrdenCompra> obtenerTodasLasOrdenCompras();
    OrdenCompra agregarOrdenCompra(OrdenCompra ordenCompra);
}
