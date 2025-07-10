package org.example.desktop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class OrdenCompra {


    private int id;


    private EstadoOrden estado = EstadoOrden.pendiente;

    private Proveedor proveedor;

    private BigDecimal total;

    private MedioPago medioPago;

    private LocalDateTime fechaPago;

    // --- Getters y Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    // --- Enum interno para estado ---
    public enum EstadoOrden {
        pendiente,
        parcial,
        completada,
        cancelada
    }

    public OrdenCompra(int id, EstadoOrden estado, Proveedor proveedor, BigDecimal total, MedioPago medioPago, LocalDateTime fechaPago) {
        this.id = id;
        this.estado = estado;
        this.proveedor = proveedor;
        this.total = total;
        this.medioPago = medioPago;
        this.fechaPago = fechaPago;
    }

    public OrdenCompra(EstadoOrden estado, Proveedor proveedor, BigDecimal total, MedioPago medioPago, LocalDateTime fechaPago) {
        this.estado = estado;
        this.proveedor = proveedor;
        this.total = total;
        this.medioPago = medioPago;
        this.fechaPago = fechaPago;
    }
}

