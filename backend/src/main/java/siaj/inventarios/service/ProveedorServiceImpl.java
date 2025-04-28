package siaj.inventarios.service;

import org.mindrot.jbcrypt.BCrypt;
import siaj.inventarios.dao.ProveedorDAO;
import siaj.inventarios.dao.ProveedorDAOImpl;
import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Proveedor;
import siaj.inventarios.model.Rol;

import java.util.List;

public class ProveedorServiceImpl implements ProveedorService {
    private final ProveedorDAO proveedorDAO;

    public ProveedorServiceImpl(ProveedorDAO proveedorDAO) {this.proveedorDAO = proveedorDAO;}
    @Override

    public MensajesResultados registrarProveedor(String razonSocial, String email){
        MensajesResultados mensaje;

        if (email == null ||  email.isEmpty() || razonSocial == null || razonSocial.isEmpty()){
            return new MensajesResultados(false,"Email o Razon Social no pueden estar vacios");

        }
        Proveedor proveedor = proveedorDAO.buscarProveedorPorEmail(email);

        if (proveedor == null) {
            mensaje = new MensajesResultados(false,"El proveedor no existe");
            return mensaje;
        }else{
            mensaje = new MensajesResultados(true,"El proveedor ya existe");
        }
        return mensaje;
    }
    @Override
    public MensajesResultados registrarProveedor(Proveedor proveedor){
        if(proveedor.getRazonSocial() == null || proveedor.getRazonSocial().isEmpty()){
            return new MensajesResultados(false,"Razon Social no puede estar vacia");
        }

        Proveedor existe = proveedorDAO.buscarProveedorPorEmail(proveedor.getEmail());
        if(existe != null){
            return new MensajesResultados(false,"El proveedor ya existe");
        }

        try {
            proveedorDAO.registrarProveedor(proveedor);
            return new MensajesResultados(true,"Proveedor registrado correctamente");

        }catch (Exception e){
            System.out.println("Error al registrar Proveedor" + e.getMessage());
            return new MensajesResultados(false,"Error al registrar Proveedor" + e.getMessage());
        }
    }
    @Override
    public MensajesResultados actualizarProveedor(Proveedor proveedor){
        return new MensajesResultados(false,"El proveedor fue actualizado correctamente");

    }
    @Override
    public List<Proveedor> listarProveedores(){return proveedorDAO.listarproveedores();}


}
