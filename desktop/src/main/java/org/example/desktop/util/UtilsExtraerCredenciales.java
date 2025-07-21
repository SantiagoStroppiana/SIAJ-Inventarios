package org.example.desktop.util;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Clase utilitaria para parsear credenciales de una respuesta SOAP de WSAA.
 */
public class UtilsExtraerCredenciales {

    /**
     * Contenedor simple para token y sign.
     */
    public static class Credenciales {
        private final String token;
        private final String sign;

        public Credenciales(String token, String sign) {
            this.token = token;
            this.sign = sign;
        }

        public String getToken() {
            return token;
        }

        public String getSign() {
            return sign;
        }
    }

    /**
     * Parseá el XML SOAP que contiene <token> y <sign> dentro de <credentials>.
     *
     * @param soapXml El texto completo de la respuesta SOAP.
     * @return Un objeto Credenciales con los valores extraídos.
     * @throws Exception Si ocurre un error durante parsing o no se encuentran etiquetas.
     */
    public static Credenciales parsearCredenciales(String soapXml) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        Document doc = dbFactory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(soapXml.getBytes("UTF-8")));

        NodeList tokens = doc.getElementsByTagName("token");
        NodeList signs  = doc.getElementsByTagName("sign");

        if (tokens.getLength() == 0 || signs.getLength() == 0) {
            throw new RuntimeException("No se encontraron etiquetas <token> o <sign> en la respuesta.");
        }

        String token = tokens.item(0).getTextContent().trim();
        String sign  = signs.item(0).getTextContent().trim();

        return new Credenciales(token, sign);
    }
}
