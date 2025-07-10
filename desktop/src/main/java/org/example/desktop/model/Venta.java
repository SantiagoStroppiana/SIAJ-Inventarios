package org.example.desktop.model;


import org.example.desktop.dto.UsuarioDTO;

import java.time.LocalDateTime;



public class Venta {


    private int id;


    private double total;
    private UsuarioDTO usuarioDTO;

    private EstadoVenta estado = EstadoVenta.pendiente;

    public Venta() {

    }

    public enum EstadoVenta {
        pendiente,
        completada,
        cancelada
    }



    private String fechaPago;
    //LocalDateTime fechaPago = LocalDateTime.parse(fechaPago);

    //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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

    public Venta(double total, EstadoVenta estado, String fechaPago, Usuario usuario, MedioPago medioPago) {
        this.total = total;
        this.estado = estado;
        this.fechaPago = fechaPago;
        this.usuario = usuario;
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

    public Venta(int id, double total, EstadoVenta estado, String fechaPago, UsuarioDTO usuarioDTO, MedioPago medioPago) {
        this.id = id;
        this.total = total;
        this.estado = estado;
        this.fechaPago = fechaPago;
        this.usuarioDTO = usuarioDTO;
        this.medioPago = medioPago;
    }

    public UsuarioDTO getUsuarioDTO() {
        return usuarioDTO;
    }

    public void setUsuarioDTO(UsuarioDTO usuarioDTO) {
        this.usuarioDTO = usuarioDTO;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", total=" + total +
                ", estado=" + estado +
                ", fechaPago='" + fechaPago + '\'' +
                ", medioPago=" + medioPago +
                '}';
    }
}
