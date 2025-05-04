package siaj.inventarios.service;

import siaj.inventarios.dao.ProductoDAO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Producto;

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
    public String crearProducto (Producto producto) {
     return productoDAO.crearProducto(producto);
    }

    @Override
    public MensajesResultados validaciones (String sku, String nombre, int stock, double precio /*categoria,estado y proveedor*/){


    if (sku.isEmpty()){
        return new MensajesResultados(false, "SKU no ingresado");
    }

    if (!nombre.matches("[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ]+")) {
        return new MensajesResultados(true, "El nombre solo puede contener letras, números y espacios." );

    }


    return null;
    }

    public MensajesResultados validaciones(String sku, String nombre, int stock, double precio, String categoria, String estado, Integer proveedorId) {

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

        if (categoria == null ||  categoria.trim().isEmpty()) {
            return new MensajesResultados(false, "Categoría no seleccionada");
        }

        if (estado == null || estado.trim().isEmpty()) {
            return new MensajesResultados(false, "Estado no seleccionado");
        }

        if (proveedorId == null || proveedorId <= 0) {
            return new MensajesResultados(false, "Proveedor inválido o no seleccionado");
        }

        return new MensajesResultados(true, "Validación exitosa");
    }




}