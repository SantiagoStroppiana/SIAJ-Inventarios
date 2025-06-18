package siaj.inventarios.dao;

import siaj.inventarios.model.GrupoCategoria;

import java.util.List;
import java.util.Optional;

public interface GrupoCategoriaDAO {
    List<GrupoCategoria> findAll();

    List<GrupoCategoria> findAllWithCategorias();

    Optional<GrupoCategoria> findById(Integer id);

    Optional<GrupoCategoria> findByIdWithCategorias(Integer id);

    Optional<GrupoCategoria> findByNombre(String nombre);

    List<GrupoCategoria> findByNombreContaining(String nombre);

    List<GrupoCategoria> findByDescripcionContaining(String descripcion);

    GrupoCategoria save(GrupoCategoria grupoCategoria);

    GrupoCategoria update(GrupoCategoria grupo);

    void deleteById(Integer id);

    boolean existsByNombre(String nombre);

    boolean existsById(Integer id);

    long count();

    boolean existsByNombreAndIdNot(String nombre, int id);
}
