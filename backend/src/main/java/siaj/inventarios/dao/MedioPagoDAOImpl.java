package siaj.inventarios.dao;

import org.hibernate.Session;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.MedioPago;
import siaj.inventarios.model.Producto;
import siaj.inventarios.util.HibernateUtil;

import java.util.List;

public class MedioPagoDAOImpl implements MedioPagoDAO {


    @Override
    public MensajesResultados crearMedioPago (MedioPago mediopago){

        Session session = HibernateUtil.getSession();

        try{
            session.beginTransaction();
            session.persist(mediopago);
            session.getTransaction().commit();
            return new MensajesResultados(true, "Medio de pago registrado con exito");

        }catch (Exception e) {

            session.getTransaction().rollback();
            return new MensajesResultados(false, "Error al crear el medio de pago: " + e.getMessage());

        }finally {
            session.close();
        }
    }


    @Override
    public List<MedioPago> listarMedioPago(){
        List<MedioPago> mediosPagos;


        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            mediosPagos = session.createQuery("FROM MedioPago", MedioPago.class).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {

            System.err.println("Error al listar medios de pagos: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar medios de pagos", e);
        }


        return mediosPagos;
    }
}
