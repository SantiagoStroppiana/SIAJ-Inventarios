package siaj.inventarios.dao;

import siaj.inventarios.model.Producto;
import java.util.List;

public interface ProductoDAO {

    List<Producto> listarProductos();
    String crearProducto(Producto producto);

}
