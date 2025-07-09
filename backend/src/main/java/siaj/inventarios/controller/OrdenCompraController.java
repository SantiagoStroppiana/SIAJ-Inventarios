package siaj.inventarios.controller;

import siaj.inventarios.model.OrdenCompra;
import siaj.inventarios.service.OrdenCompraService;

import java.util.List;

public class OrdenCompraController {
    private OrdenCompraService ordenCompraService;

    public OrdenCompraController(){}
    public OrdenCompraController(OrdenCompraService ordenCompraService){
        this.ordenCompraService = ordenCompraService;
    }
    public List<OrdenCompra> listarOrdenCompra() {
        return ordenCompraService.obtenerTodasLasOrdenCompras();
    }

    public OrdenCompra agregarOrdenCompra(OrdenCompra ordenCompra) {
        return ordenCompraService.agregarOrdenCompra(ordenCompra);
    }
}
