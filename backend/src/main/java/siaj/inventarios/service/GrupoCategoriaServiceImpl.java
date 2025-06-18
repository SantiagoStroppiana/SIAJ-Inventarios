package siaj.inventarios.service;

import siaj.inventarios.dao.GrupoCategoriaDAO;
import siaj.inventarios.model.GrupoCategoria;

import java.util.List;
import java.util.Optional;

public class GrupoCategoriaServiceImpl implements GrupoCategoriaService {

    private final GrupoCategoriaDAO grupoCategoriaDAO;

    public GrupoCategoriaServiceImpl(GrupoCategoriaDAO grupoCategoriaDAO) {
        this.grupoCategoriaDAO = grupoCategoriaDAO;
    }

    // Obtener todos los grupos
    public List<GrupoCategoria> findAll() {
        return grupoCategoriaDAO.findAll();
    }

    // Obtener todos los grupos con sus categorías
    public List<GrupoCategoria> findAllWithCategorias() {
        return grupoCategoriaDAO.findAllWithCategorias();
    }

    // Obtener por ID
    public Optional<GrupoCategoria> findById(Integer id) {
        return grupoCategoriaDAO.findById(id);
    }

    // Obtener por ID con categorías
    public Optional<GrupoCategoria> findByIdWithCategorias(Integer id) {
        return grupoCategoriaDAO.findByIdWithCategorias(id);
    }

    // Obtener por nombre
    public Optional<GrupoCategoria> findByNombre(String nombre) {
        return grupoCategoriaDAO.findByNombre(nombre);
    }

    // Buscar por nombre que contenga texto
    public List<GrupoCategoria> findByNombreContaining(String nombre) {
        return grupoCategoriaDAO.findByNombreContaining(nombre);
    }

    // Buscar por descripción que contenga texto
    public List<GrupoCategoria> findByDescripcionContaining(String descripcion) {
        return grupoCategoriaDAO.findByDescripcionContaining(descripcion);
    }

    // Crear nuevo grupo
    public GrupoCategoria save(GrupoCategoria grupoCategoria) {
        validateGrupoCategoria(grupoCategoria);
        return grupoCategoriaDAO.save(grupoCategoria);
    }

    // Actualizar grupo existente
    public GrupoCategoria update(Integer id, GrupoCategoria grupoCategoria) {
        Optional<GrupoCategoria> existingGrupo = grupoCategoriaDAO.findById(id);
        if (existingGrupo.isEmpty()) {
            throw new RuntimeException("Grupo de categoría no encontrado con ID: " + id);
        }

        GrupoCategoria grupo = existingGrupo.get();
        grupo.setNombre(grupoCategoria.getNombre());
        grupo.setDescripcion(grupoCategoria.getDescripcion());

        validateGrupoCategoriaForUpdate(grupo);
        return grupoCategoriaDAO.update(grupo);
    }

    // Eliminar por ID
    public void deleteById(Integer id) {
        Optional<GrupoCategoria> grupo = grupoCategoriaDAO.findByIdWithCategorias(id);
        if (grupo.isEmpty()) {
            throw new RuntimeException("Grupo de categoría no encontrado con ID: " + id);
        }

        if (!grupo.get().getCategorias().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el grupo porque tiene categorías asociadas");
        }

        grupoCategoriaDAO.deleteById(id);
    }

    // Verificar si existe por nombre
    public boolean existsByNombre(String nombre) {
        return grupoCategoriaDAO.existsByNombre(nombre);
    }

    // Verificar si existe
    public boolean existsById(Integer id) {
        return grupoCategoriaDAO.existsById(id);
    }

    // Contar total de grupos
    public long count() {
        return grupoCategoriaDAO.count();
    }

    // Métodos de validación privados
    private void validateGrupoCategoria(GrupoCategoria grupoCategoria) {
        if (grupoCategoria.getNombre() == null || grupoCategoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del grupo no puede estar vacío");
        }

        if (grupoCategoria.getNombre().length() > 255) {
            throw new IllegalArgumentException("El nombre del grupo no puede exceder 255 caracteres");
        }

        if (grupoCategoriaDAO.existsByNombre(grupoCategoria.getNombre())) {
            throw new RuntimeException("Ya existe un grupo con el nombre: " + grupoCategoria.getNombre());
        }
    }

    private void validateGrupoCategoriaForUpdate(GrupoCategoria grupoCategoria) {
        if (grupoCategoria.getNombre() == null || grupoCategoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del grupo no puede estar vacío");
        }

        if (grupoCategoria.getNombre().length() > 255) {
            throw new IllegalArgumentException("El nombre del grupo no puede exceder 255 caracteres");
        }

        if (grupoCategoriaDAO.existsByNombreAndIdNot(grupoCategoria.getNombre(), grupoCategoria.getId())) {
            throw new RuntimeException("Ya existe un grupo con el nombre: " + grupoCategoria.getNombre());
        }
    }
}
