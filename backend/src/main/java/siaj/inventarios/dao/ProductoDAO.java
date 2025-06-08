package siaj.inventarios.dao;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Producto;
import java.util.List;

public interface ProductoDAO {

    List<Producto> listarProductos();
    MensajesResultados crearProducto(Producto producto);


    boolean buscarSku (String sku);
    MensajesResultados modificarProducto (Producto producto);
    List<Producto> filtrarProveedor(int id);
    List<Producto> filtrarCategoria(int idCategoria);
    Producto buscarPorSku(String sku);

}
