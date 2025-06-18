package siaj.inventarios.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "grupos_categorias")
public class GrupoCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    // Corregimos la relación bidireccional
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference // Maneja la referencia en JSON
    private List<Categoria> categorias = new ArrayList<>();

    // Constructores
    public GrupoCategoria() {}

    public GrupoCategoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
        // Aseguramos la consistencia bidireccional
        if (categorias != null) {
            for (Categoria categoria : categorias) {
                categoria.setGrupo(this);
            }
        }
    }

    // Métodos de utilidad para mantener la consistencia bidireccional
    public void addCategoria(Categoria categoria) {
        if (categoria != null) {
            categorias.add(categoria);
            categoria.setGrupo(this);
        }
    }

    public void removeCategoria(Categoria categoria) {
        if (categoria != null) {
            categorias.remove(categoria);
            categoria.setGrupo(null);
        }
    }

    @Override
    public String toString() {
        return nombre; // Para ComboBox
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GrupoCategoria that = (GrupoCategoria) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}