package org.example.desktop.dto;


public class DetalleOrdenCompraDTO {
    private int id;
    private int ordenCompraId;
    private int productoId;
    private int cantidad;
    private double precioUnitario;

    public DetalleOrdenCompraDTO() {
    }

    public DetalleOrdenCompraDTO(int id, int ordenCompraId, int productoId, int cantidad, double precioUnitario) {
        this.id = id;
        this.ordenCompraId = ordenCompraId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public DetalleOrdenCompraDTO(int ordenCompraId, int productoId, int cantidad, double precioUnitario) {
        this.ordenCompraId = ordenCompraId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrdenCompraId() {
        return ordenCompraId;
    }

    public void setOrdenCompraId(int ordenCompraId) {
        this.ordenCompraId = ordenCompraId;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
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
}

