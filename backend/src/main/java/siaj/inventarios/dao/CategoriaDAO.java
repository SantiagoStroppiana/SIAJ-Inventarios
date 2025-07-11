package siaj.inventarios.dao;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Categoria;

import java.util.List;

public interface CategoriaDAO {

    List<Categoria> listarCategorias();
    MensajesResultados crearCategoria(Categoria categoria);
    MensajesResultados modificarCategoria (Categoria categoria);
    //boolean buscarCategoria (String categoria);


}
