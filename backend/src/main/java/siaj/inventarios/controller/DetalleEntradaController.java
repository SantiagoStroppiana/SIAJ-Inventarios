package siaj.inventarios.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import siaj.inventarios.dto.DetalleEntradaDTO;
import siaj.inventarios.model.DetalleEntrada;
import siaj.inventarios.service.CategoriaService;
import siaj.inventarios.service.DetalleEntradaService;

import java.util.List;

public class DetalleEntradaController {

    private DetalleEntradaService detalleEntradaService;

    public DetalleEntradaController(DetalleEntradaService detalleEntradaService) {
        this.detalleEntradaService = detalleEntradaService;
    }

    public void registrarDetalleEntrada(Context ctx) {
        DetalleEntradaDTO dto = ctx.bodyAsClass(DetalleEntradaDTO.class);
        try {
            DetalleEntrada detalle = detalleEntradaService.registrarDetalle(dto);
            ctx.status(201).json(detalle);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(400).result("Error al registrar detalle de entrada: " + e.getMessage());
        }
    }

    public List<DetalleEntradaDTO> listarDetalleEntradas() {
        return detalleEntradaService.listarDetalleEntradas();
    }
}
