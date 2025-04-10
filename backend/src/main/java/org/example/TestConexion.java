package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import siaj.inventarios.util.HibernateUtil;

public class TestConexion {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        if (session != null) {
            System.out.println("✅ Conexión exitosa a la base de datos.");
            session.close();
        } else {
            System.out.println("❌ Error al conectar con la base de datos.");
        }

        sessionFactory.close();
    }
}
