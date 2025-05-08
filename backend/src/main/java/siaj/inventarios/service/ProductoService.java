package siaj.inventarios.service;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Producto;

import java.util.List;

public interface ProductoService {

    List<Producto> listarProductos();

    String crearProducto(Producto producto);

    MensajesResultados validaciones (String sku, String nombre, int stock, double precio,/* String categoria,*/ boolean estado, Integer proveedorId);

    String modificarProducto(Producto producto);

    List<Producto> filtrarProveedor(int id);
}