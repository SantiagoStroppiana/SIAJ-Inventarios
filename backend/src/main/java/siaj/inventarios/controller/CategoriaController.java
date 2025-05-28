package siaj.inventarios.controller;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Categoria;
import siaj.inventarios.model.Producto;
import siaj.inventarios.service.CategoriaService;

import java.util.List;

public class CategoriaController {

    private CategoriaService categoriaService;



    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    public List<Categoria> mostrarCategorias () {return categoriaService.listarCategorias();}


    public MensajesResultados crearCategoria (Categoria categoria) {

        return categoriaService.crearCategoria(categoria);
    }

    public MensajesResultados modificarCategoria (Categoria categoria){
        return categoriaService.modificarCategoria(categoria);
    }

}
