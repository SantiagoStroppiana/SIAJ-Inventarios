package siaj.inventarios.service;

import org.hibernate.Session;
import siaj.inventarios.dao.*;
import siaj.inventarios.dto.DetalleOrdenCompraDTO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.*;
import siaj.inventarios.util.HibernateUtil;

import java.math.BigDecimal;
import java.util.List;

public class DetalleOrdenCompraServiceImpl implements  DetalleOrdenCompraService{

    private DetalleOrdenCompraDAO detalleOrdenCompraDAO = new DetalleOrdenCompraDAOImpl();
    private OrdenCompraDAO ordenCompraDAO = new OrdenCompraDAOImpl();
    private ProductoDAO productoDAO = new ProductoDAOImpl();


    public DetalleOrdenCompraServiceImpl(DetalleOrdenCompraDAO detalleOrdenCompraDAO) {
        this.detalleOrdenCompraDAO = detalleOrdenCompraDAO;
    }

    public DetalleOrdenCompraServiceImpl() {}

    public DetalleOrdenCompraServiceImpl(CategoriaDAO categoriaDAO) {}
    /*@Override
    public DetalleOrdenCompraDTO registrarDetalle(DetalleOrdenCompra detalle) {
    return detalleOrdenCompraDAO.agregarDetalle(detalle);
    }*/


    @Override
    public MensajesResultados registrarDetalle(DetalleOrdenCompraDTO detalleOrdenCompraDTO) {
        Session session = HibernateUtil.getSession();
        try {
            session.beginTransaction();

            OrdenCompra ordenCompra = session.get(OrdenCompra.class, detalleOrdenCompraDTO.getOrdenCompraId());
            Producto producto = session.get(Producto.class, detalleOrdenCompraDTO.getProductoId());

            if (ordenCompra == null || producto == null) {
                throw new RuntimeException("Orden de compra o producto no encontrado");
            }

            // Asignar instancias persistidas
            DetalleOrdenCompra detalleOrdenCompra = new DetalleOrdenCompra();
            detalleOrdenCompra.setOrdenCompra(ordenCompra);
            detalleOrdenCompra.setProducto(producto);
            detalleOrdenCompra.setCantidad(detalleOrdenCompraDTO.getCantidad());
            detalleOrdenCompra.setPrecioUnitario(BigDecimal.valueOf(detalleOrdenCompraDTO.getPrecioUnitario()));

            // Descontar stock
            /*
            int stockNuevo = producto.getStock() - detalleVenta.getCantidad();
            if (stockNuevo < 0) {
                throw new RuntimeException("Stock insuficiente");
            }
            producto.setStock(stockNuevo);
            session.merge(producto);*/

            // Guardar el detalle
            session.persist(detalleOrdenCompra);

            session.getTransaction().commit();
            return new MensajesResultados(true, "Detalle de orden de compra guardado con éxito");

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Error en guardar detalles de orden de compra: " + e.getMessage());
        } finally {
            session.close();
        }
    }


    @Override
    public List<DetalleOrdenCompraDTO> obtenerDetalles() {
        return List.of();
    }

}
