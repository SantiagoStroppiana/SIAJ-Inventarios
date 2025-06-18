package siaj.inventarios.dao;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Categoria;
import siaj.inventarios.model.GrupoCategoria;
import siaj.inventarios.model.Producto;

import java.util.List;
import java.util.Optional;

public interface CategoriaDAO {

    // Métodos originales
    MensajesResultados crearCategoria(Categoria categoria);
    List<Categoria> listarCategorias();
    MensajesResultados modificarCategoria(Categoria categoria);

    // Nuevos métodos para manejar grupos
    List<Categoria> listarCategoriasPorGrupo(int grupoId);
    List<Categoria> listarCategoriasPorGrupo(GrupoCategoria grupo);

    // Métodos de búsqueda
    Optional<Categoria> obtenerCategoriaPorId(int id);
    Optional<Categoria> obtenerCategoriaPorNombre(String nombre);
    List<Categoria> buscarCategoriasPorNombre(String nombre);

    // Métodos de eliminación
    MensajesResultados eliminarCategoria(int id);

    // Métodos para productos
    List<Producto> obtenerProductosPorCategoria(int categoriaId);
    long contarProductosPorCategoria(int categoriaId);

    // Métodos de validación
    boolean existeCategoriaPorNombre(String nombre);
    boolean existeCategoriaPorNombreYGrupo(String nombre, int grupoId);

    // Métodos de estadísticas
    long contarCategorias();
    long contarCategoriasPorGrupo(int grupoId);

    // Métodos avanzados
    List<Categoria> obtenerCategoriasConProductos();
}