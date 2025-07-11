package siaj.inventarios.dao;

import siaj.inventarios.model.Entrada;

import java.util.List;

public interface EntradaDAO {

    List<Entrada> obtenerTodasLasEntradas();
    Entrada agregarEntrada(Entrada entrada);
    Entrada buscarPorId(int id);
}
