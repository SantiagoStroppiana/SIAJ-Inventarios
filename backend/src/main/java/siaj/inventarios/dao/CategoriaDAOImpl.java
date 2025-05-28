package siaj.inventarios.dao;

import org.hibernate.Session;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Categoria;
import siaj.inventarios.model.Producto;
import siaj.inventarios.util.HibernateUtil;

import java.util.List;

public class CategoriaDAOImpl implements CategoriaDAO {

    @Override
    public MensajesResultados crearCategoria(Categoria categoria){

        Session session = HibernateUtil.getSession();


        try{
            session.beginTransaction();
            session.persist(categoria);
            session.getTransaction().commit();
            return new MensajesResultados(true, "Categoria registrada con exito");
        }catch (Exception e) {

            session.getTransaction().rollback();
            return new MensajesResultados(false, "Error al crear categoria: ");

        }finally {
            session.close();
        }
    }
    @Override
    public List<Categoria> listarCategorias(){
        List<Categoria> categorias;


        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            categorias = session.createQuery("FROM Categoria", Categoria.class).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {

            System.err.println("Error al listar categorias: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar categorias", e);
        }


        return categorias;
    }

    @Override
    public MensajesResultados modificarCategoria (Categoria categoria){

        Session session = HibernateUtil.getSession();

        try{
            session.beginTransaction();
            session.merge(categoria);
            session.getTransaction().commit();
            return new MensajesResultados(true, "Categoria editado con exito");

        }catch (Exception e) {

            session.getTransaction().rollback();
            return new MensajesResultados(false, "Error al editar categoria: ");
        }finally {
            session.close();
        }
    }

}
