package org.example.desktop.model;



import java.math.BigDecimal;


public class DetalleVenta {


    private int id;


    private Venta venta;


    private int cantidad;


    private double precioUnitario;


    private Producto producto;


    public DetalleVenta() {
    }

    public DetalleVenta(Venta venta, int cantidad, double precioUnitario, Producto producto) {
        this.venta = venta;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.producto = producto;
    }


    public int getId() {
        return id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}

