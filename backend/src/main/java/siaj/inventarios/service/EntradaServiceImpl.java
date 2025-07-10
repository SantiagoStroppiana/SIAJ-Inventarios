package siaj.inventarios.service;

import siaj.inventarios.dao.EntradaDAO;
import siaj.inventarios.dao.EntradaDAOImpl;
import siaj.inventarios.dao.OrdenCompraDAO;
import siaj.inventarios.dao.OrdenCompraDAOImpl;
import siaj.inventarios.dto.OrdenCompraDTO;
import siaj.inventarios.model.Entrada;
import siaj.inventarios.model.MedioPago;
import siaj.inventarios.model.OrdenCompra;
import siaj.inventarios.model.Proveedor;

import java.time.LocalDateTime;
import java.util.List;

public class EntradaServiceImpl implements EntradaService{

    private final EntradaDAO entradaDAO= new EntradaDAOImpl();
    public EntradaServiceImpl(EntradaDAO entradaDAO) {}

    @Override
    public List<Entrada> obtenerTodasLasEntradas() {
        return entradaDAO.obtenerTodasLasEntradas();
    }

    @Override
    public Entrada agregarEntrada(Entrada entrada) {
    return entradaDAO.agregarEntrada(entrada);
    }


    @Override
    public Entrada buscarPorId(int id) {
        Entrada entrada = entradaDAO.buscarPorId(id);
        if (entrada == null) {
            throw new RuntimeException("Entrada no encontrada con ID: " + id);
        }
        return entrada;
    }
}
