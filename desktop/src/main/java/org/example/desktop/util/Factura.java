package org.example.desktop.util;

import org.example.desktop.util.FacturaItem;

import java.time.LocalDate;
import java.util.List;

public class Factura {
    private int puntoVenta;
    private int tipoComprobante;
    private long cuitEmisor;
    private long cuitReceptor;
    private List<FacturaItem> items;
    private double total;
    private String cae;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimientoCAE;
    private int numero;

    public Factura(int puntoVenta, int tipoComprobante, long cuitEmisor, long cuitReceptor,
                   List<FacturaItem> items, double total, String cae, LocalDate fechaEmision,
                   LocalDate fechaVencimientoCAE, int numero) {
        this.puntoVenta = puntoVenta;
        this.tipoComprobante = tipoComprobante;
        this.cuitEmisor = cuitEmisor;
        this.cuitReceptor = cuitReceptor;
        this.items = items;
        this.total = total;
        this.cae = cae;
        this.fechaEmision = fechaEmision;
        this.fechaVencimientoCAE = fechaVencimientoCAE;
        this.numero = numero;
    }

    // getters
    public int getPuntoVenta() { return puntoVenta; }
    public int getTipoComprobante() { return tipoComprobante; }
    public long getCuitEmisor() { return cuitEmisor; }
    public long getCuitReceptor() { return cuitReceptor; }
    public List<FacturaItem> getItems() { return items; }
    public double getTotal() { return total; }
    public String getCae() { return cae; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public LocalDate getFechaVencimientoCAE() { return fechaVencimientoCAE; }
    public int getNumero() { return numero; }
}