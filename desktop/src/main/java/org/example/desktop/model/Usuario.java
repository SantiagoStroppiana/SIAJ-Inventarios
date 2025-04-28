package org.example.desktop.model;

public class Usuario {

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Rol roles_id;

    public Usuario() {}

    public Usuario(String nombre, String apellido, String email, String password, Rol roles_id) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.roles_id = roles_id;
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
    public Rol getRoles_id() {
        return roles_id;
    }
    public void setRoles_id(Rol roles_id) {
        this.roles_id = roles_id;
    }
}
