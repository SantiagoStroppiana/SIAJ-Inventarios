package org.example.desktop.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.example.desktop.model.DetalleVenta;
import org.example.desktop.model.Producto;
import org.example.desktop.model.Venta;

import java.awt.Color;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentaPDFGenerator {

    public void generarPDF(Venta venta, List<DetalleVenta> detalles, String rutaSalida) throws Exception {
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));
        doc.open();

        // Definir colores (basados en tu sistema)
        Color colorPrimario = new Color(76, 175, 80);     // Verde como tu sistema
        Color colorSecundario = new Color(45, 45, 45);    // Gris oscuro como tu sistema
        Color colorFondo = new Color(248, 248, 248);      // Gris muy claro para contraste
        Color colorTotal = new Color(76, 175, 80);        // Verde para total
        Color colorTexto = new Color(33, 33, 33);         // Negro para texto

        // Encabezado con diseño como tu sistema
        PdfPTable encabezado = new PdfPTable(1);
        encabezado.setWidthPercentage(100);
        PdfPCell celdaEncabezado = new PdfPCell(new Phrase("SIAJ INVENTARIOS - FACTURA A",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Font.BOLD, Color.WHITE)));
        celdaEncabezado.setBackgroundColor(colorSecundario);
        celdaEncabezado.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaEncabezado.setPadding(20);
        celdaEncabezado.setBorder(Rectangle.NO_BORDER);
        encabezado.addCell(celdaEncabezado);
        doc.add(encabezado);

        doc.add(new Paragraph(" "));

        // Sección de datos de la venta con diseño mejorado
        PdfPTable datosVenta = new PdfPTable(2);
        datosVenta.setWidthPercentage(100);
        datosVenta.setWidths(new float[]{1, 1});

        // Columna izquierda
        PdfPCell celdaIzq = new PdfPCell();
        celdaIzq.setBorder(Rectangle.NO_BORDER);
        celdaIzq.setBackgroundColor(colorFondo);
        celdaIzq.setPadding(15);

        Font fonteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, colorPrimario);
        Font fonteContenido = FontFactory.getFont(FontFactory.HELVETICA, 10, colorTexto);

        Paragraph pNumero = new Paragraph();
        pNumero.add(new Chunk("Número de Venta: ", fonteTitulo));
        pNumero.add(new Chunk(String.valueOf(venta.getId()), fonteContenido));

        Paragraph pFecha = new Paragraph();
        pFecha.add(new Chunk("Fecha de pago: ", fonteTitulo));
        //pFecha.add(new Chunk(String.valueOf(venta.getFechaPago()), fonteContenido));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime fecha = LocalDateTime.parse(venta.getFechaPago());
        String fechaFormateada = fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        pFecha.add(new Chunk(fechaFormateada, fonteContenido));


        Paragraph pMedioPago = new Paragraph();
        pMedioPago.add(new Chunk("Medio de pago: ", fonteTitulo));
        pMedioPago.add(new Chunk(venta.getMedioPago() != null ? venta.getMedioPago().getTipo() : "No especificado", fonteContenido));

        celdaIzq.addElement(pNumero);
        celdaIzq.addElement(new Paragraph(" "));
        celdaIzq.addElement(pFecha);
        celdaIzq.addElement(new Paragraph(" "));
        celdaIzq.addElement(pMedioPago);

        // Columna derecha
        PdfPCell celdaDer = new PdfPCell();
        celdaDer.setBorder(Rectangle.NO_BORDER);
        celdaDer.setBackgroundColor(colorFondo);
        celdaDer.setPadding(15);

        Paragraph pEstado = new Paragraph();
        pEstado.add(new Chunk("Estado: ", fonteTitulo));
        pEstado.add(new Chunk(String.valueOf(venta.getEstado()), fonteContenido));

        celdaDer.addElement(pEstado);

        datosVenta.addCell(celdaIzq);
        datosVenta.addCell(celdaDer);
        doc.add(datosVenta);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));

        // Título de productos con estilo de tu sistema
        Paragraph tituloProductos = new Paragraph("📦 DETALLE DE PRODUCTOS",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, colorPrimario));
        tituloProductos.setAlignment(Element.ALIGN_LEFT);
        tituloProductos.setSpacingBefore(10);
        doc.add(tituloProductos);
        doc.add(new Paragraph(" "));

        // Tabla de productos con diseño mejorado
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3, 2, 1, 2, 2});

        // Encabezados de la tabla con colores de tu sistema
        Font fonteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);

        PdfPCell[] encabezados = {
                new PdfPCell(new Phrase("Producto", fonteEncabezado)),
                new PdfPCell(new Phrase("SKU", fonteEncabezado)),
                new PdfPCell(new Phrase("Cant", fonteEncabezado)),
                new PdfPCell(new Phrase("P. Unitario", fonteEncabezado)),
                new PdfPCell(new Phrase("Subtotal", fonteEncabezado))
        };

        for (PdfPCell celda : encabezados) {
            celda.setBackgroundColor(colorSecundario);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setPadding(10);
            tabla.addCell(celda);
        }

        // Filas de productos con colores alternados
        Font fonteTabla = FontFactory.getFont(FontFactory.HELVETICA, 10, colorTexto);
        boolean filaAlterna = false;

        for (DetalleVenta d : detalles) {
            Producto p = d.getProducto();
            Color colorFila = filaAlterna ? new Color(249, 249, 249) : Color.WHITE;

            PdfPCell[] celdas = {
                    new PdfPCell(new Phrase(p.getNombre(), fonteTabla)),
                    new PdfPCell(new Phrase(p.getSku(), fonteTabla)),
                    new PdfPCell(new Phrase(String.valueOf(d.getCantidad()), fonteTabla)),
                    new PdfPCell(new Phrase(String.format("$ %.2f", d.getPrecioUnitario()), fonteTabla)),
                    new PdfPCell(new Phrase(String.format("$ %.2f", d.getCantidad() * d.getPrecioUnitario()), fonteTabla))
            };

            for (int i = 0; i < celdas.length; i++) {
                celdas[i].setBackgroundColor(colorFila);
                celdas[i].setPadding(6);
                if (i == 1 || i == 2 || i == 3 || i == 4) { // Centrar SKU, Cantidad y precios
                    celdas[i].setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                tabla.addCell(celdas[i]);
            }

            filaAlterna = !filaAlterna;
        }

        doc.add(tabla);
        doc.add(new Paragraph(" "));

        // Total con diseño verde como tu sistema
        PdfPTable tablaTotal = new PdfPTable(1);
        tablaTotal.setWidthPercentage(100);
        tablaTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell celdaTotal = new PdfPCell(new Phrase("💰 TOTAL: $" + String.format("%.2f", venta.getTotal()),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.WHITE)));
        celdaTotal.setBackgroundColor(colorTotal);
        celdaTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaTotal.setPadding(15);
        celdaTotal.setBorder(Rectangle.NO_BORDER);

        tablaTotal.addCell(celdaTotal);
        doc.add(tablaTotal);

        // Pie de página con estilo de tu sistema
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));
        Paragraph pieGracias = new Paragraph("✅ ¡Gracias por su compra!",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, colorPrimario));
        pieGracias.setAlignment(Element.ALIGN_CENTER);
        doc.add(pieGracias);

        Paragraph pieSistema = new Paragraph("SIAJ INVENTARIOS - Sistema de Gestión",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, colorTexto));
        pieSistema.setAlignment(Element.ALIGN_CENTER);
        doc.add(pieSistema);

        doc.close();
    }
}