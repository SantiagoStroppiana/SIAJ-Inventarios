package siaj.inventarios.dao;



import siaj.inventarios.model.OrdenCompra;

import java.util.List;

public interface OrdenCompraDAO {
    List<OrdenCompra> obtenerTodasLasOrdenCompras();
    OrdenCompra agregarOrdenCompra(OrdenCompra ordenCompra);
}
