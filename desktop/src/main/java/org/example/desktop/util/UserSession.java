package org.example.desktop.util;

import org.example.desktop.model.Usuario;

public class UserSession {

    private static UserSession instance;
    private Usuario usuario;

    private UserSession(Usuario usuario) {
        this.usuario = usuario;
    };

    public static void iniciarSesion(Usuario usuario) {
        if (instance == null) {
            instance = new UserSession(usuario);
        }
    }

    public static Usuario getUsuarioActual() {
        return instance != null ? instance.usuario : null;
    }

    public static void cerrarSesion() {
        instance = null;
    }

    public static boolean haySesionActiva(){
        return instance != null;
    }


}
