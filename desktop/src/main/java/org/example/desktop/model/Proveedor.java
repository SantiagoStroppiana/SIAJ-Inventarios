package org.example.desktop.model;

public class Proveedor {

    private int id;
    private String razonSocial;
    private String email;
    private String telefono;
    private String direccion;
    private String cuit;
    private boolean activo;
    private long fecha_alta;



    public Proveedor() {}

    public Proveedor(int id, String razonSocial, String email, String telefono, String direccion, String cuit,boolean activo, long fecha_alta) {
        this.id = id;
        this.razonSocial = razonSocial;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.cuit = cuit;
        this.activo = activo;
        this.fecha_alta = fecha_alta;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getRazonSocial() {
        return razonSocial;
    }
    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public long getFecha_alta() {
        return fecha_alta;
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + id +
                ", razonSocial='" + razonSocial + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", activo=" + activo +
                ", fecha_alta=" + fecha_alta +
                '}';
    }

    public Proveedor(int id, String razonSocial, String email, String telefono, String direccion, boolean activo) {
        this.id = id;
        this.razonSocial = razonSocial;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = activo;
    }

    public void setFecha_alta(long fecha_alta) {
        this.fecha_alta = fecha_alta;
    }
    public boolean getEstado() {
        return isActivo();
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public Proveedor(int id) {
        this.id = id;
    }
}
