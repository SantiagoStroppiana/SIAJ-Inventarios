package org.example.desktop.model;


import java.time.LocalDateTime;



public class Venta {


    private int id;


    private double total;


    private EstadoVenta estado = EstadoVenta.pendiente;

    public Venta() {

    }

    public enum EstadoVenta {
        pendiente,
        completada,
        cancelada
    }



    private String fechaPago; //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

//LocalDateTime fechaPago = LocalDateTime.parse(fechaPago);
    private Usuario usuario;


    private MedioPago medioPago;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }

    public Venta(int id, double total, EstadoVenta estado, String fechaPago, Usuario usuario, MedioPago medioPago) {
        this.id = id;
        this.total = total;
        this.estado = estado;
        this.fechaPago = fechaPago;
        this.usuario = usuario;
        this.medioPago = medioPago;
    }
}
