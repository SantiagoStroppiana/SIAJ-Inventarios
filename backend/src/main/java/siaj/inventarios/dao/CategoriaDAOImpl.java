package siaj.inventarios.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Categoria;
import siaj.inventarios.model.GrupoCategoria;
import siaj.inventarios.model.Producto;
import siaj.inventarios.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class CategoriaDAOImpl implements CategoriaDAO {

    @Override
    public MensajesResultados crearCategoria(Categoria categoria) {
        Session session = HibernateUtil.getSession();

        try {
            session.beginTransaction();

            // Validar que la categoría no tenga un ID asignado (es nueva)
            if (categoria.getId() != 0) {
                return new MensajesResultados(false, "La categoría ya tiene un ID asignado. Use el método de modificación.");
            }

            // Si viene grupoId, buscar y asignar el grupo
            if (categoria.getGrupoId() != 0) {
                GrupoCategoria grupo = session.get(GrupoCategoria.class, categoria.getGrupoId());
                if (grupo == null) {
                    return new MensajesResultados(false, "Grupo con ID " + categoria.getGrupoId() + " no encontrado.");
                }
                categoria.setGrupo(grupo);
            }

            // Persistir la nueva categoría
            session.persist(categoria);
            session.getTransaction().commit();
            return new MensajesResultados(true, "Categoría registrada con éxito");
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            return new MensajesResultados(false, "Error al crear categoría: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<Categoria> listarCategorias() {
        Session session = HibernateUtil.getSession();
        try {
            return session.createQuery("from Categoria", Categoria.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public MensajesResultados modificarCategoria(Categoria categoria) {
        Session session = HibernateUtil.getSession();

        try {
            session.beginTransaction();

            // Cargar la categoría existente
            Categoria categoriaExistente = session.get(Categoria.class, categoria.getId());

            if (categoriaExistente == null) {
                return new MensajesResultados(false, "Categoría no encontrada");
            }

            // Actualizar campos
            categoriaExistente.setNombre(categoria.getNombre());
            categoriaExistente.setDescripcion(categoria.getDescripcion());

            // Si viene grupoId, buscar y asignar el grupo
            if (categoria.getGrupoId() != null) {
                GrupoCategoria grupo = session.get(GrupoCategoria.class, categoria.getGrupoId());
                if (grupo != null) {
                    categoriaExistente.setGrupo(grupo);
                }
            }

            session.merge(categoriaExistente);
            session.getTransaction().commit();
            return new MensajesResultados(true, "Categoría editada con éxito");
        } catch (Exception e) {
            session.getTransaction().rollback();
            return new MensajesResultados(false, "Error al editar categoría: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    // Nuevos métodos para manejar grupos
    @Override
    public List<Categoria> listarCategoriasPorGrupo(int grupoId) {
        List<Categoria> categorias;

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            Query<Categoria> query = session.createQuery(
                    "FROM Categoria c WHERE c.grupoId = :grupoId ORDER BY c.nombre",
                    Categoria.class
            );
            query.setParameter("grupoId", grupoId);
            categorias = query.getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error al listar categorías por grupo: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar categorías por grupo", e);
        }

        return categorias;
    }

    @Override
    public List<Categoria> listarCategoriasPorGrupo(GrupoCategoria grupo) {
        return listarCategoriasPorGrupo(grupo.getId());
    }

    @Override
    public Optional<Categoria> obtenerCategoriaPorId(int id) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            // Incluir el grupo en la consulta
            Query<Categoria> query = session.createQuery(
                    "SELECT c FROM Categoria c LEFT JOIN FETCH c.grupo WHERE c.id = :id",
                    Categoria.class
            );
            query.setParameter("id", id);

            Categoria categoria = query.uniqueResult();
            session.getTransaction().commit();

            return Optional.ofNullable(categoria);
        } catch (Exception e) {
            System.err.println("Error al obtener categoría por ID: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Categoria> obtenerCategoriaPorNombre(String nombre) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            Query<Categoria> query = session.createQuery(
                    "SELECT c FROM Categoria c LEFT JOIN FETCH c.grupo WHERE c.nombre = :nombre",
                    Categoria.class
            );
            query.setParameter("nombre", nombre);

            Categoria categoria = query.uniqueResult();
            session.getTransaction().commit();

            return Optional.ofNullable(categoria);
        } catch (Exception e) {
            System.err.println("Error al obtener categoría por nombre: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Categoria> buscarCategoriasPorNombre(String nombre) {
        List<Categoria> categorias;

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            Query<Categoria> query = session.createQuery(
                    "SELECT c FROM Categoria c LEFT JOIN FETCH c.grupo WHERE LOWER(c.nombre) LIKE LOWER(:nombre) ORDER BY c.nombre",
                    Categoria.class
            );
            query.setParameter("nombre", "%" + nombre + "%");
            categorias = query.getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error al buscar categorías por nombre: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al buscar categorías por nombre", e);
        }

        return categorias;
    }

    @Override
    public MensajesResultados eliminarCategoria(int id) {
        Session session = HibernateUtil.getSession();

        try {
            session.beginTransaction();

            // Verificar si la categoría tiene productos asociados
            Query<Long> countQuery = session.createQuery(
                    "SELECT COUNT(p) FROM Producto p JOIN p.categorias c WHERE c.id = :categoriaId",
                    Long.class
            );
            countQuery.setParameter("categoriaId", id);
            Long count = countQuery.uniqueResult();

            if (count > 0) {
                return new MensajesResultados(false,
                        "No se puede eliminar la categoría porque tiene " + count + " productos asociados");
            }

            // Eliminar la categoría
            Categoria categoria = session.get(Categoria.class, id);
            if (categoria != null) {
                session.remove(categoria);
                session.getTransaction().commit();
                return new MensajesResultados(true, "Categoría eliminada con éxito");
            } else {
                return new MensajesResultados(false, "Categoría no encontrada");
            }

        } catch (Exception e) {
            session.getTransaction().rollback();
            return new MensajesResultados(false, "Error al eliminar categoría: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public List<Producto> obtenerProductosPorCategoria(int categoriaId) {
        List<Producto> productos;

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            Query<Producto> query = session.createQuery(
                    "SELECT DISTINCT p FROM Producto p JOIN p.categorias c WHERE c.id = :categoriaId ORDER BY p.nombre",
                    Producto.class
            );
            query.setParameter("categoriaId", categoriaId);
            productos = query.getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error al obtener productos por categoría: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener productos por categoría", e);
        }

        return productos;
    }

    @Override
    public long contarProductosPorCategoria(int categoriaId) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(DISTINCT p) FROM Producto p JOIN p.categorias c WHERE c.id = :categoriaId",
                    Long.class
            );
            query.setParameter("categoriaId", categoriaId);

            Long count = query.uniqueResult();
            session.getTransaction().commit();

            return count != null ? count : 0L;
        } catch (Exception e) {
            System.err.println("Error al contar productos por categoría: " + e.getMessage());
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public boolean existeCategoriaPorNombre(String nombre) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(c) FROM Categoria c WHERE LOWER(c.nombre) = LOWER(:nombre)",
                    Long.class
            );
            query.setParameter("nombre", nombre);

            Long count = query.uniqueResult();
            session.getTransaction().commit();

            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("Error al verificar existencia de categoría: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existeCategoriaPorNombreYGrupo(String nombre, int grupoId) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(c) FROM Categoria c WHERE LOWER(c.nombre) = LOWER(:nombre) AND c.grupoId = :grupoId",
                    Long.class
            );
            query.setParameter("nombre", nombre);
            query.setParameter("grupoId", grupoId);

            Long count = query.uniqueResult();
            session.getTransaction().commit();

            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("Error al verificar existencia de categoría por nombre y grupo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Métodos para obtener estadísticas
    @Override
    public long contarCategorias() {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            Query<Long> query = session.createQuery("SELECT COUNT(c) FROM Categoria c", Long.class);
            Long count = query.uniqueResult();

            session.getTransaction().commit();
            return count != null ? count : 0L;
        } catch (Exception e) {
            System.err.println("Error al contar categorías: " + e.getMessage());
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public long contarCategoriasPorGrupo(int grupoId) {
        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(c) FROM Categoria c WHERE c.grupoId = :grupoId",
                    Long.class
            );
            query.setParameter("grupoId", grupoId);

            Long count = query.uniqueResult();
            session.getTransaction().commit();

            return count != null ? count : 0L;
        } catch (Exception e) {
            System.err.println("Error al contar categorías por grupo: " + e.getMessage());
            e.printStackTrace();
            return 0L;
        }
    }

    // Método para obtener categorías con sus productos (útil para reportes)
    @Override
    public List<Categoria> obtenerCategoriasConProductos() {
        List<Categoria> categorias;

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            categorias = session.createQuery(
                    "SELECT DISTINCT c FROM Categoria c " +
                            "LEFT JOIN FETCH c.grupo " +
                            "LEFT JOIN FETCH c.productos " +
                            "ORDER BY c.grupo.nombre, c.nombre",
                    Categoria.class
            ).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error al obtener categorías con productos: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener categorías con productos", e);
        }

        return categorias;
    }
}