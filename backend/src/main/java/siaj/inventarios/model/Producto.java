package siaj.inventarios.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name= "nombre", nullable=false, length=60)
    private String nombre;
    @Column(name = "precio", nullable=false, precision = 10, scale = 2)
    private BigDecimal precio;
    @Column(name = "sku", nullable=false, length=100, unique = true)
    private String sku;
    @Column(name = "activo", nullable=false)
    private boolean activo;
    @Column(name = "img", nullable=false, length=255)
    private String img;
    @Column(name = "fecha_alta", nullable=false)
    private Date fecha_alta;
    @Column(name = "stock", nullable=false)
    private int stock;
    @Column(name = "stock_minimo", nullable=false)
    private int stock_minimo;

    @JsonProperty("proveedor_id")
    @ManyToOne
    @JoinColumn(name = "proveedores_id", nullable=false)
    private Proveedor proveedorid;

    public Producto(int id, String nombre, BigDecimal precio, String sku, boolean activo, String img, Date fecha_alta, int stock, int stock_minimo,Proveedor proveedorid) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.sku = sku;
        this.activo = activo;
        this.img = img;
        this.fecha_alta = fecha_alta;
        this.stock = stock;
        this.stock_minimo = stock_minimo;
        this.proveedorid = proveedorid;
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

    public Date getFecha_alta() {
        return fecha_alta;
    }

    public void setFecha_alta(Date fecha_alta) {
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
        return proveedorid;
    }

    public void setProveedorid(Proveedor proveedorid) {
        this.proveedorid = proveedorid;
    }


    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", sku='" + sku + '\'' +
                ", proveedor=" + (proveedorid != null ? proveedorid.getRazonSocial() : "null") +
                '}';
    }
}
