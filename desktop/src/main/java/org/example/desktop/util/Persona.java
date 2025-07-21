package org.example.desktop.util;

public class Persona {
    private String idPersona;
    private String nombre;
    private String apellido;
    private String tipoDocumento;
    private String numeroDocumento;
    private String descripcionActividadPrincipal;
    private String fechaNacimiento;
    private String domicilioReal;     // nueva
    private String domicilioFiscal;   // nueva
    // Agregá más campos según necesites

    public String getDomicilioReal() { return domicilioReal; }
    public void setDomicilioReal(String domicilioReal) { this.domicilioReal = domicilioReal; }

    public String getDomicilioFiscal() { return domicilioFiscal; }
    public void setDomicilioFiscal(String domicilioFiscal) { this.domicilioFiscal = domicilioFiscal; }

    // Getters y Setters
    public String getIdPersona() { return idPersona; }
    public void setIdPersona(String idPersona) { this.idPersona = idPersona; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getDescripcionActividadPrincipal() { return descripcionActividadPrincipal; }
    public void setDescripcionActividadPrincipal(String descripcionActividadPrincipal) {
        this.descripcionActividadPrincipal = descripcionActividadPrincipal;
    }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    @Override
    public String toString() {
        return "Persona{" +
                "idPersona='" + idPersona + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", descripcionActividadPrincipal='" + descripcionActividadPrincipal + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                ", domicilioReal='" + domicilioReal + '\'' +
                ", domicilioFiscal='" + domicilioFiscal + '\'' +
                '}';
    }
}