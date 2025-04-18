package siaj.inventarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name= "nombre", nullable=false, length=60)
    private String nombre;
    @Column(name = "precio", nullable=false, precision = 10, scale = 2)
    private BigDecimal precio;
    @Column(name = "sku", nullable=false, length=100, unique = true)
    private String sku;
    @Column(name = "activo", nullable=false)
    private boolean activo;
    @Column(name = "img", nullable=false, length=255)
    private String img;
    @Column(name = "fecha_alta", nullable=false)
    private Date fecha_alta;
    @Column(name = "stock", nullable=false)
    private int stock;
    @Column(name = "stock_minimo", nullable=false)
    private int stock_minimo;

    /* FOREING KEY
    @ManyToOne
    @JoinColumn(name = "proovedores_id", nullable=false)
    private Proveedor Proveedorid;
*/
    /* FOREIGN KEY INTERMEDIA
    @ManyToMany
    @JoinTable(
            name = "categorias_productos",
            joinColumns = @JoinColumn(name = "productos_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_productos_id")
    )
*/


}
