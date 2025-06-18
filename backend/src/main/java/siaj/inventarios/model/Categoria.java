package siaj.inventarios.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    // Eliminamos el campo grupoId redundante
    // @Column(name = "grupo_id", nullable = false)
    // private int grupoId;

    // Corregimos el mapeo de la relación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = true)
    @JsonBackReference // Evita la referencia circular en JSON
    private GrupoCategoria grupo;

    @Transient // Para que Hibernate no trate de mapearlo a la BD
    @JsonProperty("grupoId")
    private Integer grupoId;
    @JsonInclude(JsonInclude.Include.NON_NULL) // No incluir si es null


    public Integer getGrupoId() {
        if (grupo != null) {
            return grupo.getId();
        }
        return grupoId; // Puede ser null
    }

    public void setGrupoId(Integer grupoId) {
        this.grupoId = grupoId;
    }



    @JsonBackReference
    @ManyToMany(mappedBy = "categorias", fetch = FetchType.LAZY)
    @JsonIgnore // Evita que se serialice esta colección
    private List<Producto> productos;

    // Constructores
    public Categoria() {}

    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Categoria(String nombre, String descripcion, GrupoCategoria grupo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.grupo = grupo;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public GrupoCategoria getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoCategoria grupo) {
        this.grupo = grupo;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return nombre; // Para ComboBox
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Categoria categoria = (Categoria) obj;
        return id == categoria.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}