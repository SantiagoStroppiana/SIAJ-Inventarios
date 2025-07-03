package siaj.inventarios.controller;

import siaj.inventarios.dto.DetalleVentaDTO;
import siaj.inventarios.dto.MensajesResultados;
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

    public MensajesResultados crear(DetalleVenta detalleVenta) {
      return detalleVentaService.registrarDetalle(detalleVenta);
    }

    public List<DetalleVenta> obtenerPorVenta(int ventaId) {
        return detalleVentaService.obtenerPorVenta(ventaId);
    }

    public List <DetalleVentaDTO> obtenerDetalles() {
        return detalleVentaService.obtenerDetalles();
    }
}