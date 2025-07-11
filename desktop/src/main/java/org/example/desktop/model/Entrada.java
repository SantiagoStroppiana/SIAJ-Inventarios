package org.example.desktop.model;


import java.time.LocalDateTime;

public class Entrada {


    private int id;

    private OrdenCompra ordenCompra;


    private LocalDateTime fecha;

    public Entrada(int id, OrdenCompra ordenCompra, LocalDateTime fecha) {
        this.id = id;
        this.ordenCompra = ordenCompra;
        this.fecha = fecha;
    }

    public Entrada() {
    }
// --- Getters y Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OrdenCompra getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(OrdenCompra ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Entrada{" +
                "id=" + id +
                ", ordenCompra=" + ordenCompra +
                ", fecha=" + fecha +
                '}';
    }
}
