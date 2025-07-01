package siaj.inventarios.dao;

import java.util.List;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Venta;

public interface VentaDAO {


    List<Venta> obtenerTodasLasVentas();
    MensajesResultados agregarVenta(Venta venta);
    MensajesResultados actualizarVenta(Venta venta);
 //   void eliminarVenta(Venta venta);

}
