package siaj.inventarios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medio_pago")
public class MedioPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public MedioPago(int id) {
        this.id = id;
    }

    @Column(nullable = false, unique = true)
    private String tipo;

    public MedioPago() {
    }

    public MedioPago(String tipo) {
        this.tipo = tipo;
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public MedioPago(int id, String tipo) {
        this.id = id;
        this.tipo = tipo;
    }
}
