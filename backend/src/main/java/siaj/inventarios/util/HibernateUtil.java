package siaj.inventarios.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import siaj.inventarios.model.Usuario;

import java.util.Properties;


public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            Dotenv dotenv = Dotenv.load();
            Properties properties = new Properties();
            properties.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
//            properties.setProperty("hibernate.connection.url", "jdbc:mysql://" + dotenv.get("DB_HOST") + ":" + dotenv.get("DB_PORT") + "/" + dotenv.get("DB_NAME"));
//            properties.setProperty("hibernate.connection.username", dotenv.get("DB_USER"));
//            properties.setProperty("hibernate.connection.password", dotenv.get("DB_PASSWORD"));

            String dbHost = System.getenv("DB_HOST");
            String dbPort = System.getenv("DB_PORT");
            String dbName = System.getenv("DB_NAME");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");

            properties.setProperty("hibernate.connection.url", "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName);
            properties.setProperty("hibernate.connection.username", dbUser);
            properties.setProperty("hibernate.connection.password", dbPassword);

            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            properties.setProperty("hibernate.show_sql", "true");
            properties.setProperty("hibernate.hbm2ddl.auto", "update");

            Configuration configuration = new Configuration();
            configuration.setProperties(properties);

            configuration.addAnnotatedClass(siaj.inventarios.model.Usuario.class);
            configuration.addAnnotatedClass(siaj.inventarios.model.Rol.class);
            configuration.addAnnotatedClass(siaj.inventarios.model.Producto.class);
            configuration.addAnnotatedClass(siaj.inventarios.model.Proveedor.class);
            configuration.addAnnotatedClass(siaj.inventarios.model.Categoria.class);
            configuration.addAnnotatedClass(siaj.inventarios.model.CategoriaProducto.class);
            configuration.addAnnotatedClass(siaj.inventarios.model.Venta.class);
            configuration.addAnnotatedClass(siaj.inventarios.model.MedioPago.class);
            configuration.addAnnotatedClass(siaj.inventarios.model.DetalleVenta.class);
            configuration.addAnnotatedClass(siaj.inventarios.model.OrdenCompra.class);
            configuration.addAnnotatedClass(siaj.inventarios.model.DetalleOrdenCompra.class);
            configuration.addAnnotatedClass(siaj.inventarios.model.Entrada.class);
            configuration.addAnnotatedClass(siaj.inventarios.model.DetalleEntrada.class);



            sessionFactory = configuration.buildSessionFactory();


        }catch (Throwable ex) {
            throw new ExceptionInInitializerError("Error inciar SessionFactory" + ex.getMessage());
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }


    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

}
