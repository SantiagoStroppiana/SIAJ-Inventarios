package siaj.inventarios.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsuarioRolDTO {

    private int id;
    @JsonProperty("nombreRol")
    private String nuevoRol;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNuevoRol() {
        return nuevoRol;
    }

    public void setNuevoRol(String nuevoRol) {
        this.nuevoRol = nuevoRol;
    }
}
