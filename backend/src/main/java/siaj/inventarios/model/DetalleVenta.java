package siaj.inventarios.model;


    import jakarta.persistence.*;
import java.math.BigDecimal;

    @Entity
    @Table(name = "detalle_venta")
    public class DetalleVenta {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @ManyToOne
        @JoinColumn(name = "ventas_id", nullable = false)
        private Venta venta;

        @Column(nullable = false)
        private int cantidad;

        @Column(name = "precio_unitario", nullable = false)
        private BigDecimal precioUnitario;

        @ManyToOne
        @JoinColumn(name = "productos_id", nullable = false)
        private Producto producto;

        
        public DetalleVenta() {
        }

        public DetalleVenta(Venta venta, int cantidad, BigDecimal precioUnitario, Producto producto) {
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

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public Producto getProducto() {
            return producto;
        }

        public void setProducto(Producto producto) {
            this.producto = producto;
        }
    }

