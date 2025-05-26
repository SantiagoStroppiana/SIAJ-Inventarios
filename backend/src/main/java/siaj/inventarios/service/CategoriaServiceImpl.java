package siaj.inventarios.service;

import siaj.inventarios.dao.CategoriaDAO;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Categoria;
import siaj.inventarios.model.Producto;

import java.math.BigDecimal;
import java.util.List;

public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaDAO categoriaDAO;

    public CategoriaServiceImpl(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    @Override
    public List<Categoria> listarCategorias() {
        return categoriaDAO.listarCategorias();
    }
    @Override
    public MensajesResultados crearCategoria(Categoria categoria) {
        return categoriaDAO.crearCategoria(categoria);
       /* MensajesResultados mr = validaciones(producto.getSku(), producto.getNombre(), producto.getStock(), precio.doubleValue()/*,categoria*//*, producto.isActivo(),producto.getProveedorid().getId());
        if (mr.isExito()){
            filtrarCategoria(1);//NO IRIA ACA
            return productoDAO.crearProducto(producto);


        }else {
            return mr.getMensaje();
        }*/

    }



    @Override
    public MensajesResultados modificarCategoria(Categoria categoria){
    return categoriaDAO.modificarCategoria(categoria);
        /*MensajesResultados mr = validacionesModificar(producto.getSku(), producto.getNombre(), producto.getStock(), precio.doubleValue()/*,categoria*//*, producto.isActivo(),producto.getProveedorid().getId());
        if (mr.isExito()){
            return productoDAO.modificarProducto(producto);

        }else {
            return mr.getMensaje();
        }*/
    }



}
