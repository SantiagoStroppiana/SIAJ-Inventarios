package org.example.desktop.util;

import org.example.desktop.util.Factura;
import org.example.desktop.util.FacturaItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class FacturaElectronicaService {

    private static final String WSFE_HOMO_URL = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx";
//    private static final String WSFE_PROD_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";

    // Obtiene el último comprobante autorizado
    private int obtenerUltimoComprobanteAutorizado(String token, String sign, int ptoVta, int tipoCbte, long cuit) throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\">" +
                "<soap:Body>" +
                "<ar:FECompUltimoAutorizado>" +
                "<ar:Auth>" +
                "<ar:Token>" + token + "</ar:Token>" +
                "<ar:Sign>" + sign + "</ar:Sign>" +
                "<ar:Cuit>" + cuit + "</ar:Cuit>" +
                "</ar:Auth>" +
                "<ar:PtoVta>" + ptoVta + "</ar:PtoVta>" +
                "<ar:CbteTipo>" + tipoCbte + "</ar:CbteTipo>" +
                "</ar:FECompUltimoAutorizado>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        HttpURLConnection conn = (HttpURLConnection) new URL(WSFE_HOMO_URL).openConnection();
//        HttpURLConnection conn = (HttpURLConnection) new URL(WSFE_PROD_URL).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"http://ar.gov.afip.dif.FEV1/FECompUltimoAutorizado\"");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(xml.getBytes("UTF-8"));
        }

        try (InputStream is = conn.getInputStream()) {
            Document resp = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            NodeList nodes = resp.getElementsByTagName("CbteNro");
            if (nodes.getLength() == 0) {
                System.out.println("⚠️ No se encontró CbteNro, usando 0 como base");
                return 0; // Si no hay comprobantes previos, empezar desde 0
            }
            return Integer.parseInt(nodes.item(0).getTextContent());
        }
    }

    public Factura emitirFactura(String token, String sign, int ptoVta, int tipoCbte,
                                 long cuitEmi, long cuitRec, List<FacturaItem> items) throws Exception {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int ult = obtenerUltimoComprobanteAutorizado(token, sign, ptoVta, tipoCbte, cuitEmi);
        int nro = ult + 1;

        double total = items.stream().mapToDouble(FacturaItem::getSubtotal).sum();
        double neto = total;

        System.out.println("📋 Emitiendo factura:");
        System.out.println("   Punto de Venta: " + ptoVta);
        System.out.println("   Tipo Comprobante: " + tipoCbte);
        System.out.println("   Número: " + nro);
        System.out.println("   Total: $" + String.format("%.2f", total));

        // Construir el detalle del comprobante
        StringBuilder det = new StringBuilder();
        det.append("<ar:FECAEDetRequest>")
                .append("<ar:Concepto>1</ar:Concepto>") // 1 = Productos
                .append("<ar:DocTipo>99</ar:DocTipo>") // 99 = Sin identificar / Consumidor Final
                .append("<ar:DocNro>").append(cuitRec == 0 ? "0" : String.valueOf(cuitRec)).append("</ar:DocNro>")
                .append("<ar:CbteDesde>").append(nro).append("</ar:CbteDesde>")
                .append("<ar:CbteHasta>").append(nro).append("</ar:CbteHasta>")
                .append("<ar:CbteFch>").append(fecha).append("</ar:CbteFch>")
                .append("<ar:ImpTotal>").append(String.format(Locale.US, "%.2f", total)).append("</ar:ImpTotal>")
                .append("<ar:ImpNeto>").append(String.format(Locale.US, "%.2f", neto)).append("</ar:ImpNeto>")
                .append("<ar:ImpIVA>0.00</ar:ImpIVA>") // Sin IVA para Factura C
                .append("<ar:ImpTotConc>0.00</ar:ImpTotConc>") // Importe total de conceptos que no integran precio neto gravado
                .append("<ar:ImpOpEx>0.00</ar:ImpOpEx>") // Importe total de operaciones exentas
                .append("<ar:ImpTrib>0.00</ar:ImpTrib>") // Importe total de tributos
                .append("<ar:MonId>PES</ar:MonId>") // Moneda: Pesos
                .append("<ar:MonCotiz>1.00</ar:MonCotiz>") // Cotización: 1 para pesos
                .append("</ar:FECAEDetRequest>");

        // Construir el XML SOAP completo
        String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\">" +
                "<soap:Body>" +
                "<ar:FECAESolicitar>" +
                "<ar:Auth>" +
                "<ar:Token>" + token + "</ar:Token>" +
                "<ar:Sign>" + sign + "</ar:Sign>" +
                "<ar:Cuit>" + cuitEmi + "</ar:Cuit>" +
                "</ar:Auth>" +
                "<ar:FeCAEReq>" +
                "<ar:FeCabReq>" +
                "<ar:CantReg>1</ar:CantReg>" +
                "<ar:PtoVta>" + ptoVta + "</ar:PtoVta>" +
                "<ar:CbteTipo>" + tipoCbte + "</ar:CbteTipo>" +
                "</ar:FeCabReq>" +
                "<ar:FeDetReq>" +
                det.toString() +
                "</ar:FeDetReq>" +
                "</ar:FeCAEReq>" +
                "</ar:FECAESolicitar>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        // Debug: Imprimir el XML que se va a enviar
        System.out.println("📤 XML enviado a AFIP:");
        System.out.println(body);

        HttpURLConnection conn = (HttpURLConnection) new URL(WSFE_HOMO_URL).openConnection();
//        HttpURLConnection conn = (HttpURLConnection) new URL(WSFE_PROD_URL).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"http://ar.gov.afip.dif.FEV1/FECAESolicitar\"");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes("UTF-8"));
        }

        InputStream response;
        try {
            response = conn.getInputStream();
        } catch (IOException e) {
            // Imprime el XML de error completo
            if (conn.getErrorStream() != null) {
                String err = new String(conn.getErrorStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                System.err.println("❌ Error AFIP FECAESolicitar:\n" + err);
            }
            throw e;
        }

        Document resp = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(response);

        // Imprimir la respuesta completa
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter sw = new StringWriter();
        tf.transform(new DOMSource(resp), new StreamResult(sw));
        System.out.println("📦 Respuesta completa FECAESolicitar:\n" + sw.toString());

        // Verificar errores
        NodeList errs = resp.getElementsByTagName("Err");
        if (errs.getLength() > 0) {
            for (int i = 0; i < errs.getLength(); i++) {
                Element errEl = (Element) errs.item(i);
                String code = errEl.getElementsByTagName("Code").item(0).getTextContent();
                String msg  = errEl.getElementsByTagName("Msg").item(0).getTextContent();
                System.err.printf("❌ AFIP Error %s: %s%n", code, msg);
            }
            throw new RuntimeException("AFIP devolvió errores. Ver logs anteriores.");
        }

        // Extraer el CAE
        NodeList caeNodes = resp.getElementsByTagName("CAE");
        if (caeNodes.getLength() == 0)
            throw new RuntimeException("No vino CAE en FECAESolicitar");

        String cae = caeNodes.item(0).getTextContent();

        // Extraer fecha de vencimiento del CAE
        LocalDate fechaVencCAE = null;
        NodeList fechaVencNodes = resp.getElementsByTagName("CAEFchVto");
        if (fechaVencNodes.getLength() > 0) {
            String fechaVencStr = fechaVencNodes.item(0).getTextContent();
            // Formato: YYYYMMDD
            if (fechaVencStr.length() == 8) {
                fechaVencCAE = LocalDate.parse(fechaVencStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
        }

        System.out.println("✅ Factura autorizada:");
        System.out.println("   CAE: " + cae);
        System.out.println("   Fecha Venc. CAE: " + (fechaVencCAE != null ? fechaVencCAE : "No disponible"));

        // Crear y retornar la factura con todos los datos
        return new Factura(ptoVta, tipoCbte, cuitEmi, cuitRec, items, total,
                cae, LocalDate.now(), fechaVencCAE, nro);
    }
}