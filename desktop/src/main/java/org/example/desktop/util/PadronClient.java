package org.example.desktop.util;

import org.example.desktop.util.Empresa;
import org.example.desktop.util.Persona;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class PadronClient {
    //PRODUCCION
    private static final String URL_PROD = "https://aws.afip.gov.ar/sr-padron/webservices/personaServiceA13";


    public static Object consultarCUIT(String token, String sign, String cuitRep, String cuitConsultada) throws Exception {
        if (esPersonaJuridica(cuitConsultada)) {
            return consultarPersonaJuridica(token, sign, cuitRep, cuitConsultada);
        } else {
            return consultarPersonaFisica(token, sign, cuitRep, cuitConsultada);
        }
    }

    private static boolean esPersonaJuridica(String cuit) {
        int pref = Integer.parseInt(cuit.substring(0, 2));
        return pref == 30 || pref == 33 || pref == 34;
    }

    private static Persona consultarPersonaFisica(String token, String sign, String cuitRep, String cuitCons) throws Exception {
        String xml = enviarRequest(token, sign, cuitRep, cuitCons);
        Document doc = parseXml(xml);
        Element persona = (Element) doc.getElementsByTagName("persona").item(0);

        Persona p = new Persona();
        p.setIdPersona(getText(persona, "idPersona"));
        p.setNombre(getText(persona, "nombre"));
        p.setApellido(getText(persona, "apellido"));
        p.setTipoDocumento(getText(persona, "tipoDocumento"));
        p.setNumeroDocumento(getText(persona, "numeroDocumento"));
        p.setDescripcionActividadPrincipal(getText(persona, "descripcionActividadPrincipal"));
        p.setFechaNacimiento(getText(persona, "fechaNacimiento"));
        asignarDomicilios(persona, p);
        return p;
    }

    private static Empresa consultarPersonaJuridica(String token, String sign, String cuitRep, String cuitCons) throws Exception {
        String xml = enviarRequest(token, sign, cuitRep, cuitCons);
        System.out.println("===== XML devuelto por AFIP =====");
        System.out.println(xml);

        Document doc = parseXml(xml);
        Element persona = (Element) doc.getElementsByTagName("persona").item(0);

        Empresa e = new Empresa();
        e.setNumeroDocumento(cuitCons);
        e.setRazonSocial(abreviarRazonSocial(getText(persona, "razonSocial")));
        e.setDescripcionActividadPrincipal(getText(persona, "descripcionActividadPrincipal"));
        asignarDomicilios(persona, e);
        return e;
    }

    private static String enviarRequest(String token, String sign, String cuitRep, String cuitCons) throws Exception {
        String soap = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:a13=\"http://a13.soap.ws.server.puc.sr/\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<a13:getPersona>"
                + "<token>" + token + "</token>"
                + "<sign>" + sign + "</sign>"
                + "<cuitRepresentada>" + cuitRep + "</cuitRepresentada>"
                + "<idPersona>" + cuitCons + "</idPersona>"
                + "</a13:getPersona>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        HttpURLConnection conn = (HttpURLConnection) new URL(URL_PROD).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"\"");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(soap.getBytes(StandardCharsets.UTF_8));
        }

        InputStream is = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder resp = new StringBuilder();
        br.lines().forEach(l -> resp.append(l).append("\n"));
        String xml = resp.toString();

        if (conn.getResponseCode() >= 400) {
            System.err.println("Respuesta SOAP con error:");
            System.err.println(xml);
            throw new RuntimeException("Error al consultar CUIT: HTTP " + conn.getResponseCode());
        }

        return xml;
    }

    private static Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    private static void asignarDomicilios(Element persona, Object obj) {
        NodeList doms = persona.getElementsByTagName("domicilio");
        String real = "", fiscal = "";
        if (doms.getLength() >= 1) real = buildAddress((Element) doms.item(0));
        if (doms.getLength() >= 2) fiscal = buildAddress((Element) doms.item(1));

        if (obj instanceof Persona) {
            ((Persona) obj).setDomicilioReal(real);
            ((Persona) obj).setDomicilioFiscal(fiscal);
        } else if (obj instanceof Empresa) {
            ((Empresa) obj).setDomicilioFiscal(fiscal);
        }
    }

    private static String buildAddress(Element dom) {
        String calle = getText(dom, "calle");
        String numero = getText(dom, "numero");
        String localidad = getText(dom, "localidad");
        String provincia = abreviarProvincia(getText(dom, "descripcionProvincia"));
        String cp = getText(dom, "codigoPostal");

        String direccion = String.join(" ",
                calle,
                numero,
                localidad,
                provincia
        ).replaceAll("\\s+", " ").trim();

        if (!cp.isEmpty()) {
            direccion += " (" + cp + ")";
        }

        return direccion;
    }

    private static String abreviarProvincia(String provincia) {
        if (provincia == null) return "";

        String p = provincia.toUpperCase();

        if (p.contains("CIUDAD AUTONOMA")) return "CABA";
        if (p.contains("BUENOS AIRES") && !p.contains("CIUDAD")) return "Bs As";
        if (p.contains("CORDOBA")) return "Córdoba";
        if (p.contains("SANTA FE")) return "Sta Fe";
        if (p.contains("MENDOZA")) return "Mendoza";
        if (p.contains("ENTRE RIOS")) return "E. Ríos";
        if (p.contains("TIERRA DEL FUEGO")) return "T. del Fuego";
        if (p.contains("CORRIENTES")) return "Corrientes";
        if (p.contains("CHACO")) return "Chaco";
        if (p.contains("CHUBUT")) return "Chubut";
        if (p.contains("FORMOSA")) return "Formosa";
        if (p.contains("JUJUY")) return "Jujuy";
        if (p.contains("LA PAMPA")) return "La Pampa";
        if (p.contains("LA RIOJA")) return "La Rioja";
        if (p.contains("MISIONES")) return "Misiones";
        if (p.contains("NEUQUEN")) return "Neuquén";
        if (p.contains("RIO NEGRO")) return "R. Negro";
        if (p.contains("SALTA")) return "Salta";
        if (p.contains("SAN JUAN")) return "San Juan";
        if (p.contains("SAN LUIS")) return "San Luis";
        if (p.contains("SANTA CRUZ")) return "Sta Cruz";
        if (p.contains("SANTIAGO DEL ESTERO")) return "Sgo del Estero";
        if (p.contains("TUCUMAN")) return "Tucumán";
        if (p.contains("CATAMARCA")) return "Catamarca";

        return provincia; // Default sin abreviar
    }

    private static String abreviarRazonSocial(String razonSocial) {
        if (razonSocial == null || razonSocial.trim().isEmpty()) return "";

        String rs = razonSocial.toUpperCase().trim();

        int idxSociedad = rs.indexOf("SOCIEDAD");
        if (idxSociedad < 0) {
            // No encontró "SOCIEDAD", sólo Title Case y limpio palabras comunes
            rs = limpiarYTitleCase(rs);
            return rs;
        }

        String nombreComercial = rs.substring(0, idxSociedad).trim();
        String tipoSocietario = rs.substring(idxSociedad).trim();

        // Sacar iniciales de tipo societario (todas las palabras excepto "SOCIEDAD" se reducen a iniciales)
        // Por ejemplo: "SOCIEDAD ANONIMA COMERCIAL INDUSTRIAL" -> "S.A.C.I."
        String[] palabras = tipoSocietario.split("\\s+");
        StringBuilder sigla = new StringBuilder();

        for (int i = 0; i < palabras.length; i++) {
            if (i == 0) {
                // La palabra "SOCIEDAD", ponemos "S."
                sigla.append("S.");
            } else {
                // Sólo la inicial seguida de punto
                if (!palabras[i].isEmpty()) {
                    sigla.append(palabras[i].charAt(0)).append(".");
                }
            }
        }

        nombreComercial = titleCase(nombreComercial);

        return (nombreComercial + " " + sigla.toString()).trim();
    }

    private static String limpiarYTitleCase(String texto) {
        texto = texto.replaceAll("COMERCIAL", "")
                .replaceAll("INDUSTRIAL", "")
                .replaceAll("FINANCIERA", "")
                .replaceAll("INMOBILIARIA", "")
                .replaceAll("AGROPECUARIA", "")
                .replaceAll("Y", "")
                .replaceAll("\\s+", " ")
                .trim();

        return titleCase(texto);
    }

    private static String titleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
                titleCase.append(c);
            } else {
                if (nextTitleCase) {
                    titleCase.append(Character.toTitleCase(c));
                    nextTitleCase = false;
                } else {
                    titleCase.append(Character.toLowerCase(c));
                }
            }
        }
        return titleCase.toString();
    }





    private static String getText(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        if (nl.getLength() == 0) return "";
        return Optional.ofNullable(nl.item(0).getTextContent()).orElse("").trim();
    }
}
