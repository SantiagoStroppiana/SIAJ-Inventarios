package org.example.desktop.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.example.desktop.util.Factura;
import org.example.desktop.util.FacturaItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class GeneradorPDF {

    public String generarPDF(Factura factura) throws Exception {
        Document doc = new Document(PageSize.A4);
        doc.setMargins(20, 20, 20, 20);

        String rutaDocumentos = System.getProperty("user.home") + File.separator + "Documents";
        File carpetaFacturas = new File(rutaDocumentos, "Facturas");
        if (!carpetaFacturas.exists()) {
            carpetaFacturas.mkdirs();
        }

        String nombrePdf = "FacturaC " + factura.getNumero() + ".pdf";
        String rutaSalida = carpetaFacturas.getAbsolutePath() + File.separator + nombrePdf;
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));

        doc.open();

        // Obtener el canvas para posicionamiento absoluto
        PdfContentByte canvas = writer.getDirectContent();

        // Colores más formales y sobrios
        Color colorPrimario = new Color(0, 51, 102);        // Azul oscuro AFIP
        Color colorSecundario = new Color(70, 70, 70);      // Gris profesional
        Color colorBorde = new Color(180, 180, 180);        // Gris claro para bordes
        Color colorFondo = new Color(250, 250, 250);        // Fondo muy sutil
        Color colorTexto = Color.BLACK;                     // Negro para texto
        Color colorCAE = new Color(0, 102, 51);            // Verde oscuro para CAE

        // ENCABEZADO PRINCIPAL CON ESTRUCTURA AFIP
        PdfPTable encabezadoPrincipal = new PdfPTable(3);
        encabezadoPrincipal.setWidthPercentage(100);
        encabezadoPrincipal.setWidths(new float[]{2.5f, 1f, 2.5f});

        // Columna izquierda - Datos del emisor
        PdfPCell celdaEmisor = new PdfPCell();
        celdaEmisor.setBorder(Rectangle.BOX);
        celdaEmisor.setBorderColor(colorBorde);
        celdaEmisor.setPadding(10);

        Font fonteEmisorTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, colorPrimario);
        Font fonteEmisorTexto = FontFactory.getFont(FontFactory.HELVETICA, 9, colorTexto);

        Paragraph pRazonSocial = new Paragraph("SIAJ INVENTARIOS S.A.", fonteEmisorTitulo);
        Paragraph pDomicilio = new Paragraph("Domicilio: Av. Corrientes 1234", fonteEmisorTexto);
        Paragraph pLocalidad = new Paragraph("Localidad: Buenos Aires - CP: 1010", fonteEmisorTexto);
        Paragraph pCondicionIVAEmisor = new Paragraph("Condición frente al IVA: Responsable Inscripto", fonteEmisorTexto);

        celdaEmisor.addElement(pRazonSocial);
        celdaEmisor.addElement(pDomicilio);
        celdaEmisor.addElement(pLocalidad);
        celdaEmisor.addElement(pCondicionIVAEmisor);

        // Columna central - Tipo de comprobante (como AFIP)
        PdfPCell celdaCentral = new PdfPCell();
        celdaCentral.setBorder(Rectangle.BOX);
        celdaCentral.setBorderColor(colorBorde);
        celdaCentral.setBackgroundColor(colorFondo);
        celdaCentral.setPadding(5);
        celdaCentral.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Letra grande del tipo de factura
        Font fonteTipoGrande = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 48, colorPrimario);
        Paragraph pTipoLetra = new Paragraph(obtenerLetraTipoComprobante(factura.getTipoComprobante()), fonteTipoGrande);
        pTipoLetra.setAlignment(Element.ALIGN_CENTER);

        Font fonteTipoDesc = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, colorTexto);
        Paragraph pTipoDesc = new Paragraph(obtenerDescripcionTipoComprobante(factura.getTipoComprobante()), fonteTipoDesc);
        pTipoDesc.setAlignment(Element.ALIGN_CENTER);

        celdaCentral.addElement(pTipoLetra);
        celdaCentral.addElement(pTipoDesc);

        // Columna derecha - Datos de la factura
        PdfPCell celdaFactura = new PdfPCell();
        celdaFactura.setBorder(Rectangle.BOX);
        celdaFactura.setBorderColor(colorBorde);
        celdaFactura.setPadding(10);

        Font fonteDatosTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, colorTexto);
        Font fonteDatosTexto = FontFactory.getFont(FontFactory.HELVETICA, 9, colorTexto);

        Paragraph pPtoVta = new Paragraph();
        pPtoVta.add(new Chunk("Punto de Venta: ", fonteDatosTitulo));
        pPtoVta.add(new Chunk(String.format("%04d", factura.getPuntoVenta()), fonteDatosTexto));

        Paragraph pCompNro = new Paragraph();
        pCompNro.add(new Chunk("Comp. Nro: ", fonteDatosTitulo));
        pCompNro.add(new Chunk(String.format("%08d", factura.getNumero()), fonteDatosTexto));

        Paragraph pFechaEmision = new Paragraph();
        pFechaEmision.add(new Chunk("Fecha de Emisión: ", fonteDatosTitulo));
        pFechaEmision.add(new Chunk(formatearFecha(factura.getFechaEmision().toString()), fonteDatosTexto));

        Paragraph pCUITEmisor = new Paragraph();
        pCUITEmisor.add(new Chunk("CUIT: ", fonteDatosTitulo));
        pCUITEmisor.add(new Chunk(formatearCUIT(factura.getCuitEmisor()), fonteDatosTexto));

        Paragraph pInicioAct = new Paragraph();
        pInicioAct.add(new Chunk("Inicio de Actividades: ", fonteDatosTitulo));
        pInicioAct.add(new Chunk("01/01/2020", fonteDatosTexto));

        celdaFactura.addElement(pPtoVta);
        celdaFactura.addElement(pCompNro);
        celdaFactura.addElement(pFechaEmision);
        celdaFactura.addElement(pCUITEmisor);
        celdaFactura.addElement(pInicioAct);

        encabezadoPrincipal.addCell(celdaEmisor);
        encabezadoPrincipal.addCell(celdaCentral);
        encabezadoPrincipal.addCell(celdaFactura);

        doc.add(encabezadoPrincipal);
        doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 8)));

        // **QR CODE PEQUEÑO - Posicionado en zona libre (esquina superior derecha)**
        agregarQRPequeno(canvas, factura, PageSize.A4.getWidth(), PageSize.A4.getHeight());

        // DATOS DEL CLIENTE
        PdfPTable datosCliente = new PdfPTable(1);
        datosCliente.setWidthPercentage(100);

        PdfPCell celdaCliente = new PdfPCell();
        celdaCliente.setBorder(Rectangle.BOX);
        celdaCliente.setBorderColor(colorBorde);
        celdaCliente.setPadding(10);

        Font fonteClienteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, colorPrimario);
        Paragraph pTituloCliente = new Paragraph("DATOS DEL CLIENTE", fonteClienteTitulo);

        Font fonteClienteDatos = FontFactory.getFont(FontFactory.HELVETICA, 9, colorTexto);

        Paragraph pNombreCliente = new Paragraph();
        pNombreCliente.add(new Chunk("Apellido y Nombre / Razón Social: ", fonteDatosTitulo));
        String nombreCliente = factura.getCuitReceptor() == 0 ? "CONSUMIDOR FINAL" : "CLIENTE S.A.";
        pNombreCliente.add(new Chunk(nombreCliente, fonteClienteDatos));

        Paragraph pDomicilioCliente = new Paragraph();
        pDomicilioCliente.add(new Chunk("Domicilio: ", fonteDatosTitulo));
        pDomicilioCliente.add(new Chunk("N/A", fonteClienteDatos));

        Paragraph pCUITCliente = new Paragraph();
        pCUITCliente.add(new Chunk("CUIT: ", fonteDatosTitulo));
        String cuitReceptor = factura.getCuitReceptor() == 0 ? "N/A" : formatearCUIT(factura.getCuitReceptor());
        pCUITCliente.add(new Chunk(cuitReceptor, fonteClienteDatos));

        Paragraph pCondicionIVACliente = new Paragraph();
        pCondicionIVACliente.add(new Chunk("Condición frente al IVA: ", fonteDatosTitulo));
        pCondicionIVACliente.add(new Chunk(obtenerCondicionIVA(factura.getCuitReceptor()), fonteClienteDatos));

        celdaCliente.addElement(pTituloCliente);
        celdaCliente.addElement(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 4)));
        celdaCliente.addElement(pNombreCliente);
        celdaCliente.addElement(pDomicilioCliente);
        celdaCliente.addElement(pCUITCliente);
        celdaCliente.addElement(pCondicionIVACliente);

        datosCliente.addCell(celdaCliente);
        doc.add(datosCliente);

        doc.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 8)));

        // TABLA DE PRODUCTOS - Diferente estructura según tipo de comprobante
        boolean esFacturaC = (factura.getTipoComprobante() == 11);

        PdfPTable tabla;
        String[] encabezados;
        float[] anchos;

        if (esFacturaC) {
            // Factura C: Sin columna de IVA
            tabla = new PdfPTable(4);
            encabezados = new String[]{"DESCRIPCIÓN", "CANT.", "P. UNITARIO", "IMPORTE"};
            anchos = new float[]{5f, 1f, 2f, 2f};
        } else {
            // Otras facturas: Con columna de IVA
            tabla = new PdfPTable(5);
            encabezados = new String[]{"DESCRIPCIÓN", "CANT.", "P. UNITARIO", "% IVA", "IMPORTE"};
            anchos = new float[]{5f, 1f, 2f, 1.5f, 2f};
        }

        tabla.setWidthPercentage(100);
        tabla.setWidths(anchos);

        // Encabezados de la tabla con estilo formal
        Font fonteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Color colorEncabezado = colorPrimario;

        for (String titulo : encabezados) {
            PdfPCell celda = new PdfPCell(new Phrase(titulo, fonteEncabezado));
            celda.setBackgroundColor(colorEncabezado);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setPadding(8);
            celda.setBorder(Rectangle.BOX);
            celda.setBorderColor(Color.WHITE);
            tabla.addCell(celda);
        }

        // Filas de productos con estilo alternado
        Font fonteTabla = FontFactory.getFont(FontFactory.HELVETICA, 8, colorTexto);
        Font fonteTablaNum = FontFactory.getFont(FontFactory.HELVETICA, 8, colorTexto);

        for (int i = 0; i < factura.getItems().size(); i++) {
            FacturaItem item = factura.getItems().get(i);
            Color colorFila = (i % 2 == 0) ? Color.WHITE : new Color(248, 248, 248);

            // Descripción
            PdfPCell cDescripcion = new PdfPCell(new Phrase(item.getDescripcion(), fonteTabla));
            cDescripcion.setBackgroundColor(colorFila);
            cDescripcion.setPadding(6);
            cDescripcion.setBorder(Rectangle.BOX);
            cDescripcion.setBorderColor(colorBorde);
            tabla.addCell(cDescripcion);

            // Cantidad
            PdfPCell cCantidad = new PdfPCell(new Phrase(String.valueOf(item.getCantidad()), fonteTablaNum));
            cCantidad.setBackgroundColor(colorFila);
            cCantidad.setPadding(6);
            cCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
            cCantidad.setBorder(Rectangle.BOX);
            cCantidad.setBorderColor(colorBorde);
            tabla.addCell(cCantidad);

            // Precio Unitario
            PdfPCell cPrecio = new PdfPCell(new Phrase(String.format("$ %.2f", item.getPrecioUnitario()), fonteTablaNum));
            cPrecio.setBackgroundColor(colorFila);
            cPrecio.setPadding(6);
            cPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cPrecio.setBorder(Rectangle.BOX);
            cPrecio.setBorderColor(colorBorde);
            tabla.addCell(cPrecio);

            // IVA (solo si no es factura C)
            if (!esFacturaC) {
                PdfPCell cIVA = new PdfPCell(new Phrase("21%", fonteTablaNum));
                cIVA.setBackgroundColor(colorFila);
                cIVA.setPadding(6);
                cIVA.setHorizontalAlignment(Element.ALIGN_CENTER);
                cIVA.setBorder(Rectangle.BOX);
                cIVA.setBorderColor(colorBorde);
                tabla.addCell(cIVA);
            }

            // Importe
            PdfPCell cImporte = new PdfPCell(new Phrase(String.format("$ %.2f", item.getSubtotal()), fonteTablaNum));
            cImporte.setBackgroundColor(colorFila);
            cImporte.setPadding(6);
            cImporte.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cImporte.setBorder(Rectangle.BOX);
            cImporte.setBorderColor(colorBorde);
            tabla.addCell(cImporte);
        }

        doc.add(tabla);

        // **FOOTER FIJO - Siempre en la parte inferior del PDF**
        agregarFooterFijo(canvas, doc, factura, esFacturaC, colorPrimario, colorBorde, colorTexto, colorCAE);

        // PIE DE PÁGINA
        float pieY = 30f; // Posición fija desde el bottom
        canvas.beginText();
        canvas.setFontAndSize(com.lowagie.text.pdf.BaseFont.createFont(), 8);
        canvas.setTextMatrix((PageSize.A4.getWidth() / 2) - 150, pieY);
        canvas.showText("Comprobante Autorizado por AFIP - SIAJ INVENTARIOS - Sistema de Facturación Electrónica");
        canvas.endText();

        doc.close();
        System.out.println("✅ PDF de factura electrónica (formato AFIP) generado: " + rutaSalida);
        return rutaSalida;
    }

    /**
     * Agrega un QR pequeño en la esquina superior derecha
     */
    private void agregarQRPequeno(PdfContentByte canvas, Factura factura, float pageWidth, float pageHeight) {
        try {
            String urlVerificacion = generarURLVerificacion(factura);
            BufferedImage qrImage = generarCodigoQR(urlVerificacion, 80, 80); // QR más pequeño

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);
            Image qrPdfImage = Image.getInstance(baos.toByteArray());

            float qrSize = 80f; // Tamaño reducido
            qrPdfImage.scaleToFit(qrSize, qrSize);

            // Posicionar en esquina superior derecha
            float qrX = pageWidth - qrSize - 30;
            float qrY = pageHeight - qrSize - 30;
            qrPdfImage.setAbsolutePosition(qrX, qrY);

            canvas.addImage(qrPdfImage);

            // Agregar texto pequeño debajo del QR
            canvas.beginText();
            canvas.setFontAndSize(com.lowagie.text.pdf.BaseFont.createFont(), 6);
            canvas.setTextMatrix(qrX + 15, qrY + 5);
            canvas.showText("Verificar en AFIP");
            canvas.endText();

        } catch (Exception e) {
            System.err.println("Error generando QR pequeño: " + e.getMessage());
        }
    }

    /**
     * Agrega el footer fijo en la parte inferior del PDF
     */
    private void agregarFooterFijo(PdfContentByte canvas, Document doc, Factura factura, boolean esFacturaC,
                                   Color colorPrimario, Color colorBorde, Color colorTexto, Color colorCAE) throws Exception {

        float pageWidth = PageSize.A4.getWidth();
        float footerHeight = 100f;
        float footerY = 50f; // Margen desde el bottom

        // Dibujar rectángulo del footer
        canvas.setColorStroke(colorBorde);
        canvas.rectangle(20, footerY, pageWidth - 40, footerHeight);
        canvas.stroke();

        // Línea divisoria vertical para separar CAE de totales
        float dividerX = pageWidth * 0.6f;
        canvas.moveTo(dividerX, footerY);
        canvas.lineTo(dividerX, footerY + footerHeight);
        canvas.stroke();

        // LADO IZQUIERDO - Información CAE
        canvas.beginText();
        canvas.setFontAndSize(com.lowagie.text.pdf.BaseFont.createFont(), 10);
        canvas.setColorFill(colorCAE);
        canvas.setTextMatrix(30, footerY + footerHeight - 20);
        canvas.showText("CAE N°: " + factura.getCae());
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(com.lowagie.text.pdf.BaseFont.createFont(), 9);
        canvas.setColorFill(colorTexto);
        canvas.setTextMatrix(30, footerY + footerHeight - 35);
        String fechaVto = factura.getFechaVencimientoCAE() != null ?
                formatearFecha(factura.getFechaVencimientoCAE().toString()) : "N/A";
        canvas.showText("Fecha Vto. CAE: " + fechaVto);
        canvas.endText();

//        canvas.beginText();
//        canvas.setTextMatrix(30, footerY + footerHeight - 50);
//        canvas.showText("Punto de Venta: " + String.format("%04d", factura.getPuntoVenta()));
//        canvas.endText();
//
//        canvas.beginText();
//        canvas.setTextMatrix(30, footerY + footerHeight - 65);
//        canvas.showText("Tipo Comprobante: " + factura.getTipoComprobante());
//        canvas.endText();

        // LADO DERECHO - Totales
        float totalesX = dividerX + 20;

        if (esFacturaC) {
            // Solo mostrar total para Factura C
            canvas.beginText();
            canvas.setFontAndSize(com.lowagie.text.pdf.BaseFont.createFont(), 14);
            canvas.setColorFill(colorPrimario);
            canvas.setTextMatrix(totalesX, footerY + footerHeight - 40);
            canvas.showText("TOTAL: $ " + String.format("%.2f", factura.getTotal()));
            canvas.endText();
        } else {
            // Mostrar subtotal, IVA y total
            double subtotal = factura.getTotal() / 1.21;
            double ivaTotal = factura.getTotal() - subtotal;

            canvas.beginText();
            canvas.setFontAndSize(com.lowagie.text.pdf.BaseFont.createFont(), 10);
            canvas.setColorFill(colorTexto);
            canvas.setTextMatrix(totalesX, footerY + footerHeight - 25);
            canvas.showText("Subtotal: $ " + String.format("%.2f", subtotal));
            canvas.endText();

            canvas.beginText();
            canvas.setTextMatrix(totalesX, footerY + footerHeight - 40);
            canvas.showText("IVA 21%: $ " + String.format("%.2f", ivaTotal));
            canvas.endText();

            canvas.beginText();
            canvas.setFontAndSize(com.lowagie.text.pdf.BaseFont.createFont(), 12);
            canvas.setColorFill(colorPrimario);
            canvas.setTextMatrix(totalesX, footerY + footerHeight - 60);
            canvas.showText("TOTAL: $ " + String.format("%.2f", factura.getTotal()));
            canvas.endText();
        }
    }

    private String obtenerDescripcionTipoComprobante(int tipo) {
        switch (tipo) {
            case 1: return "FACTURA";
            case 6: return "FACTURA";
            case 11: return "FACTURA";
            case 51: return "FACTURA";
            default: return "COMPROBANTE";
        }
    }

    private String obtenerLetraTipoComprobante(int tipo) {
        switch (tipo) {
            case 1: return "A";
            case 6: return "B";
            case 11: return "C";
            case 51: return "M";
            default: return "X";
        }
    }

    private String formatearCUIT(long cuit) {
        String cuitStr = String.valueOf(cuit);
        if (cuitStr.length() == 11) {
            return cuitStr.substring(0, 2) + "-" + cuitStr.substring(2, 10) + "-" + cuitStr.substring(10);
        }
        return cuitStr;
    }

    private String formatearFecha(String fecha) {
        try {
            // Intentar parsear como yyyy-MM-dd (formato típico de toString() de java.util.Date)
            if (fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
                LocalDate date = LocalDate.parse(fecha); // parsea como yyyy-MM-dd por defecto
                return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }

            // Si viene como yyyyMMdd (8 caracteres), formatear manualmente
            if (fecha.length() == 8 && fecha.matches("\\d{8}")) {
                String year = fecha.substring(0, 4);
                String month = fecha.substring(4, 6);
                String day = fecha.substring(6, 8);
                return day + "/" + month + "/" + year;
            }

        } catch (Exception e) {
            // Error de parseo, continuar abajo
        }

        // Si no se pudo formatear, devolver la original
        return fecha;
    }

    private String obtenerCondicionIVA(long cuit) {
        return cuit == 0 ? "Consumidor Final" : "Responsable Inscripto";
    }

    /**
     * Genera la URL de verificación para el código QR
     * En homologación usa datos de prueba
     */
    private String generarURLVerificacion(Factura factura) {
        // Para homologación, usamos una URL que simula la verificación de AFIP
        // En producción sería: https://www.afip.gob.ar/fe/qr/

        StringBuilder url = new StringBuilder();
        url.append("https://serviciosweb.afip.gob.ar/genericos/comprobantes/");
        url.append("?ver=").append("1"); // Versión
        url.append("&vd=").append(formatearFecha(factura.getFechaEmision().toString())); // Fecha
        url.append("&vt=").append(factura.getTipoComprobante()); // Tipo comprobante
        url.append("&vn=").append(factura.getNumero()); // Número
        url.append("&vi=").append(String.format("%.2f", factura.getTotal()).replace(",", ".")); // Importe
        url.append("&vr=").append(factura.getCuitReceptor() == 0 ? "0" : String.valueOf(factura.getCuitReceptor())); // CUIT receptor
        url.append("&ve=").append(factura.getCuitEmisor()); // CUIT emisor
        url.append("&vc=").append(factura.getCae()); // CAE
        url.append("&cl=").append("HOMOLOGACION"); // Indica que es homologación

        return url.toString();
    }

    /**
     * Genera un código QR usando ZXing
     */
    private BufferedImage generarCodigoQR(String texto, int ancho, int alto) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, ancho, alto, hints);

        BufferedImage image = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        graphics.setColor(java.awt.Color.WHITE);
        graphics.fillRect(0, 0, ancho, alto);
        graphics.setColor(java.awt.Color.BLACK);

        for (int i = 0; i < bitMatrix.getWidth(); i++) {
            for (int j = 0; j < bitMatrix.getHeight(); j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        graphics.dispose();
        return image;
    }
}