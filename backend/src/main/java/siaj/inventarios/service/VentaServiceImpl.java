package siaj.inventarios.service;

import siaj.inventarios.dao.ProductoDAO;
import siaj.inventarios.dao.VentaDAO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Venta;

import java.math.BigDecimal;
import java.util.List;

public class VentaServiceImpl implements VentaService{

    private final VentaDAO ventaDAO;

    public VentaServiceImpl(VentaDAO ventaDAO) {
        this.ventaDAO = ventaDAO;
    }



    @Override
    public List<Venta> obtenerTodasLasVentas() {
        return ventaDAO.obtenerTodasLasVentas();
    }

    @Override
    public Venta obtenerVentaPorId(int id) {
        return null;
    }

    @Override
    public MensajesResultados registrarVenta(Venta venta) {

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
