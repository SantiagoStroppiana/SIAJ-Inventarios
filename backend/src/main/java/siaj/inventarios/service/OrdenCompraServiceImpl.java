package siaj.inventarios.service;

import siaj.inventarios.dao.MedioPagoDAOImpl;
import siaj.inventarios.dao.OrdenCompraDAO;
import siaj.inventarios.dao.OrdenCompraDAOImpl;
import siaj.inventarios.dao.ProveedorDAOImpl;
import siaj.inventarios.dto.OrdenCompraDTO;
import siaj.inventarios.model.MedioPago;
import siaj.inventarios.model.OrdenCompra;
import siaj.inventarios.model.Proveedor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class OrdenCompraServiceImpl implements OrdenCompraService{

    private final OrdenCompraDAO ordenCompraDAO= new OrdenCompraDAOImpl();
    public OrdenCompraServiceImpl(OrdenCompraDAO ordenCompraDAO) {}

    private final ProveedorService proveedorService = new ProveedorServiceImpl(new ProveedorDAOImpl());
    private final MedioPagoService medioPagoService = new MedioPagoServiceImpl(new MedioPagoDAOImpl());


    @Override
    public List<OrdenCompraDTO> obtenerTodasLasOrdenCompras() {
        return ordenCompraDAO.obtenerTodasLasOrdenCompras();
    }

    public OrdenCompraDTO agregarOrdenCompraDesdeDTO(OrdenCompraDTO dto) {
        OrdenCompra orden = new OrdenCompra();

        orden.setEstado(OrdenCompra.EstadoOrden.valueOf(dto.getEstado()));
        orden.setTotal(dto.getTotal());
        LocalDateTime fechaPago = Instant.ofEpochMilli(dto.getFechaPago())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        orden.setFechaPago(fechaPago);
        //orden.setFechaPago(LocalDateTime.parse(dto.getFechaPago()));

        // Acá se hacen las búsquedas
        Proveedor proveedor = proveedorService.buscarPorId(dto.getProveedorId());
        MedioPago medioPago = medioPagoService.buscarPorId(dto.getMedioPagoId());

        orden.setProveedor(proveedor);
        orden.setMedioPago(medioPago);

        OrdenCompra creada = ordenCompraDAO.agregarOrdenCompra(orden);

        return new OrdenCompraDTO(
                creada.getId(),
                proveedor.getId(),
                medioPago.getId(),
                creada.getTotal(),
                creada.getFechaPago()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(), // 👈 Convertimos el LocalDateTime a long
                creada.getEstado().toString()
        );
    }


    @Override
    public OrdenCompra buscarPorId(int id) {
        OrdenCompra ordenCompra = ordenCompraDAO.buscarPorId(id);
        if (ordenCompra == null) {
            throw new RuntimeException("Orden de pago no encontrada con ID: " + id);
        }
        return ordenCompra;
    }

}
