package org.example.desktop.model;

import org.example.desktop.dto.UsuarioDTO;

public class LoginResponse {

    private boolean success;
    private String message;
    private UsuarioDTO usuario;

    public LoginResponse(boolean success, String message, UsuarioDTO usuario) {
        this.success = success;
        this.message = message;
        this.usuario = usuario;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "suceess=" + success +
                ", message='" + message + '\'' +
                ", usuario=" + usuario +
                '}';
    }
}

