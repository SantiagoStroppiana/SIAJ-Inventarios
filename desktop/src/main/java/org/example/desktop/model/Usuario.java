package org.example.desktop.model;

public class Usuario {

    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Rol rolId;

    public Usuario() {}

    public Usuario(int id, String nombre, String apellido, String email, String password, Rol rolId) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rolId = rolId;
    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
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
    public Rol getRolId() {
        return rolId;
    }
    public void setRolId(Rol rolId) {
        this.rolId = rolId;
    }
    public String getNombreRol() {
        return rolId != null ? rolId.getNombre() : "Sin Rol";
    }
}
