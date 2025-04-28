package org.example.desktop.model;

public class MensajesResultados {

    private boolean exito;
    private String mensaje;

    public MensajesResultados() {}

    public MensajesResultados(boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
    }

    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }
}
