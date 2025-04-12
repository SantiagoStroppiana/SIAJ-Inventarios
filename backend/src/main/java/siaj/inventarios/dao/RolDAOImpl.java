package siaj.inventarios.dao;

import org.hibernate.Session;
import siaj.inventarios.model.Rol;
import siaj.inventarios.util.HibernateUtil;

public class RolDAOImpl implements RolDAO {
    @Override
    public Rol obteneRolPorNombre(String nombre) {

        Session session = HibernateUtil.getSession();

        try{
            return session.createQuery("FROM Rol WHERE nombre = :nombreRol", Rol.class)
                    .setParameter("nombreRol", nombre)
                    .uniqueResult();
        }catch (Exception e){
            throw new RuntimeException("Error al obtener rol" + e.getMessage());
        }

    }
}
