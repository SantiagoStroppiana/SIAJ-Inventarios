package siaj.inventarios.dao;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.MedioPago;

import java.util.List;

public interface MedioPagoDAO {
    MensajesResultados crearMedioPago(MedioPago medioPago);
    List<MedioPago> listarMedioPago();
}
