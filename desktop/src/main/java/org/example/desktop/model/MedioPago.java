package org.example.desktop.model;


public class MedioPago {


    private int id;


    private String nombre;

    public MedioPago() {
    }

    public MedioPago(String nombre) {
        this.nombre = nombre;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public MedioPago(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}
