package siaj.inventarios.dto;

public class MensajesResultados {

    private boolean exito;
    private String mensaje;

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
