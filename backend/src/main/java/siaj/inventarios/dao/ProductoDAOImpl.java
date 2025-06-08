package siaj.inventarios.dao;

import org.hibernate.Session;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Producto;
import siaj.inventarios.model.Proveedor;
import siaj.inventarios.model.Usuario;
import siaj.inventarios.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public List<Producto> listarProductos(){
        List<Producto> productos;


        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            productos = session.createQuery("FROM Producto", Producto.class).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {

            System.err.println("Error al listar productos: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar productos", e);
        }


        return productos;
    }

    @Override
    public MensajesResultados crearProducto (Producto producto){

        Session session = HibernateUtil.getSession();
        String re;

        try{
            session.beginTransaction();
            session.persist(producto);
            session.getTransaction().commit();
            return new MensajesResultados(true, "Producto registrado con exito");
//            re= "Producto registtrado con exito";

        }catch (Exception e) {

            session.getTransaction().rollback();
            return new MensajesResultados(false, "Error al crear producto: " + e.getMessage());
//            re="Error al crear producto: " + e.getMessage();

//            throw new RuntimeException("Erorr al crear producto" + e.getMessage());
        }finally {
            session.close();
        }
//        return re;
    }



    @Override
    public boolean buscarSku(String sku) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(p) FROM Producto p WHERE p.sku = :sku", Long.class)
                    .setParameter("sku", sku)
                    .uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Producto buscarPorSku(String sku) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Producto p WHERE p.sku = :sku", Producto.class)
                    .setParameter("sku", sku)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public MensajesResultados modificarProducto (Producto producto){

        Session session = HibernateUtil.getSession();
        String re;

        try{
            session.beginTransaction();
            session.merge(producto);
            session.getTransaction().commit();
            return new MensajesResultados(true, "Producto Modificado con exito");
//            re= "Producto editado con exito";

        }catch (Exception e) {

            session.getTransaction().rollback();
            return new MensajesResultados(false, "Error al modificar producto: " + e.getMessage());
//            re="Error al editar producto: " + e.getMessage();

//            throw new RuntimeException("Erorr al editar producto" + e.getMessage());
        }finally {
            session.close();
        }
//        return re;
    }

    @Override
    public List<Producto> filtrarProveedor(int id) {
        Session session = HibernateUtil.getSession();
        List<Producto> productos = new ArrayList<>();

        try {
            session.beginTransaction();
            productos = session.createQuery("FROM Producto p WHERE p.proveedorid.id = :id", Producto.class)
                    .setParameter("id", id)
                    .getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Error al buscar productos del proveedor: " + e.getMessage());
        } finally {
            session.close();
        }

        return productos;
    }

    @Override
    public List<Producto> filtrarCategoria(int idCategoria) {
        Session session = HibernateUtil.getSession();
        List<Producto> productos = new ArrayList<>();

        try {
            session.beginTransaction();

            productos = session.createQuery(
                            "SELECT cp.producto FROM CategoriaProducto cp WHERE cp.categoria.id = :idCategoria", Producto.class)
                    .setParameter("idCategoria", idCategoria)
                    .getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Error al buscar productos por categoría: " + e.getMessage());
        } finally {
            session.close();
        }

        return productos;
    }

}
