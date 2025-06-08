package siaj.inventarios.controller;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Producto;
import siaj.inventarios.service.ProductoService;

import java.util.List;

public class ProductoController {

    private ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // public Producto mostrarProductos (Producto producto) {return productoService.listarProductos().get(producto.getId());}
    public List<Producto> mostrarProductos () {return productoService.listarProductos();}


    //sku, nombre, categoria, stock, activo, precio y proveedor VALIDACIONES?

    public MensajesResultados crearProducto (Producto producto) {

        return productoService.crearProducto(producto);
    }

    public MensajesResultados modificarProducto (Producto producto){

        return productoService.modificarProducto(producto);
    }

}