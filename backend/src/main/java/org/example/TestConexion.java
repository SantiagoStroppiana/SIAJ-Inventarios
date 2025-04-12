package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import siaj.inventarios.dao.UsuarioDAOImpl;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Rol;
import siaj.inventarios.model.Usuario;
import siaj.inventarios.service.UsuarioServiceImpl;
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

//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Introduzca el nombre del rol: ");
//        String nombre = scanner.nextLine();
//
//        Rol rol = new Rol();
//        rol.setNombre(nombre);
//        Transaction tx = null;
//
//        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
//
//            tx = session.beginTransaction();
//            session.save(rol);
//            tx.commit();
//            System.out.println("Rol guardado exitosamente " + rol.getNombre());
//        }catch (Exception e) {
//            if (tx != null) {
//                tx.rollback();
//                e.printStackTrace();
//            }
//        }


        UsuarioServiceImpl usuarioService = new UsuarioServiceImpl(new UsuarioDAOImpl());

        // Crear un Scanner para leer la entrada del usuario
        Scanner scanner = new Scanner(System.in);

        // Pedir los datos del usuario
        System.out.println("Ingrese los datos del usuario para registrar:");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        // Crear el objeto Usuario con los datos ingresados
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setPassword(password);

        // Probar el registro del usuario
        System.out.println("\nIntentando registrar el usuario...");
        MensajesResultados resultado = usuarioService.registrarUsuario(usuario);
        System.out.println("Resultado: " + resultado.getMensaje());

        // Cerrar el scanner
        scanner.close();

    }
}
