package org.example.desktop.dto;

public class UsuarioRolDTO {

    private int idUsuario;
    private String nuevoRol;

    public UsuarioRolDTO() {}

    public String getNuevoRol() {
        return nuevoRol;
    }

    public void setNuevoRol(String nuevoRol) {
        this.nuevoRol = nuevoRol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
