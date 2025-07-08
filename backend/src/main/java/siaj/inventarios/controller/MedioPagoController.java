package siaj.inventarios.controller;


import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.MedioPago;
import siaj.inventarios.service.MedioPagoService;

import java.util.List;

public class MedioPagoController {
    private MedioPagoService medioPagoService;

    public MedioPagoController() {}

    public MedioPagoController(MedioPagoService medioPagoService) {
        this.medioPagoService = medioPagoService;
    }
    public MensajesResultados crearMedioPago(MedioPago medioPago){
        return medioPagoService.crearMedioPago(medioPago);
    }

    public List<MedioPago> listarMedioPago() {
        return medioPagoService.listarMedioPago();
    }
}
