package siaj.inventarios.controller;

import siaj.inventarios.dto.OrdenCompraDTO;
import siaj.inventarios.model.Entrada;
import siaj.inventarios.model.OrdenCompra;
import siaj.inventarios.service.EntradaService;
import siaj.inventarios.service.OrdenCompraService;

import java.util.List;

public class EntradaController {

    private EntradaService entradaService;

    public EntradaController(){}
    public EntradaController(EntradaService entradaService){
        this.entradaService = entradaService;
    }
    public List<Entrada> listarEntradas() {
        return entradaService.obtenerTodasLasEntradas();
    }

    public Entrada agregarEntrada(Entrada entrada) {
        return entradaService.agregarEntrada(entrada);
    }

}
