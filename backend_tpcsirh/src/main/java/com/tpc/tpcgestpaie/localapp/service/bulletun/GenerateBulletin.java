package com.tpc.tpcgestpaie.localapp.service.bulletun;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.Image; // iText 5 pour Flying Saucer
import com.tpc.tpcgestpaie.localapp.dto.bulletin.BulletinPaieGenerateDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.resource.ImageResource;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GenerateBulletin {

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");

    @Value("${app.security.qr-secret:mySuperSecretKeyForQREncryption123}")
    private String qrSecret;

    @Value("${app.security.qr-format-prefix:TPC}")
    private String qrFormatPrefix;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public byte[] generateBulletinPdf(BulletinPaieGenerateDTO dto) throws Exception {
        // 1) Charger le template HTML
        InputStream templateStream = getClass().getResourceAsStream("/templates/bulletin_template.html");
        if (templateStream == null) {
            throw new IllegalStateException("Template HTML introuvable dans resources/templates/");
        }
        String htmlTemplate = new String(templateStream.readAllBytes(), StandardCharsets.UTF_8);

        // 2) Construire les lignes du tableau
        StringBuilder rows = new StringBuilder();

        Map<String, Double> detailsSalaire = dto.getDetailsSalaire();
        if (detailsSalaire != null && !detailsSalaire.isEmpty()) {
            // SALAIRE DE BASE en premier
            detailsSalaire.entrySet().stream()
                    .filter(e -> "SALAIRE DE BASE".equalsIgnoreCase(e.getKey()))
                    .findFirst()
                    .ifPresent(e -> appendRow(rows, "SALAIRE DE BASE", e.getValue(), null, null, null));

            // Tous les autres
            detailsSalaire.entrySet().stream()
                    .filter(e -> e.getKey() == null || !"SALAIRE DE BASE".equalsIgnoreCase(e.getKey()))
                    .forEach(e -> {
                        String label = (e.getKey() == null ? "AUTRE" : e.getKey());
                        appendRow(rows, label, e.getValue(), null, null, null);
                    });
        }

        appendRow(rows, "HEURES SUPP", dto.getHeuresSup(), null, null, null);
        appendRow(rows, "PRIMES EXCEPTIONNELLES", dto.getPrimesExceptionnelles(), null, null, null);
        appendRow(rows, "PRIME 13EME MOIS", dto.getPrimes13eMois(), null, null, null);
        appendRow(rows, "AUTRE AVANTAGE", dto.getAutreAvantage(), null, null, null);

        if (dto.getPrimeAnciennete() != null && dto.getPrimeAnciennete() != 0.0) {
            appendRow(rows, "PRIME D'ANCIENNETE", dto.getPrimeAnciennete(), null, null, null);
        }

        String moisAnnee = dto.getMois();
        String getMois = moisAnnee.substring(5);

        if (getMois.equals("03")) {
            appendRow(rows, "TAXE RADIOPHONIQUE", null, dto.getTaxeRadiophonique(), null, null);
        } else if (getMois.equals("06")) {
            appendRow(rows, "TAXES TÉLÉVISUELLE", null, dto.getTaxeTelevisuel(), null, null);
        }

        Double brut = dto.getSalaireBrutArrondi() != null ? dto.getSalaireBrutArrondi() : dto.getSalaireBrut();
        appendRow(rows, "SALAIRE BRUT", brut, null, null, null);

        appendRow(rows, "CNSS", null, dto.getMontantCnss(), null, null);
        appendRow(rows, "ITS", null, dto.getMontantIpts(), null, null);
        appendRow(rows, "TOTAL RETENUE", null, dto.getTotalRetenue(), null, null);

        appendRow(rows, "CNSS (employeur)", null, null, null, dto.getMontantCnssEmployeur());
        appendRow(rows, "VPS", null, null, null, dto.getMontantVps());
        appendRow(rows, "AIB", null, null, null, dto.getMontantAib());

        appendRow(rows, "TOTAL CHARGE PATRONALE", null, null, null, dto.getTotalChargePatronale());

        appendRow(rows, "AVANCE", null, null, dto.getAvance(), null);
        appendRow(rows, "ACOMPTE", null, null, dto.getAcompte(), null);
        appendRow(rows, "MENSUALITE", null, null, dto.getMensualite(), null);
        appendRow(rows, "TOTAL AUTRES RETENUES", null, null, dto.getTotalRetenueNet(), null);

        // 3) GÉNÉRER LE QR CODE
        String secureQRData = generateSecureQRDataForBulletin(dto);
        String qrCodeImgTag = buildQrImgTag(secureQRData, 300);

        String dateFin = ("CDD".equals(dto.getTypeContrat()) && dto.getDateFin() != null)
                ? dto.getDateFin()
                : "Indéterminée";

        // 4) 🔥 CHARGER LE LOGO EN DATA URI BASE64
        String logoImgTag = loadLogoAsImgTag(dto.getLogoEntreprise());

        // 5) Remplacer les placeholders
        String filledHtml = htmlTemplate
                .replace("{{nomEntreprise}}", escape(dto.getNomEntreprise()))
                .replace("{{numeroCnssEmployeur}}", escape(dto.getNumeroCnssEmployeur()))
                .replace("{{signataire}}", escape(dto.getSignataire()))
                .replace("{{adresse}}", escape(dto.getAdresse()))
                .replace("{{telephone}}", escape(dto.getTelephone()))
                .replace("{{rccm}}", escape(dto.getRccm()))
                .replace("{{mail}}", escape(dto.getMail()))
                .replace("{{site}}", escape(dto.getSite()))
                .replace("{{nomBanque}}", escape(dto.getNomBanque()))
                .replace("{{mois}}", escape(dto.getMois()))
                .replace("{{logoEntreprise}}", logoImgTag) // 🔥 Utilise le data URI
                .replace("{{nomPrenomEmploye}}", escape(dto.getNomPrenomEmploye()))
                .replace("{{matriculeEmploye}}", escape(dto.getMatriculeEmploye()))
                .replace("{{fonction}}", escape(dto.getFonction()))
                .replace("{{typeContrat}}", escape(dto.getTypeContrat()))
                .replace("{{natureContrat}}", escape(dto.getNatureContrat()))
                .replace("{{dateDebut}}", escape(dto.getDateDebut()))
                .replace("{{dateFin}}", escape(dateFin))
                .replace("{{numeroCnss}}", escape(dto.getNumeroCnss()))
                .replace("{{nombreEnfant}}", safe(dto.getNombreEnfant()))
                .replace("{{tempsTravail}}", safe(dto.getTempsTravail()))
                .replace("{{heuresSup}}", safe(dto.getHeuresSup()))
                .replace("{{soldeConge}}", safe(dto.getSoldeConge()))
                .replace("{{congePris}}", safe(dto.getCongePris()))
                .replace("{{modePaiement}}", escape(dto.getModePaiement()))
                .replace("{{numeroCompteEmploye}}", escape(dto.getNumeroCompteEmploye()))
                .replace("{{salaireBase}}", formatMoney(dto.getSalaireBase()))
                .replace("{{salaireBrut}}", formatMoney(dto.getSalaireBrut()))
                .replace("{{salaireBrutArrondi}}", formatMoney(dto.getSalaireBrutArrondi()))
                .replace("{{mensualite}}", formatMoney(dto.getMensualite()))
                .replace("{{avance}}", formatMoney(dto.getAvance()))
                .replace("{{acompte}}", formatMoney(dto.getAcompte()))
                .replace("{{primesExceptionnelles}}", formatMoney(dto.getPrimesExceptionnelles()))
                .replace("{{primes13eMois}}", formatMoney(dto.getPrimes13eMois()))
                .replace("{{autreAvantage}}", formatMoney(dto.getAutreAvantage()))
                .replace("{{montantCnss}}", formatMoney(dto.getMontantCnss()))
                .replace("{{montantIpts}}", formatMoney(dto.getMontantIpts()))
                .replace("{{totalAutreRetenue}}", formatMoney(dto.getTotalAutreRetenue()))
                .replace("{{totalRetenue}}", formatMoney(dto.getTotalRetenue()))
                .replace("{{totalChargePatronale}}", formatMoney(dto.getTotalChargePatronale()))
                .replace("{{montantCnssEmployeur}}", formatMoney(dto.getMontantCnssEmployeur()))
                .replace("{{montantVps}}", formatMoney(dto.getMontantVps()))
                .replace("{{montantAib}}", formatMoney(dto.getMontantAib()))
                .replace("{{salaireNet}}", formatMoney(dto.getSalaireNet()))
                .replace("{{netAPayer}}", formatMoney(dto.getNetAPayer()))
                .replace("{{rows}}", rows.toString())
                .replace("{{qrCodeImg}}", qrCodeImgTag);

        // 6) HTML -> PDF avec Flying Saucer + support data URI
        return renderHtmlToPdf(filledHtml);
    }

    /**
     * 🔥 CHARGE LE LOGO ET RETOURNE UNE BALISE IMG AVEC DATA URI BASE64
     */
    private String loadLogoAsImgTag(String logoUrl) {
        if (logoUrl == null || logoUrl.isEmpty()) {
            log.warn("Logo URL est vide ou null");
            return ""; // Logo vide
        }

        try {
            // Construire le chemin complet du fichier
            String filePath = logoUrl.startsWith("/") ? logoUrl.substring(1) : logoUrl;
            Path logoPath = Paths.get(uploadDir, filePath.replace("uploads/", ""));

            log.debug("Tentative de chargement du logo: {}", logoPath);

            if (!Files.exists(logoPath)) {
                log.warn("Logo non trouvé: {}", logoPath);
                return "";
            }

            // Lire le fichier et convertir en base64
            byte[] imageBytes = Files.readAllBytes(logoPath);
            String mimeType = detectMimeType(imageBytes);
            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            log.debug("Logo chargé: {} ({} bytes, type: {})", logoPath, imageBytes.length, mimeType);

            // Retourner la balise img complète avec data URI
            return "<img src=\"data:" + mimeType + ";base64," + base64 + "\" alt=\"Logo\" style=\"max-width:150px;max-height:80px;\"/>";

        } catch (IOException e) {
            log.error("Erreur chargement logo: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Détecte le type MIME de l'image
     */
    private String detectMimeType(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length < 4) {
            return "image/png";
        }

        // PNG
        if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == 0x50 &&
                imageBytes[2] == 0x4E && imageBytes[3] == 0x47) {
            return "image/png";
        }

        // JPEG
        if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8 &&
                imageBytes[2] == (byte) 0xFF) {
            return "image/jpeg";
        }

        // GIF
        if (imageBytes[0] == 0x47 && imageBytes[1] == 0x49 && imageBytes[2] == 0x46) {
            return "image/gif";
        }

        return "image/png";
    }

    /**
     * 🔥 MÉTHODE CENTRALE : HTML vers PDF avec Flying Saucer (iText 5)
     * Support des images base64 via UserAgent personnalisé
     */
    private byte[] renderHtmlToPdf(String htmlContent) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();

        // 🔥 USER AGENT PERSONNALISÉ POUR DATA URI (base64)
        ITextOutputDevice outputDevice = renderer.getOutputDevice();
        ITextUserAgent userAgent = new ITextUserAgent(outputDevice) {
            @Override
            public ImageResource getImageResource(String uri) {
                // Gérer les data URI (base64)
                if (uri != null && uri.startsWith("data:")) {
                    try {
                        int commaIndex = uri.indexOf(",");
                        if (commaIndex > 0) {
                            // Extraire la partie base64 après la virgule
                            String base64Data = uri.substring(commaIndex + 1);

                            // Supprimer l'en-tête MIME si présent dans la partie base64
                            // (normalement déjà séparé par la virgule)
                            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

                            // Créer l'image iText 5
                            Image image = Image.getInstance(imageBytes);

                            // Wrapper pour Flying Saucer
                            ITextFSImage fsImage = new ITextFSImage(image);

                            return new ImageResource(uri, fsImage);
                        }
                    } catch (Exception e) {
                        log.error("Erreur décodage image base64 [{}]: {}", uri.substring(0, Math.min(50, uri.length())), e.getMessage());
                    }
                }
                // Pour les autres URIs (http, https, file), comportement par défaut
                return super.getImageResource(uri);
            }
        };

        renderer.getSharedContext().setUserAgentCallback(userAgent);
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(baos);

        return baos.toByteArray();
    }

    // ========== TOUTES LES AUTRES MÉTHODES ==========
    private String generateSecureQRDataForBulletin(BulletinPaieGenerateDTO dto) {
        try {
            String proprietaryData = buildProprietaryFormatForBulletin(dto);
            String encryptedData = encryptQRData(proprietaryData);
            return qrFormatPrefix + "v1:" + encryptedData;
        } catch (Exception e) {
            throw new RuntimeException("Impossible de générer le QR Code sécurisé: " + e.getMessage());
        }
    }

    private String buildProprietaryFormatForBulletin(BulletinPaieGenerateDTO dto) {
        StringBuilder sb = new StringBuilder();

        sb.append("TYPE:BULLETIN;");
        sb.append("MOIS:").append(dto.getMois() != null ? dto.getMois() : "").append(";");
        sb.append("EMP:").append(dto.getNomPrenomEmploye() != null ? dto.getNomPrenomEmploye() : "").append(";");
        sb.append("EMPMAT:").append(dto.getMatriculeEmploye() != null ? dto.getMatriculeEmploye() : "").append(";");
        sb.append("FONCTION:").append(dto.getFonction() != null ? dto.getFonction() : "").append(";");
        sb.append("COMP:").append(dto.getNomEntreprise() != null ? dto.getNomEntreprise() : "").append(";");
        sb.append("PHONE:").append(dto.getTelephone() != null ? dto.getTelephone() : "").append(";");
        sb.append("ADDRESS:").append(dto.getAdresse() != null ? dto.getAdresse() : "").append(";");
        sb.append("SIGNATAIRE:").append(dto.getSignataire() != null ? dto.getSignataire() : "").append(";");
        sb.append("CNSS_EMP:").append(dto.getNumeroCnss() != null ? dto.getNumeroCnss() : "").append(";");
        sb.append("CNSS_EMPLOYEUR:").append(dto.getNumeroCnssEmployeur() != null ? dto.getNumeroCnssEmployeur() : "").append(";");
        sb.append("TYPE_CONTRAT:").append(dto.getTypeContrat() != null ? dto.getTypeContrat() : "").append(";");
        sb.append("NATURE_CONTRAT:").append(dto.getNatureContrat() != null ? dto.getNatureContrat() : "").append(";");
        sb.append("DATE_DEBUT:").append(dto.getDateDebut() != null ? dto.getDateDebut() : "").append(";");
        sb.append("DATE_FIN:").append(dto.getDateFin() != null ? dto.getDateFin() : "").append(";");
        sb.append("SALAIRE_BASE:").append(dto.getSalaireBase() != null ? dto.getSalaireBase() : 0).append(";");
        sb.append("SALAIRE_BRUT:").append(dto.getSalaireBrut() != null ? dto.getSalaireBrut() : 0).append(";");
        sb.append("SALAIRE_NET:").append(dto.getSalaireNet() != null ? dto.getSalaireNet() : 0).append(";");
        sb.append("NET_A_PAYER:").append(dto.getNetAPayer() != null ? dto.getNetAPayer() : 0).append(";");
        sb.append("CNSS_MONTANT:").append(dto.getMontantCnss() != null ? dto.getMontantCnss() : 0).append(";");
        sb.append("IPTS:").append(dto.getMontantIpts() != null ? dto.getMontantIpts() : 0).append(";");
        sb.append("AVANCE:").append(dto.getAvance() != null ? dto.getAvance() : 0).append(";");
        sb.append("ACOMPTE:").append(dto.getAcompte() != null ? dto.getAcompte() : 0).append(";");
        sb.append("TOTAL_RETENUE:").append(dto.getTotalRetenue() != null ? dto.getTotalRetenue() : 0).append(";");
        sb.append("CNSS_EMPLOYEUR_MONTANT:").append(dto.getMontantCnssEmployeur() != null ? dto.getMontantCnssEmployeur() : 0).append(";");
        sb.append("VPS:").append(dto.getMontantVps() != null ? dto.getMontantVps() : 0).append(";");
        sb.append("AIB:").append(dto.getMontantAib() != null ? dto.getMontantAib() : 0).append(";");
        sb.append("TOTAL_CHARGE_PATRONALE:").append(dto.getTotalChargePatronale() != null ? dto.getTotalChargePatronale() : 0).append(";");
        sb.append("MODE_PAIEMENT:").append(dto.getModePaiement() != null ? dto.getModePaiement() : "").append(";");
        sb.append("BANQUE:").append(dto.getNomBanque() != null ? dto.getNomBanque() : "").append(";");
        sb.append("COMPTE:").append(dto.getNumeroCompteEmploye() != null ? dto.getNumeroCompteEmploye() : "").append(";");
        sb.append("TEMPS_TRAVAIL:").append(dto.getTempsTravail() != null ? dto.getTempsTravail() : 0).append(";");
        sb.append("HEURES_SUP:").append(dto.getHeuresSup() != null ? dto.getHeuresSup() : 0).append(";");
        sb.append("TS:").append(System.currentTimeMillis()).append(";");

        String bulletinHash = generateBulletinHash(dto);
        sb.append("HASH:").append(bulletinHash.substring(0, 12)).append(";");
        sb.append("SIG:").append(generateBulletinSignature(dto, bulletinHash));

        return sb.toString();
    }

    private String generateBulletinHash(BulletinPaieGenerateDTO dto) {
        try {
            String dataToHash = dto.getMatriculeEmploye() + ":" +
                    dto.getMois() + ":" +
                    dto.getSalaireNet() + ":" +
                    dto.getNomEntreprise();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(dataToHash.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération hash bulletin", e);
        }
    }

    private String generateBulletinSignature(BulletinPaieGenerateDTO dto, String hash) {
        try {
            String dataToSign = dto.getMatriculeEmploye() + ":" +
                    dto.getMois() + ":" +
                    hash.substring(0, 12) + ":" +
                    qrSecret;

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(dataToSign.getBytes());

            return bytesToHex(hashBytes).substring(0, 8);
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération signature bulletin", e);
        }
    }

    private String encryptQRData(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(getKeyBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("Erreur chiffrement QR", e);
        }
    }

    private byte[] getKeyBytes() {
        byte[] keyBytes = qrSecret.getBytes();
        byte[] result = new byte[16];

        for (int i = 0; i < Math.min(keyBytes.length, 16); i++) {
            result[i] = keyBytes[i];
        }

        return result;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public byte[] generateBulletinsPdf(List<BulletinPaieGenerateDTO> bulletins) throws Exception {
        if (bulletins == null || bulletins.isEmpty()) return new byte[0];

        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        merger.setDestinationStream(outputStream);

        for (BulletinPaieGenerateDTO dto : bulletins) {
            byte[] pdf = generateBulletinPdf(dto);
            merger.addSource(new RandomAccessReadBuffer(pdf));
        }

        merger.mergeDocuments(null);
        return outputStream.toByteArray();
    }

    private void appendRow(StringBuilder sb, String designation,
                           Double gain, Double retenueLegale,
                           Double autreRetenue, Double chargePatronale) {

        boolean empty = (isZero(gain) && isZero(retenueLegale)
                && isZero(autreRetenue) && isZero(chargePatronale));
        if (empty) return;

        boolean bold = "SALAIRE BRUT".equalsIgnoreCase(designation)
                || "TOTAL RETENUE".equalsIgnoreCase(designation)
                || "TOTAL CHARGE PATRONALE".equalsIgnoreCase(designation)
                || "TOTAL AUTRES RETENUES".equalsIgnoreCase(designation);

        sb.append("<tr>")
                .append("<td>").append(escape(designation)).append("</td>")
                .append("<td>")
                .append(bold ? "<strong>" : "")
                .append(formatMoney(gain))
                .append(bold ? "</strong>" : "")
                .append("</td>")
                .append("<td>")
                .append(bold ? "<strong>" : "")
                .append(formatMoney(retenueLegale))
                .append(bold ? "</strong>" : "")
                .append("</td>")
                .append("<td>")
                .append(bold ? "<strong>" : "")
                .append(formatMoney(autreRetenue))
                .append(bold ? "</strong>" : "")
                .append("</td>")
                .append("<td>")
                .append(bold ? "<strong>" : "")
                .append(formatMoney(chargePatronale))
                .append(bold ? "</strong>" : "")
                .append("</td>")
                .append("</tr>");
    }

    private boolean isZero(Double v) {
        return v == null || Math.abs(v) < 0.0000001;
    }

    private String formatMoney(Double v) {
        if (v == null) return "";
        return MONEY_FORMAT.format(v);
    }

    private String safe(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String buildQrImgTag(String secureData, int sizePx) throws Exception {
        if (secureData == null || secureData.isEmpty()) {
            return "";
        }

        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(secureData, BarcodeFormat.QR_CODE, sizePx, sizePx);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            String base64 = Base64.getEncoder().encodeToString(os.toByteArray());

            return "<img src=\"data:image/png;base64," + base64 + "\" alt=\"QR Code\" style=\"display:block;margin:10px auto;\"/>";

        } catch (Exception e) {
            log.error("Erreur génération QR code: {}", e.getMessage());
            return "<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==\" alt=\"QR Error\"/>";
        }
    }

    @Deprecated
    public static String generateQRCodeBase64(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
    }
}