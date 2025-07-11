package siaj.inventarios.controller;

import siaj.inventarios.dao.MedioPagoDAOImpl;
import siaj.inventarios.dao.OrdenCompraDAOImpl;
import siaj.inventarios.dao.ProductoDAOImpl;
import siaj.inventarios.dao.ProveedorDAOImpl;
import siaj.inventarios.dto.DetalleOrdenCompraDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.DetalleOrdenCompra;
import siaj.inventarios.model.OrdenCompra;
import siaj.inventarios.model.Producto;
import siaj.inventarios.service.*;

import java.math.BigDecimal;
import java.util.List;

public class DetalleOrdenCompraController {

    private final OrdenCompraService ordenCompraService = new OrdenCompraServiceImpl(new OrdenCompraDAOImpl());
    private final ProductoService productoService = new ProductoServiceImpl(new ProductoDAOImpl());
    private DetalleOrdenCompraService detalleOrdenCompraService = new DetalleOrdenCompraServiceImpl();

    public DetalleOrdenCompraController(DetalleOrdenCompraService detalleOrdenCompraService) {
    }

    public MensajesResultados crear(DetalleOrdenCompraDTO detalleDTO) {
       /* Producto producto = productoService.buscarPorId(detalleDTO.getProductoId());
        OrdenCompra orden = ordenCompraService.buscarPorId(detalleDTO.getOrdenCompraId());

        DetalleOrdenCompra detalle = new DetalleOrdenCompra();
        detalle.setProducto(producto);
        detalle.setOrdenCompra(orden);
        detalle.setCantidad(detalleDTO.getCantidad());
        detalle.setPrecioUnitario(BigDecimal.valueOf(detalleDTO.getPrecioUnitario()));*/

        return detalleOrdenCompraService.registrarDetalle(detalleDTO);
    }

    public List<DetalleOrdenCompraDTO> obtenerDetalles() {
        return detalleOrdenCompraService.obtenerDetalles();
    }
}
