package siaj.inventarios.service;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Producto;

import java.util.List;

public interface ProductoService {

    List<Producto> listarProductos();

    MensajesResultados crearProducto(Producto producto);

    MensajesResultados validaciones (String sku, String nombre, int stock, double precio,double precioCosto,/* String categoria,*/ boolean estado, Integer proveedorId);

    MensajesResultados modificarProducto(Producto producto);

    List<Producto> filtrarProveedor(int id);

    List<Producto> filtrarCategoria(int idCategoria);

    Producto buscarPorId(int id);
}