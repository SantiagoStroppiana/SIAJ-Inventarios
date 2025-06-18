package siaj.inventarios.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import siaj.inventarios.model.GrupoCategoria;
import siaj.inventarios.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class GrupoCategoriaDAOImpl implements GrupoCategoriaDAO {

    private SessionFactory sessionFactory;

    public GrupoCategoriaDAOImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    // Obtener todos los grupos SIN categorías (para casos donde no las necesitas)
    public List<GrupoCategoria> findAllBasic() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM GrupoCategoria", GrupoCategoria.class).list();
        }
    }

    // Obtener todos los grupos CON categorías (método principal)
    public List<GrupoCategoria> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<GrupoCategoria> query = session.createQuery(
                    "SELECT DISTINCT gc FROM GrupoCategoria gc LEFT JOIN FETCH gc.categorias",
                    GrupoCategoria.class
            );
            return query.list();
        }
    }

    // Obtener todos los grupos con sus categorías (método alternativo)
    public List<GrupoCategoria> findAllWithCategorias() {
        return findAll(); // Ahora ambos métodos hacen lo mismo
    }

    // Obtener por ID SIN categorías
    public Optional<GrupoCategoria> findByIdBasic(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            GrupoCategoria grupo = session.get(GrupoCategoria.class, id);
            return Optional.ofNullable(grupo);
        }
    }

    // Obtener por ID CON categorías (método principal)
    public Optional<GrupoCategoria> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Query<GrupoCategoria> query = session.createQuery(
                    "SELECT gc FROM GrupoCategoria gc LEFT JOIN FETCH gc.categorias WHERE gc.id = :id",
                    GrupoCategoria.class
            );
            query.setParameter("id", id);
            GrupoCategoria grupo = query.uniqueResult();
            return Optional.ofNullable(grupo);
        }
    }

    // Obtener por ID con categorías (método alternativo)
    public Optional<GrupoCategoria> findByIdWithCategorias(Integer id) {
        return findById(id); // Ahora ambos métodos hacen lo mismo
    }

    // Buscar por nombre exacto
    public Optional<GrupoCategoria> findByNombre(String nombre) {
        try (Session session = sessionFactory.openSession()) {
            Query<GrupoCategoria> query = session.createQuery(
                    "SELECT gc FROM GrupoCategoria gc LEFT JOIN FETCH gc.categorias WHERE gc.nombre = :nombre",
                    GrupoCategoria.class
            );
            query.setParameter("nombre", nombre);
            GrupoCategoria grupo = query.uniqueResult();
            return Optional.ofNullable(grupo);
        }
    }

    // Buscar por nombre que contenga texto
    public List<GrupoCategoria> findByNombreContaining(String nombre) {
        try (Session session = sessionFactory.openSession()) {
            Query<GrupoCategoria> query = session.createQuery(
                    "SELECT DISTINCT gc FROM GrupoCategoria gc LEFT JOIN FETCH gc.categorias WHERE LOWER(gc.nombre) LIKE LOWER(:nombre)",
                    GrupoCategoria.class
            );
            query.setParameter("nombre", "%" + nombre + "%");
            return query.list();
        }
    }

    // Buscar por descripción que contenga texto
    public List<GrupoCategoria> findByDescripcionContaining(String descripcion) {
        try (Session session = sessionFactory.openSession()) {
            Query<GrupoCategoria> query = session.createQuery(
                    "SELECT DISTINCT gc FROM GrupoCategoria gc LEFT JOIN FETCH gc.categorias WHERE LOWER(gc.descripcion) LIKE LOWER(:descripcion)",
                    GrupoCategoria.class
            );
            query.setParameter("descripcion", "%" + descripcion + "%");
            return query.list();
        }
    }

    // Guardar nuevo grupo
    public GrupoCategoria save(GrupoCategoria grupoCategoria) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(grupoCategoria);
            transaction.commit();
            return grupoCategoria;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Actualizar grupo existente
    public GrupoCategoria update(GrupoCategoria grupoCategoria) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(grupoCategoria);
            transaction.commit();
            return grupoCategoria;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Eliminar por ID
    public void deleteById(Integer id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            GrupoCategoria grupo = session.get(GrupoCategoria.class, id);
            if (grupo != null) {
                session.remove(grupo);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    // Verificar si existe por nombre
    public boolean existsByNombre(String nombre) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(gc) FROM GrupoCategoria gc WHERE gc.nombre = :nombre",
                    Long.class
            );
            query.setParameter("nombre", nombre);
            return query.uniqueResult() > 0;
        }
    }

    // Verificar si existe por nombre excluyendo un ID
    public boolean existsByNombreAndIdNot(String nombre, Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(gc) FROM GrupoCategoria gc WHERE gc.nombre = :nombre AND gc.id != :id",
                    Long.class
            );
            query.setParameter("nombre", nombre);
            query.setParameter("id", id);
            return query.uniqueResult() > 0;
        }
    }

    // Verificar si existe por ID
    public boolean existsById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(GrupoCategoria.class, id) != null;
        }
    }

    // Contar total de grupos
    public long count() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(gc) FROM GrupoCategoria gc", Long.class);
            return query.uniqueResult();
        }
    }

    // Método duplicado corregido
    @Override
    public boolean existsByNombreAndIdNot(String nombre, int id) {
        return existsByNombreAndIdNot(nombre, Integer.valueOf(id));
    }
}