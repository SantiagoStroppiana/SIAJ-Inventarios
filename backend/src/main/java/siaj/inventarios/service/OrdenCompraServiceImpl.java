package siaj.inventarios.service;

import siaj.inventarios.dao.OrdenCompraDAO;
import siaj.inventarios.dao.OrdenCompraDAOImpl;
import siaj.inventarios.model.OrdenCompra;

import java.util.List;

public class OrdenCompraServiceImpl implements OrdenCompraService{

    private final OrdenCompraDAO ordenCompraDAO= new OrdenCompraDAOImpl();
    public OrdenCompraServiceImpl(OrdenCompraDAO ordenCompraDAO) {}

    @Override
    public List<OrdenCompra> obtenerTodasLasOrdenCompras() {
        return ordenCompraDAO.obtenerTodasLasOrdenCompras();
    }

    @Override
    public OrdenCompra agregarOrdenCompra(OrdenCompra ordenCompra) {
        return ordenCompraDAO.agregarOrdenCompra(ordenCompra);
    }
}
