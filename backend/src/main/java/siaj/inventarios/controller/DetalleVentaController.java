package siaj.inventarios.controller;

import siaj.inventarios.dto.DetalleVentaDTO;
import siaj.inventarios.model.DetalleVenta;
import siaj.inventarios.service.DetalleVentaService;
import siaj.inventarios.service.DetalleVentaServiceImpl;

import java.util.List;

public class DetalleVentaController {

    private DetalleVentaService detalleVentaService;


    public DetalleVentaController(){

    }

    public DetalleVentaController(DetalleVentaService detalleVentaService) {
        this.detalleVentaService = detalleVentaService;
    }

    public void crear(DetalleVentaDTO detalleVenta) {
        detalleVentaService.registrarDetalle(detalleVenta);
    }

    public List<DetalleVenta> obtenerPorVenta(int ventaId) {
        return detalleVentaService.obtenerPorVenta(ventaId);
    }
}