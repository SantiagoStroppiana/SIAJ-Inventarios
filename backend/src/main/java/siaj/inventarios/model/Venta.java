package siaj.inventarios.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import siaj.inventarios.dto.UsuarioDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;



    @Entity
    @Table(name="ventas")
    public class Venta {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(nullable = false, precision = 10, scale = 2)
        private BigDecimal total;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private EstadoVenta estado = EstadoVenta.pendiente;

        public Venta() {

        }

        public enum EstadoVenta {
            pendiente,
            completada,
            cancelada
        }


        @Column(name = "fecha_pago")
        private LocalDateTime fechaPago;

        @ManyToOne
        @JoinColumn(name = "usuarios_id", nullable = false)
        private Usuario usuario;

        @ManyToOne
        @JoinColumn(name = "medio_pago_id", nullable = false)
        private MedioPago medioPago;



        @Transient
        @JsonProperty("usuarioDTO")
        private UsuarioDTO usuarioDTO;

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

        public EstadoVenta getEstado() {
            return estado;
        }

        public void setEstado(EstadoVenta estado) {
            this.estado = estado;
        }

        public LocalDateTime getFechaPago() {
            return fechaPago;
        }

        public void setFechaPago(LocalDateTime fechaPago) {
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

        public Venta(int id, BigDecimal total, EstadoVenta estado, LocalDateTime fechaPago, Usuario usuario, MedioPago medioPago) {
            this.id = id;
            this.total = total;
            this.estado = estado;
            this.fechaPago = fechaPago;
            this.usuario = usuario;
            this.medioPago = medioPago;
        }

        public Venta(int id, BigDecimal total, EstadoVenta estado, LocalDateTime fechaPago, UsuarioDTO usuarioDTO, MedioPago medioPago) {
            this.id = id;
            this.total = total;
            this.estado = estado;
            this.fechaPago = fechaPago;
            this.usuarioDTO = usuarioDTO;
            this.medioPago = medioPago;
        }


        @Override
        public String toString() {
            return "Venta{" +
                    "id=" + id +
                    ", total=" + total +
                    ", estado=" + estado +
                    ", fechaPago=" + fechaPago +
                    ", usuario=" + usuario +
                    ", medioPago=" + medioPago +
                    '}';
        }
    }
