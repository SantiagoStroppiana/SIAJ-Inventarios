package org.example.desktop.model;


public class MedioPago {


    private int id;


    private String tipo;

    public MedioPago() {
    }

    public MedioPago(String tipo) {
        this.tipo = tipo;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public MedioPago(int id, String tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "MedioPago{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
