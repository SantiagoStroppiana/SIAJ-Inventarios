package siaj.inventarios.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orden_compra")
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden estado = EstadoOrden.pendiente;

    @ManyToOne
    @JoinColumn(name = "proveedores_id", nullable = false)
    private Proveedor proveedor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "medio_pago_id", nullable = false)
    private MedioPago medioPago;

    @JsonFormat
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    public OrdenCompra() {

    }

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
