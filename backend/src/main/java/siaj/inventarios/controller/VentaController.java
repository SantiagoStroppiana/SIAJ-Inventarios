package siaj.inventarios.controller;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Venta;
import siaj.inventarios.service.VentaService;

import java.util.List;

public class VentaController {

    private VentaService ventaService;

    public VentaController() {

    }

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }
    public MensajesResultados actualizarVenta (Venta venta){
        return ventaService.actualizarVenta(venta);
    }

    public MensajesResultados crearVenta (Venta venta){
        return ventaService.registrarVenta(venta);
    }

    public List<Venta> getVentas(){
        return ventaService.obtenerTodasLasVentas();
    }
}
