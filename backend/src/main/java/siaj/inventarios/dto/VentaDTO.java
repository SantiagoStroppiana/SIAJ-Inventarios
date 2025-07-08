package siaj.inventarios.dto;


import siaj.inventarios.model.MedioPago;

import siaj.inventarios.model.Venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentaDTO {

        private int id;
        private BigDecimal total;
        private String estado;
        private String fechaPago;
        private UsuarioDTO usuarioDTO;
        private MedioPago medioPago;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public UsuarioDTO getUsuarioDTO() {
        return usuarioDTO;
    }

    public void setUsuarioDTO(UsuarioDTO usuarioDTO) {
        this.usuarioDTO = usuarioDTO;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }

/*public VentaDTO(int id, BigDecimal total, String estado, String fechaPago, String usuarioNombre, String medioPagoNombre) {
        this.id = id;
        this.total = total;
        this.estado = estado;
        this.fechaPago = fechaPago;
        this.usuarioNombre = usuarioNombre;
        this.medioPagoNombre = medioPagoNombre;
    }*/

    public VentaDTO(int id, BigDecimal total, String estado, String fechaPago, UsuarioDTO usuarioDTO, MedioPago medioPago) {
        this.id = id;
        this.total = total;
        this.estado = estado;
        this.fechaPago = fechaPago;
        this.usuarioDTO = usuarioDTO;
        this.medioPago = medioPago;
    }

    public VentaDTO() {

    }
}

