package siaj.inventarios.dao;

import org.hibernate.Session;
import siaj.inventarios.model.Producto;
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

            productos = session.createQuery("FROM Producto p", Producto.class).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {

            System.err.println("Error al listar productos: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar productos", e);
        }


        return productos;
    }
}
