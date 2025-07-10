package siaj.inventarios.service;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.MedioPago;

import java.util.List;

public interface MedioPagoService {
    List<MedioPago> listarMedioPago();
    MensajesResultados crearMedioPago(MedioPago medioPago);
    MedioPago buscarPorId(int id);
}
