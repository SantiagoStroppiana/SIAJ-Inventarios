package siaj.inventarios.dao;

import org.hibernate.Session;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.dto.UsuarioDTO;
import siaj.inventarios.dto.VentaDTO;
import siaj.inventarios.model.Usuario;
import siaj.inventarios.model.Venta;
import siaj.inventarios.util.HibernateUtil;

import java.util.List;


public class VentaDAOImpl implements VentaDAO{


    @Override
    public List<Venta> obtenerTodasLasVentas() {
        List<Venta> ventas;

        try (Session session = HibernateUtil.getSession()) {
            session.beginTransaction();

            ventas = session.createQuery("FROM Venta v", Venta.class).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error al listar Venta: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar ventas", e);
        }

        return ventas;
    }

    @Override
    public VentaDTO agregarVenta(Venta v) {
        Session session = HibernateUtil.getSession();

        try {
            session.beginTransaction();
            session.persist(v);
            session.getTransaction().commit();
            UsuarioDTO usuarioDTO = new UsuarioDTO(v.getUsuario().getId());

            return new VentaDTO(
                    v.getId(),
                    v.getTotal(),
                    v.getEstado().toString(),
                    v.getFechaPago().toString(),
                    usuarioDTO,
                    v.getMedioPago()
            );

        } catch (Exception e) {
            session.getTransaction().rollback();
            throw new RuntimeException("Error al agregar venta: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public Venta obtenerPorId(int id) {
        Session session = HibernateUtil.getSession();
        try {
            return session.get(Venta.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public MensajesResultados actualizarVenta(Venta venta) {

        Session session = HibernateUtil.getSession();
        String re;

        try{
            session.beginTransaction();
            session.merge(venta);
            session.getTransaction().commit();
            return new MensajesResultados(true, "venta Modificado con exito");


        }catch (Exception e) {

            session.getTransaction().rollback();
            return new MensajesResultados(false, "Error al modificar venta: " + e.getMessage());

        }finally {
            session.close();
        }

    }
    }

    /*@Override
    public void eliminarVenta(Venta venta) {

    }*/




