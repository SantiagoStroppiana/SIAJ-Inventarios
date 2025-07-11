package siaj.inventarios.service;

import siaj.inventarios.model.Entrada;

import java.util.List;

public interface EntradaService {
    Entrada agregarEntrada(Entrada entrada);
    List<Entrada> obtenerTodasLasEntradas();
    Entrada buscarPorId(int id);
}
