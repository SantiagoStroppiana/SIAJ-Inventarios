package siaj.inventarios.service;

import siaj.inventarios.model.GrupoCategoria;

import java.util.List;
import java.util.Optional;

public interface GrupoCategoriaService {
    List<GrupoCategoria> findAll();

    List<GrupoCategoria> findAllWithCategorias();

    Optional<GrupoCategoria> findById(Integer id);

    Optional<GrupoCategoria> findByIdWithCategorias(Integer id);

    List<GrupoCategoria> findByNombreContaining(String nombre);

    List<GrupoCategoria> findByDescripcionContaining(String descripcion);

    GrupoCategoria save(GrupoCategoria grupoCategoria);

    GrupoCategoria update(Integer id, GrupoCategoria grupoCategoria);

    void deleteById(Integer id);

    boolean existsByNombre(String nombre);

    boolean existsById(Integer id);

    long count();
}
