package siaj.inventarios.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

@Entity
@Table(name="proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "razon_social", nullable=false, length = 60)
    private String razonSocial;
    @Column (name = "email", nullable = false, length = 70, unique = true)
    @Email(message = "Formato de Email inválido")
    @NotBlank(message ="El Email no puede estar Vacío")
    private String email;
    @Column(name = "telefono", nullable = false, length = 10)
    private String telefono;
    @Column(name = "direccion", nullable = false, length = 50)
    private String direccion;
    @Column(name = "cuit", nullable = false, length = 50)
    private String cuit;
    @Column(name = "activo", nullable = false)
    private boolean activo;
    @Column(name = "fecha_alta", nullable = false)
    private Date fecha_alta;

    public Proveedor() {}

    public Proveedor(String razonSocial, String email, String telefono, String direccion, String cuit,boolean activo, Date fecha_alta) {
        this.razonSocial = razonSocial;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.cuit = cuit;
        this.activo = activo;
    }
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getRazonSocial() { return razonSocial; }

    public void setRazonSocial(String razonSocial) {this.razonSocial = razonSocial; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }

    public void setDireccion(String direccion) { this.direccion = direccion; }

    public boolean isActivo() { return activo; }

    public void setActivo(boolean activo) { this.activo = activo; }

    public Date getFecha_alta() { return fecha_alta; }

    public void setFecha_alta(Date fecha_alta) {this.fecha_alta = fecha_alta; }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }
}

