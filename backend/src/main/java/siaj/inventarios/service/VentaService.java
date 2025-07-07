package siaj.inventarios.service;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.dto.VentaDTO;
import siaj.inventarios.model.Venta;

import java.math.BigDecimal;
import java.util.List;

public interface VentaService{




    List<VentaDTO> obtenerTodasLasVentas();
    Venta obtenerVentaPorId(int id);
    VentaDTO registrarVenta(Venta venta);
    void actualizarEstado(int id, Venta.EstadoVenta nuevoEstado);
   // void eliminarVenta(int id);
    MensajesResultados actualizarVenta(Venta venta);


    // Reglas adicionales
    List<Venta> obtenerVentasPorUsuario(int usuarioId);
    BigDecimal calcularTotalVentasUsuario(int usuarioId);



}
