package siaj.inventarios.service;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Proveedor;

import java.util.List;

public interface ProveedorService {
    MensajesResultados registrarProveedor(String razonSocial, String email);
    MensajesResultados registrarProveedor(Proveedor proveedor);
    MensajesResultados actualizarProveedor(Proveedor proveedor);
    List<Proveedor> listarProveedores();
    Proveedor buscarPorId(int id);
}
