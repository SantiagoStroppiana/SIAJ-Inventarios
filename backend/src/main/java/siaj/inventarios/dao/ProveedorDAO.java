package siaj.inventarios.dao;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import siaj.inventarios.model.Proveedor;

import java.util.List;

public interface ProveedorDAO {

    void registrarProveedor(Proveedor proveedor);

    void actualizarProveedor(Proveedor proveedor);

    List<Proveedor> listarproveedores();

    Proveedor buscarProveedorPorEmail(@NotBlank String email);
}
