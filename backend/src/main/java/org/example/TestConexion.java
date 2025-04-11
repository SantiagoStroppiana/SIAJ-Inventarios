package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import siaj.inventarios.model.Rol;
import siaj.inventarios.util.HibernateUtil;

import java.util.Scanner;

public class TestConexion {
    public static void main(String[] args) {

//        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//        Session session = sessionFactory.openSession();
//
//        if (session != null) {
//            System.out.println("✅ Conexión exitosa a la base de datos.");
//            session.close();
//        } else {
//            System.out.println("❌ Error al conectar con la base de datos.");
//        }
//
//        sessionFactory.close();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduzca el nombre del rol: ");
        String nombre = scanner.nextLine();

        Rol rol = new Rol();
        rol.setNombre(nombre);
        Transaction tx = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()) {

            tx = session.beginTransaction();
            session.save(rol);
            tx.commit();
            System.out.println("Rol guardado exitosamente " + rol.getNombre());
        }catch (Exception e) {
            if (tx != null) {
                tx.rollback();
                e.printStackTrace();
            }
        }

    }
}
