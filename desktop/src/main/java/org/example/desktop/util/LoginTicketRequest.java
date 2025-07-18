package org.example.desktop.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.bouncycastle.cert.jcajce.*;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Random;

public class LoginTicketRequest {
    //Produccion
    private static final String WSAA_URL_PROD = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    //Testing
//    private static final String WSAA_URL_PROD = "https://wsaahomo.afip.gov.ar/ws/services/LoginCms";


    public static String generarTicketProduccion(String service) throws Exception {
        String dirPath = VariablesEntorno.getTA();
        if (dirPath == null || dirPath.isBlank()) throw new IllegalStateException("Falta TA_SECRET_DIR en .env");
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();  // asegurar que exista

        File ticketFile = new File(dir, "ticket" + service + ".xml");



        Security.addProvider(new BouncyCastleProvider());

        if (ticketFile.exists()) {
            String contenido = Files.readString(ticketFile.toPath(), StandardCharsets.UTF_8);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new ByteArrayInputStream(contenido.getBytes(StandardCharsets.UTF_8)));

            NodeList expirationNodes = doc.getElementsByTagName("expirationTime");
            if (expirationNodes.getLength() == 0) {
                System.out.println("⚠️ No se encontró expirationTime en ticket.xml. Se generará uno nuevo.");
                ticketFile.delete();
                return generarTicketProduccion(service);
            }

            String expirationStr = expirationNodes.item(0).getTextContent();
            Instant expiration = Instant.parse(expirationStr);
            if (Instant.now().isBefore(expiration)) {
                System.out.println("♻️ Reutilizando ticket existente (vigente hasta " + expiration + ")");
                return contenido;
            } else {
                System.out.println("⏰ Ticket expirado. Generando uno nuevo...");
            }
        }

        // Generar nuevo ticket
        //Produccion
        String crtPath = VariablesEntorno.getCrtPath();
        String keyPath = VariablesEntorno.getKeyPath();

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert;
        try (FileInputStream fis = new FileInputStream(crtPath)) {
            cert = (X509Certificate) cf.generateCertificate(fis);
        }
        PrivateKey pk = leerClavePrivada(new File(keyPath));

        String uid = String.valueOf(new Random().nextInt(1_000_000));
        Instant now = Instant.now();
        String tra = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<loginTicketRequest version=\"1.0\">"
                + "<header><uniqueId>" + uid + "</uniqueId>"
                + "<generationTime>" + now.minusSeconds(60) + "</generationTime>"
                + "<expirationTime>" + now.plusSeconds(3600) + "</expirationTime></header>"
                + "<service>" + service + "</service>"
                + "</loginTicketRequest>";

        // Firmar
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        JcaContentSignerBuilder csb = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC");
        gen.addSignerInfoGenerator(
                new JcaSignerInfoGeneratorBuilder(
                        new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()
                ).build(csb.build(pk), new JcaX509CertificateHolder(cert))
        );
        gen.addCertificates(new JcaCertStore(Collections.singletonList(cert)));
        CMSSignedData signed = gen.generate(
                new CMSProcessableByteArray(tra.getBytes(StandardCharsets.UTF_8)), true
        );
        String cmsB64 = Base64.getEncoder().encodeToString(signed.getEncoded());

        // Enviar request
        String soapReq = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:ws=\"http://wsaa.view.sua.dvadac.desein.afip.gov\">"
                + "<soapenv:Header/><soapenv:Body>"
                + "<ws:loginCms><ws:in0>" + cmsB64 + "</ws:in0></ws:loginCms>"
                + "</soapenv:Body></soapenv:Envelope>";

        HttpURLConnection conn = (HttpURLConnection) new URL(WSAA_URL_PROD).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"\"");
        conn.getOutputStream().write(soapReq.getBytes(StandardCharsets.UTF_8));

        InputStream in;
        try {
            in = conn.getInputStream();
        } catch (IOException ex) {
            if (conn.getErrorStream() != null) {
                new BufferedReader(new InputStreamReader(conn.getErrorStream()))
                        .lines().forEach(System.err::println);
            }
            throw ex;
        }

        // Leer respuesta
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1) baos.write(buf, 0, len);

        String soapResponse = baos.toString("UTF-8");

        // Extraer XML del tag loginCmsReturn
        Document soapDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(soapResponse.getBytes(StandardCharsets.UTF_8)));
        NodeList retList = soapDoc.getElementsByTagName("loginCmsReturn");
        if (retList.getLength() == 0) {
            throw new RuntimeException("No se encontró loginCmsReturn");
        }

        String innerXml = retList.item(0).getTextContent();

        // Guardar el XML limpio (el ticket real)
        Files.write(ticketFile.toPath(), innerXml.getBytes(StandardCharsets.UTF_8));
        System.out.println("💾 Ticket guardado en " + ticketFile.getAbsolutePath());

        return innerXml;
    }



    private static PrivateKey leerClavePrivada(File f) throws Exception {
        String keyPem = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
        keyPem = keyPem.replaceAll("-----BEGIN.*KEY-----", "")
                .replaceAll("-----END.*KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(keyPem);
        return KeyFactory.getInstance("RSA").generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(decoded));
    }
}

