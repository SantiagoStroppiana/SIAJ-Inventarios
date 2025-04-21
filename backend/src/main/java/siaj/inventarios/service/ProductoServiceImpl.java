package siaj.inventarios.service;

import siaj.inventarios.dao.ProductoDAO;
import siaj.inventarios.model.Producto;

import java.util.List;

public class ProductoServiceImpl {

    public List<Producto> listarPorductos() {
        return ProductoDAO.listarPorductos();
    }

}
