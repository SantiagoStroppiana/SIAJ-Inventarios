package org.example.desktop.util;

public class FacturaItem {
    private String descripcion;
    private int cantidad;
    private double precioUnitario;

    public FacturaItem(String descripcion, int cantidad, double precioUnitario) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }
    // getters
    public String getDescripcion() { return descripcion; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return cantidad * precioUnitario; }
}
