package org.example.desktop.util;

import org.example.desktop.dto.UsuarioDTO;
import org.example.desktop.model.Usuario;

public class UserSession {

    private static UserSession instance;
    private static UsuarioDTO usuario;

    private UserSession(UsuarioDTO usuario) {
        this.usuario = usuario;
    };

    public static void iniciarSesion(UsuarioDTO usuario) {
        instance = new UserSession(usuario);
    }

    public static UsuarioDTO getUsuarioActual() {
        return instance != null ? instance.usuario : null;
    }

    public static void cerrarSesion() {
        instance = null;
    }

    public static boolean haySesionActiva(){
        return instance != null;
    }


}
