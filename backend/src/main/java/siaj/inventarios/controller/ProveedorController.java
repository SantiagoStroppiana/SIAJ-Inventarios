package siaj.inventarios.controller;

import siaj.inventarios.dto.MensajesResultados;
import siaj.inventarios.model.Proveedor;
import siaj.inventarios.service.ProveedorService;

import java.util.List;

public class ProveedorController {

    private ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {this.proveedorService = proveedorService;}

    public MensajesResultados registrarProveedor(Proveedor proveedor) {
        return proveedorService.registrarProveedor(proveedor);}

    public MensajesResultados actualizarProveedor(Proveedor proveedor) {return proveedorService.actualizarProveedor(proveedor);}

    public List<Proveedor> mostrarProveedores(){

        return proveedorService.listarProveedores();
    }

}
