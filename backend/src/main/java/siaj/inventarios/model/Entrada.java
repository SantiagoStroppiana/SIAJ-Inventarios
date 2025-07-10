package siaj.inventarios.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrada")
public class Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "orden_compra_id", referencedColumnName = "id")
    private OrdenCompra ordenCompra;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    public Entrada(int id, OrdenCompra ordenCompra, LocalDateTime fecha) {
        this.id = id;
        this.ordenCompra = ordenCompra;
        this.fecha = fecha;
    }

    public Entrada() {
    }
// --- Getters y Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OrdenCompra getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(OrdenCompra ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}