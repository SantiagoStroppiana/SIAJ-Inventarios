package siaj.inventarios.dao;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Producto;
import java.util.List;

public interface ProductoDAO {

    List<Producto> listarProductos();
    String crearProducto(Producto producto);
    MensajesResultados validaciones (String sku, String nombre, int stock, double precio);
    boolean buscarSku (String sku);

}
