package org.example.desktop.util;

public class Empresa {
    private String razonSocial;
    private String tipoDocumento;
    private String numeroDocumento;
    private String descripcionActividadPrincipal;
    private String domicilioFiscal;

    // Getters y setters

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getDescripcionActividadPrincipal() {
        return descripcionActividadPrincipal;
    }

    public void setDescripcionActividadPrincipal(String descripcionActividadPrincipal) {
        this.descripcionActividadPrincipal = descripcionActividadPrincipal;
    }

    public String getDomicilioFiscal() {
        return domicilioFiscal;
    }

    public void setDomicilioFiscal(String domicilioFiscal) {
        this.domicilioFiscal = domicilioFiscal;
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "razonSocial='" + razonSocial + '\'' +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", descripcionActividadPrincipal='" + descripcionActividadPrincipal + '\'' +
                ", domicilioFiscal='" + domicilioFiscal + '\'' +
                '}';
    }
}
