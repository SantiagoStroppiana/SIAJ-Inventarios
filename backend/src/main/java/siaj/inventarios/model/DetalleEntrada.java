package siaj.inventarios.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_entrada")
public class DetalleEntrada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "entrada_id")
    private Entrada entrada;

    @ManyToOne
    @JoinColumn(name = "productos_id", nullable = false)
    private Producto producto;

    private int cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    public DetalleEntrada() {

    }

    // Getters y setters...

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public DetalleEntrada(int id, Entrada entrada, Producto producto, int cantidad, BigDecimal precioUnitario) {
        this.id = id;
        this.entrada = entrada;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }
}
