package org.example.desktop.model;



import java.math.BigDecimal;


public class Producto {


    private int id;
    private String nombre;
    private BigDecimal precio;
    private String sku;
    private boolean activo;
    private String img;
    private long fecha_alta;
    private int stock;
    private int stock_minimo;
    private Proveedor  proveedor_id;


    public Producto(int id, String nombre, BigDecimal precio, String sku, boolean activo, String img, long fecha_alta, int stock, int stock_minimo,Proveedor proveedor_id) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.sku = sku;
        this.activo = activo;
        this.img = img;
        this.fecha_alta = fecha_alta;
        this.stock = stock;
        this.stock_minimo = stock_minimo;
        this.proveedor_id = proveedor_id;
    }

    public Producto() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public long getFecha_alta() {
        return fecha_alta;
    }

    public void setFecha_alta(long fecha_alta) {
        this.fecha_alta = fecha_alta;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStock_minimo() {
        return stock_minimo;
    }

    public void setStock_minimo(int stock_minimo) {
        this.stock_minimo = stock_minimo;
    }

    public Proveedor getProveedorid() {
        return proveedor_id;
    }

    public void setProveedorid(Proveedor proveedor_id) {
        this.proveedor_id = proveedor_id;
    }

    public String getProveedor() {
        return (getProveedorid() != null && getProveedorid().getRazonSocial() != null)
                ? getProveedorid().getRazonSocial()
                : "Sin proveedor";
    }


    public boolean getEstado() {
        return isActivo();
    }


}
