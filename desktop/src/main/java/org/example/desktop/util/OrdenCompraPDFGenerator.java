package org.example.desktop.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.example.desktop.controller.OrdenCompraController;
import org.example.desktop.model.OrdenCompra;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.example.desktop.model.OrdenCompra;
import org.example.desktop.model.Producto;

import java.awt.Color;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrdenCompraPDFGenerator {

    public static void generarPDF(OrdenCompra orden, List<OrdenCompraController.ItemOrden> items, String rutaSalida,String comentario) throws Exception {
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));
        doc.open();

        // Colores del sistema
        Color verde = new Color(76, 175, 80);
        Color grisOscuro = new Color(45, 45, 45);
        Color grisClaro = new Color(248, 248, 248);
        Color negro = new Color(33, 33, 33);

        // Encabezado
        PdfPTable encabezado = new PdfPTable(1);
        encabezado.setWidthPercentage(100);
        PdfPCell celdaEnc = new PdfPCell(new Phrase("SIAJ INVENTARIOS - ORDEN DE COMPRA",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Font.BOLD, Color.WHITE)));
        celdaEnc.setBackgroundColor(grisOscuro);
        celdaEnc.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaEnc.setPadding(20);
        celdaEnc.setBorder(Rectangle.NO_BORDER);
        encabezado.addCell(celdaEnc);
        doc.add(encabezado);

        doc.add(new Paragraph(" "));

        // Datos de la orden
        PdfPTable datos = new PdfPTable(2);
        datos.setWidthPercentage(100);
        datos.setWidths(new float[]{1, 1});

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, verde);
        Font fontContenido = FontFactory.getFont(FontFactory.HELVETICA, 10, negro);

        // Columna izquierda
        PdfPCell celdaIzq = new PdfPCell();
        celdaIzq.setBackgroundColor(grisClaro);
        celdaIzq.setBorder(Rectangle.NO_BORDER);
        celdaIzq.setPadding(15);

        celdaIzq.addElement(parrafoConEtiqueta("N° Orden: ", String.valueOf(orden.getId()), fontTitulo, fontContenido));
        celdaIzq.addElement(new Paragraph(" "));
        celdaIzq.addElement(parrafoConEtiqueta("Fecha creación: ", orden.getFechaPago().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), fontTitulo, fontContenido));
        celdaIzq.addElement(new Paragraph(" "));
        celdaIzq.addElement(parrafoConEtiqueta("Proveedor: ", orden.getProveedor().getRazonSocial(), fontTitulo, fontContenido));

        // Columna derecha
        PdfPCell celdaDer = new PdfPCell();
        celdaDer.setBackgroundColor(grisClaro);
        celdaDer.setBorder(Rectangle.NO_BORDER);
        celdaDer.setPadding(15);

        celdaDer.addElement(parrafoConEtiqueta("Medio de pago: ", orden.getMedioPago().getTipo(), fontTitulo, fontContenido));
        celdaDer.addElement(new Paragraph(" "));
        celdaDer.addElement(parrafoConEtiqueta("Estado: ", orden.getEstado().toString().toUpperCase(), fontTitulo, fontContenido));

        datos.addCell(celdaIzq);
        datos.addCell(celdaDer);
        doc.add(datos);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));

        // Título
        Paragraph tituloTabla = new Paragraph("📦 DETALLE DE PRODUCTOS",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, verde));
        tituloTabla.setSpacingBefore(10);
        doc.add(tituloTabla);
        doc.add(new Paragraph(" "));

        // Tabla de productos
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{4, 2, 2, 2});

        Font fuenteHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);

        String[] headers = {"Producto", "Cantidad", "P. Unitario", "Subtotal"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fuenteHeader));
            cell.setBackgroundColor(grisOscuro);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10);
            tabla.addCell(cell);
        }

        Font fuenteFila = FontFactory.getFont(FontFactory.HELVETICA, 10, negro);
        boolean alternar = false;
        for (OrdenCompraController.ItemOrden item : items) {
            Producto prod = item.getProducto();
            double precioUnitario = prod.getPrecio().doubleValue();
            double subtotal = precioUnitario * item.getCantidad();
            Color bg = alternar ? new Color(249, 249, 249) : Color.WHITE;

            tabla.addCell(celdaTexto(prod.getNombre(), fuenteFila, bg, Element.ALIGN_LEFT));
            tabla.addCell(celdaTexto(String.valueOf(item.getCantidad()), fuenteFila, bg, Element.ALIGN_CENTER));
            tabla.addCell(celdaTexto(String.format("$ %.2f", precioUnitario), fuenteFila, bg, Element.ALIGN_CENTER));
            tabla.addCell(celdaTexto(String.format("$ %.2f", subtotal), fuenteFila, bg, Element.ALIGN_CENTER));

            alternar = !alternar;
        }

        doc.add(tabla);
        doc.add(new Paragraph(" "));

        // Total
        PdfPTable tablaTotal = new PdfPTable(1);
        tablaTotal.setWidthPercentage(100);
        PdfPCell celdaTotal = new PdfPCell(new Phrase("💰 TOTAL: $" + orden.getTotal().setScale(2),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.WHITE)));
        celdaTotal.setBackgroundColor(verde);
        celdaTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaTotal.setPadding(15);
        celdaTotal.setBorder(Rectangle.NO_BORDER);
        tablaTotal.addCell(celdaTotal);
        doc.add(tablaTotal);

        // Pie
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(" "));
        Paragraph gracias = new Paragraph("✅ ¡Gracias por su gestión!",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, verde));
        gracias.setAlignment(Element.ALIGN_CENTER);
        doc.add(gracias);

        Paragraph pie = new Paragraph("SIAJ INVENTARIOS - Sistema de Gestión",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, negro));
        pie.setAlignment(Element.ALIGN_CENTER);
        doc.add(pie);


// Comentario estilizado (si existe)
        if (comentario != null && !comentario.trim().isEmpty()) {
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            PdfPTable tablaComentario = new PdfPTable(1);
            tablaComentario.setWidthPercentage(100);

            Color fondoComentario = new Color(230, 245, 255); // celeste muy suave
            Color bordeComentario = new Color(180, 220, 240); // azul grisáceo claro

            PdfPCell celdaComentario = new PdfPCell();
            celdaComentario.setBackgroundColor(fondoComentario);
            celdaComentario.setBorderColor(bordeComentario);
            celdaComentario.setPadding(15);
            celdaComentario.setBorderWidth(1.2f);

            Paragraph tituloComentario = new Paragraph("💬 Comentario del Responsable",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, verde));
            tituloComentario.setSpacingAfter(10);

            Paragraph cuerpoComentario = new Paragraph(comentario,
                    FontFactory.getFont(FontFactory.HELVETICA, 11, negro));
            cuerpoComentario.setLeading(16); // espacio entre líneas
            cuerpoComentario.setFirstLineIndent(10); // pequeña sangría

            celdaComentario.addElement(tituloComentario);
            celdaComentario.addElement(cuerpoComentario);
            tablaComentario.addCell(celdaComentario);

            doc.add(tablaComentario);
        }




        doc.close();
    }

    private static Paragraph parrafoConEtiqueta(String etiqueta, String contenido, Font fuenteTitulo, Font fuenteContenido) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(etiqueta, fuenteTitulo));
        p.add(new Chunk(contenido, fuenteContenido));
        return p;
    }

    private static PdfPCell celdaTexto(String texto, Font fuente, Color bg, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texto, fuente));
        c.setBackgroundColor(bg);
        c.setPadding(6);
        c.setHorizontalAlignment(align);
        return c;
    }
}
