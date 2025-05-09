package siaj.inventarios.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "categorias_productos")
public class CategoriaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "productos_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
