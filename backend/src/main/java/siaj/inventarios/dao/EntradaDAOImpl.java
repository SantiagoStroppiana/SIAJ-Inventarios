package siaj.inventarios.dao;

import org.hibernate.Session;
import siaj.inventarios.model.Entrada;
import siaj.inventarios.model.OrdenCompra;
import siaj.inventarios.util.HibernateUtil;

import java.util.List;

public class EntradaDAOImpl implements EntradaDAO {


    @Override
    public List<Entrada> obtenerTodasLasEntradas() {
        List<Entrada> entradas;

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            entradas = session.createQuery("FROM Entrada o", Entrada.class).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error al listar entradas: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar las entradas", e);
        }

        return entradas;

    }

    @Override
    public Entrada agregarEntrada(Entrada entrada) {
        Session session = HibernateUtil.getSession();

        try {
            session.beginTransaction();
            session.persist(entrada);
            session.getTransaction().commit();
            return entrada;

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Error al agregar entrada: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public Entrada buscarPorId(int id) {
        Session session = HibernateUtil.getSession();
        try {
            return session.get(Entrada.class, id);
        } finally {
            session.close();
        }
    }

}
