package siaj.inventarios.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name= "nombre", nullable=false, length=60)
    private String nombre;
    @Column(name = "apellido", nullable=false, length=60)
    private String apellido;
    @Column(name = "email", nullable=false, length=70, unique = true)
    @Email(message = "Formato de email inválido")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;
    @Column(name = "password", nullable=false, length=80)
    private String password;

    @ManyToOne
    @JoinColumn(name = "roles_id")
    private Rol rolId;

    public Usuario() {}

    public Usuario(String nombre, String apellido, String email, Rol rolId) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rolId = rolId;
    }

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
   }

   public String getPassword() {
        return password;
   }

   public void setPassword(String password) {
        this.password = password;
   }

   public Rol getIdRol() {
        return rolId;
   }

   public void setIdRol(Rol rolId) {
        this.rolId = rolId;
   }

}
