package siaj.inventarios.dao;



import siaj.inventarios.dto.OrdenCompraDTO;
import siaj.inventarios.model.OrdenCompra;

import java.util.List;

public interface OrdenCompraDAO {
    List<OrdenCompraDTO> obtenerTodasLasOrdenCompras();
    OrdenCompra agregarOrdenCompra(OrdenCompra ordenCompra);
    OrdenCompra buscarPorId(int id);
    void modificarOrdenCompra(OrdenCompra ordenCompra);
}
