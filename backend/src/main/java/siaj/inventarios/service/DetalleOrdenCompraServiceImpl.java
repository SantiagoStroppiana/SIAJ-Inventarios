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


    @Override
    public MensajesResultados registrarDetalle(DetalleOrdenCompraDTO detalleOrdenCompraDTO) {
        Session session = HibernateUtil.getSession();
        try {

            session.beginTransaction();

            if (detalleOrdenCompraDTO.getCantidad() <= 0){
                return new MensajesResultados(false, "La cantidad debe ser mayor que 0");
            }

            OrdenCompra ordenCompra = session.get(OrdenCompra.class, detalleOrdenCompraDTO.getOrdenCompraId());


            Producto producto = session.get(Producto.class, detalleOrdenCompraDTO.getProductoId());

            if (ordenCompra == null || producto == null) {
               return new MensajesResultados(false, "No se puede registrar la orden de compra");
            }

            DetalleOrdenCompra detalleOrdenCompra = new DetalleOrdenCompra();
            detalleOrdenCompra.setOrdenCompra(ordenCompra);
            detalleOrdenCompra.setProducto(producto);
            detalleOrdenCompra.setCantidad(detalleOrdenCompraDTO.getCantidad());
            detalleOrdenCompra.setPrecioUnitario(BigDecimal.valueOf(detalleOrdenCompraDTO.getPrecioUnitario()));

            session.persist(detalleOrdenCompra);

            session.getTransaction().commit();
            return new MensajesResultados(true, "Detalle de orden de compra guardado con éxito");

        } catch (Exception e) {
            session.getTransaction().rollback();
            return new MensajesResultados(false, e.getMessage());
        } finally {
            session.close();
        }
    }


    @Override
    public List<DetalleOrdenCompraDTO> obtenerDetalles() {
        return List.of();
    }

}
