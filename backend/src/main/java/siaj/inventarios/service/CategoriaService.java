package siaj.inventarios.service;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Categoria;
import siaj.inventarios.model.Producto;

import java.util.List;

public interface CategoriaService {

    List<Categoria> listarCategorias();

    MensajesResultados crearCategoria(Categoria categoria);

    MensajesResultados modificarCategoria(Categoria categoria);

}
