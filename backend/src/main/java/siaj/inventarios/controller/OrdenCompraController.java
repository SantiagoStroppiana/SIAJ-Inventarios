package siaj.inventarios.controller;

import siaj.inventarios.dto.OrdenCompraDTO;
import siaj.inventarios.model.OrdenCompra;
import siaj.inventarios.service.OrdenCompraService;

import java.util.List;

public class OrdenCompraController {
    private OrdenCompraService ordenCompraService;

    public OrdenCompraController(){}
    public OrdenCompraController(OrdenCompraService ordenCompraService){
        this.ordenCompraService = ordenCompraService;
    }
    public List<OrdenCompraDTO> listarOrdenCompra() {
        return ordenCompraService.obtenerTodasLasOrdenCompras();
    }

    public OrdenCompraDTO agregarOrdenCompra(OrdenCompraDTO ordenCompraDTO) {
        return ordenCompraService.agregarOrdenCompraDesdeDTO(ordenCompraDTO);
    }
}
