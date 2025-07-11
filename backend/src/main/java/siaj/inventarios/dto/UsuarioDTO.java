package siaj.inventarios.dto;
public class UsuarioDTO {

    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String nombreRol;

    public UsuarioDTO() {}

    public UsuarioDTO(int id, String nombre, String apellido, String email, String rol){
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.nombreRol = rol;
    }

    public UsuarioDTO(int id) {
        this.id = id;
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

    public String getNombreRol() {
        return nombreRol;
    }
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
}
