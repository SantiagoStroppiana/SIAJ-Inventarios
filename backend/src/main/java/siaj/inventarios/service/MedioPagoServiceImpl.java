package siaj.inventarios.service;

import siaj.inventarios.dao.DetalleVentaDAO;
import siaj.inventarios.dao.DetalleVentaDAOImpl;
import siaj.inventarios.dao.MedioPagoDAO;
import siaj.inventarios.dao.MedioPagoDAOImpl;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.MedioPago;

import java.util.List;

public class MedioPagoServiceImpl implements MedioPagoService {

    private final MedioPagoDAO medioPagoDAO= new MedioPagoDAOImpl();
    public MedioPagoServiceImpl(MedioPagoDAO medioPagoDAO) {}

    @Override
    public List<MedioPago> listarMedioPago() {
        return medioPagoDAO.listarMedioPago();
    }

    @Override
    public MensajesResultados crearMedioPago(MedioPago medioPago) {
        return medioPagoDAO.crearMedioPago(medioPago);
    }

    @Override
    public MedioPago buscarPorId(int id) {
        MedioPago medioPago = medioPagoDAO.buscarPorId(id);
        if (medioPago == null) {
            throw new RuntimeException("Medio de pago no encontrado con ID: " + id);
        }
        return medioPago;
    }

}
