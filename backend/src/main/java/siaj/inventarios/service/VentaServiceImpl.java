package siaj.inventarios.service;

import siaj.inventarios.dao.VentaDAO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.dto.VentaDTO;
import siaj.inventarios.model.Venta;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class VentaServiceImpl implements VentaService{

    private final VentaDAO ventaDAO;

    public VentaServiceImpl(VentaDAO ventaDAO) {
        this.ventaDAO = ventaDAO;
    }



    @Override
    public List<VentaDTO> obtenerTodasLasVentas() {
        List<Venta> ventas = ventaDAO.obtenerTodasLasVentas();

        return ventas.stream().map(v -> {
            VentaDTO dto = new VentaDTO();
            dto.setId(v.getId());
            dto.setTotal(v.getTotal());
            dto.setEstado(v.getEstado().name());
            dto.setFechaPago(v.getFechaPago().toString());
            dto.setUsuario(v.getUsuario());
            dto.setMedioPago(v.getMedioPago());
            return dto;
        }).collect(Collectors.toList()).reversed();
    }

    @Override
    public Venta obtenerVentaPorId(int id) {
        return null;
    }

    @Override
    public VentaDTO registrarVenta(Venta venta) {

       return ventaDAO.agregarVenta(venta);
    }
    @Override
    public MensajesResultados actualizarVenta (Venta venta){

        return ventaDAO.actualizarVenta(venta);


    }
    @Override
    public void actualizarEstado(int id, Venta.EstadoVenta nuevoEstado) {

    }
/*
    @Override
    public void eliminarVenta(int id) {

    }
*/
    @Override
    public List<Venta> obtenerVentasPorUsuario(int usuarioId) {
        return List.of();
    }

    @Override
    public BigDecimal calcularTotalVentasUsuario(int usuarioId) {
        return null;
    }
}
