package siaj.inventarios.service;

import siaj.inventarios.dao.*;
import siaj.inventarios.dto.DetalleEntradaDTO;
import siaj.inventarios.model.DetalleEntrada;
import siaj.inventarios.model.Entrada;
import siaj.inventarios.model.OrdenCompra;
import siaj.inventarios.model.Producto;

import java.math.BigDecimal;
import java.util.List;

public class DetalleEntradaServiceImpl implements DetalleEntradaService {

    private final DetalleEntradaDAO detalleEntradaDAO = new DetalleEntradaDAOImpl();
    private final EntradaDAO entradaDAO = new EntradaDAOImpl();
    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private final OrdenCompraDAO ordenCompraDAO = new OrdenCompraDAOImpl();

    public DetalleEntradaServiceImpl(DetalleEntradaDAO detalleEntradaDAO) {
    }

    public DetalleEntrada registrarDetalle(DetalleEntradaDTO dto) {


        Entrada entrada = entradaDAO.buscarPorId(dto.getEntradaId());
        Producto producto = productoDAO.buscarPorId(dto.getProductoId());

        producto.setStock(producto.getStock() + dto.getCantidad());
        productoDAO.modificarProducto(producto);


        OrdenCompra ordenCompra = ordenCompraDAO.buscarPorId(entrada.getOrdenCompra().getId());
        ordenCompra.setEstado(OrdenCompra.EstadoOrden.completada);
        ordenCompraDAO.modificarOrdenCompra(ordenCompra);



        if (entrada == null || producto == null) {
            throw new RuntimeException("Entrada o Producto no encontrados");
        }
        System.out.println("Producto seteado: " + dto.getProductoId());
        System.out.println("Producto : " + producto);
        System.out.println("Entrada seteada: " + dto.getEntradaId());

        DetalleEntrada detalle = new DetalleEntrada();
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(BigDecimal.valueOf(dto.getPrecioUnitario()));
        detalle.setEntrada(entrada);
        detalle.setProducto(producto);

        detalleEntradaDAO.guardar(detalle);


        return detalle;
    }
    public List<DetalleEntradaDTO> listarDetalleEntradas(){
        return detalleEntradaDAO.listarDetalleEntradas();
    }
}
