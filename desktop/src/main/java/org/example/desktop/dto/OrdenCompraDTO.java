package org.example.desktop.dto;

import java.math.BigDecimal;

public class OrdenCompraDTO {
    public int id;
    public int proveedorId;
    public int medioPagoId;
    public BigDecimal total;
    public long fechaPago;
    public String estado;

    public OrdenCompraDTO(int proveedorId, int medioPagoId, BigDecimal total, long fechaPago, String estado) {
        this.proveedorId = proveedorId;
        this.medioPagoId = medioPagoId;
        this.total = total;
        this.fechaPago = fechaPago;
        this.estado = estado;
    }

    public OrdenCompraDTO(int id, int proveedorId, int medioPagoId, BigDecimal total, long fechaPago, String estado) {
        this.id = id;
        this.proveedorId = proveedorId;
        this.medioPagoId = medioPagoId;
        this.total = total;
        this.fechaPago = fechaPago;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(int proveedorId) {
        this.proveedorId = proveedorId;
    }

    public int getMedioPagoId() {
        return medioPagoId;
    }

    public void setMedioPagoId(int medioPagoId) {
        this.medioPagoId = medioPagoId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public long getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(long fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "OrdenCompraDTO{" +
                "id=" + id +
                ", proveedorId=" + proveedorId +
                ", medioPagoId=" + medioPagoId +
                ", total=" + total +
                ", fechaPago='" + fechaPago + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
