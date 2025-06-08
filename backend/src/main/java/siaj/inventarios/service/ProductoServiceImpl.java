package siaj.inventarios.service;

import siaj.inventarios.dao.ProductoDAO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Producto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoServiceImpl implements ProductoService {
    private final ProductoDAO productoDAO;

    public ProductoServiceImpl(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }
    @Override
    public List<Producto> listarProductos() {
        return productoDAO.listarProductos();
    }

    @Override
    public List<Producto> filtrarCategoria(int idCategoria) {
        List<Producto> filtrarCategoria = productoDAO.filtrarCategoria(idCategoria);
        System.out.println("LISTA DE PRODUCTOS CON CATEGORIA 1\n");
        for(int i=0; i<filtrarCategoria.size(); i++){
            System.out.println("Producto "+(i+1)+filtrarCategoria.get(i));
        }
        return filtrarCategoria;
    }


    @Override
    public List<Producto> filtrarProveedor(int id) {
        List<Producto> productos = productoDAO.filtrarProveedor(id);
        System.out.println("LISTA DE PRODUCTOS CON PROVEEDOR 1\n");
        for(int i=1; i<productos.size(); i++){
            System.out.println("Producto "+(i+1)+productos.get(i));
        }
        return productos;
    }

    @Override
    public MensajesResultados crearProducto (Producto producto) {
        BigDecimal precio = producto.getPrecio();
        MensajesResultados mr = validaciones(producto.getSku(), producto.getNombre(), producto.getStock(), precio.doubleValue()/*,categoria*/, producto.isActivo(),producto.getProveedorid().getId());
       if (mr.isExito()){
           filtrarCategoria(1);//NO IRIA ACA
           return productoDAO.crearProducto(producto);

        }else {

       }
        return new MensajesResultados(false, "Error al querer crear el producto");
    }
    @Override
    public MensajesResultados modificarProducto (Producto producto){
        BigDecimal precio = producto.getPrecio();
        MensajesResultados mr = validacionesModificar(producto.getId(), producto.getSku(), producto.getNombre(), producto.getStock(), precio.doubleValue()/*,categoria*/, producto.isActivo(),producto.getProveedorid().getId());
        if (mr.isExito()){
            return productoDAO.modificarProducto(producto);

        }else {

            return mr;
        }
    }

    public MensajesResultados validaciones(String sku, String nombre, int stock, double precio,/* String categoria,*/ boolean estado, Integer proveedorId) {

        if (productoDAO.buscarSku(sku)) {
            return new MensajesResultados(false, "Ya existe un producto con ese SKU");
        }

        if (sku == null || sku.trim().isEmpty()) {
            return new MensajesResultados(false, "SKU no ingresado");
        }

        if (sku.length() > 20) {
            return new MensajesResultados(false, "El SKU no puede tener más de 20 caracteres");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            return new MensajesResultados(false, "Nombre no ingresado");
        }

        if (!nombre.matches("[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ]+")) {
            return new MensajesResultados(false, "El nombre solo puede contener letras, números y espacios");
        }

        if (stock < 0) {
            return new MensajesResultados(false, "El stock no puede ser negativo");
        }

        if (precio < 0) {
            return new MensajesResultados(false, "El precio no puede ser negativo");
        }
/*
        if (categoria == null ||  categoria.trim().isEmpty()) {
            return new MensajesResultados(false, "Categoría no seleccionada");
        }
*/


        if (proveedorId == null || proveedorId <= 0) {
            return new MensajesResultados(false, "Proveedor inválido o no seleccionado");
        }

        return new MensajesResultados(true, "Validación exitosa");
    }

    public MensajesResultados validacionesModificar(int id, String sku, String nombre, int stock, double precio,/* String categoria,*/ boolean estado, Integer proveedorId) {
        Producto productoConSku = productoDAO.buscarPorSku(sku);
        if (productoConSku != null && productoConSku.getId() != id) {
            return new MensajesResultados(false, "Ya existe un producto con ese SKU");
        }
        if (sku == null || sku.trim().isEmpty()) {
            return new MensajesResultados(false, "SKU no ingresado");
        }

        if (sku.length() > 20) {
            return new MensajesResultados(false, "El SKU no puede tener más de 20 caracteres");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            return new MensajesResultados(false, "Nombre no ingresado");
        }

        if (!nombre.matches("[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ]+")) {
            return new MensajesResultados(false, "El nombre solo puede contener letras, números y espacios");
        }

        if (stock < 0) {
            return new MensajesResultados(false, "El stock no puede ser negativo");
        }

        if (precio < 0) {
            return new MensajesResultados(false, "El precio no puede ser negativo");
        }
/*
        if (categoria == null ||  categoria.trim().isEmpty()) {
            return new MensajesResultados(false, "Categoría no seleccionada");
        }
*/


        if (proveedorId == null || proveedorId <= 0) {
            return new MensajesResultados(false, "Proveedor inválido o no seleccionado");
        }

        return new MensajesResultados(true, "Validación exitosa");
    }


}