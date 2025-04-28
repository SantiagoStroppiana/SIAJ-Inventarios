package siaj.inventarios.controller;

import siaj.inventarios.model.Producto;
import siaj.inventarios.service.ProductoService;

public class ProductoController {

    private ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    public Producto mostrarProductos (Producto producto) {return productoService.listarProductos().get(producto.getId());}

}
