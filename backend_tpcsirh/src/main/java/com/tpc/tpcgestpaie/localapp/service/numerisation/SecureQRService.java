package com.tpc.tpcgestpaie.localapp.service.numerisation;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.absence.DemandeAbsence;
import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SecureQRService {

    @Value("${app.security.qr-secret:mySuperSecretKeyForQREncryption123}")
    private String qrSecret;

    @Value("${app.security.qr-format-prefix:TPC}")
    private String qrFormatPrefix;

    /**
     * Génère un QR Code sécurisé avec format propriétaire
     */
    public String generateSecureQRData(EmployeeDocument document) {
        try {
            log.info("🔐 Génération QR Code pour document ID: {}", document.getId());

            // 1. Construire le format propriétaire
            String proprietaryData = buildProprietaryFormat(document);
            log.debug("📝 Format propriétaire: {}", proprietaryData);

            // 2. Chiffrer les données
            String encryptedData = encryptQRData(proprietaryData);
            log.debug("🔒 Données chiffrées (longueur: {})", encryptedData.length());

            // 3. Retourner avec préfixe
            String qrData = qrFormatPrefix + "v1:" + encryptedData;
            log.info("✅ QR Data généré (longueur totale: {})", qrData.length());

            return qrData;

        } catch (Exception e) {
            log.error("❌ Erreur génération QR sécurisé", e);
            throw new RuntimeException("Impossible de générer le QR Code sécurisé: " + e.getMessage());
        }
    }

    /**
     * Construit le format propriétaire que seule votre app comprend
     */
    private String buildProprietaryFormat(EmployeeDocument document) {
        StringBuilder sb = new StringBuilder();
        // Structure que seule votre app Flutter comprendra
        sb.append("DOC:").append(document.getId()).append(";");
        sb.append("REF:").append(document.getReference()).append(";");
        sb.append("EMP:")
                .append(document.getEmploye().getNom())
                .append(" ")
                .append(document.getEmploye().getPrenom())
                .append(";");
        sb.append("EMPMAT:").append(document.getEmploye().getMatricule()).append(";");
        sb.append("COMP:").append(document.getEmploye().getCompany().getName()).append(";");
        sb.append("PHONE:").append(document.getEmploye().getCompany().getPhone()).append(";");
        sb.append("ADDRESS:").append(document.getEmploye().getCompany().getAddress()).append(";");
        sb.append("CAT:")
                .append(document.getSubCategory().getName())
                .append(";");
        sb.append("DATENUM:").append(document.getSignatureTimestamp()).append(";");
        sb.append("SIGNATAIRE:").append(document.getSignedBy()).append(";");
        sb.append("HASH:").append(document.getFileHash().substring(0, 12)).append(";");
        sb.append("TS:").append(System.currentTimeMillis()).append(";");
        sb.append("SIG:").append(generateSignature(document));
        return sb.toString();
    }

    /**
     * Génère une signature pour vérification d'intégrité
     */
    private String generateSignature(EmployeeDocument document) {
        try {
            String dataToSign = document.getId() + ":" +
//                    document.getEmploye().getId() + ":" +
                    document.getEmploye().getMatricule() + ":" +
                    document.getFileHash().substring(0, 12) + ":" + // 🔥 IMPORTANT: Utiliser le même hash tronqué
                    qrSecret;

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(dataToSign.getBytes());

            // Prendre seulement les 8 premiers caractères
            return bytesToHex(hash).substring(0, 8);
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération signature", e);
        }
    }

    /**
     * Génère l'image du QR Code en Base64
     */
    public String generateQRImage(String secureData) {
        try {
            log.info("📸 Génération image QR Code (données: {} caractères)", secureData.length());

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // 🔥 AUGMENTER LA TAILLE POUR PLUS DE DONNÉES
            BitMatrix bitMatrix = qrCodeWriter.encode(secureData, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            log.info("✅ Image QR générée (taille: {} bytes)", outputStream.size());

            return base64Image;

        } catch (Exception e) {
            log.warn("⚠️ Erreur génération image QR, utilisation fallback", e);
            return generateSimpleQRBase64();
        }
    }

    /**
     * Valide et déchiffre un QR Code scanné
     */
    public Map<String, Object> validateAndDecryptQR(String qrData) {
        log.info("🔍 Validation QR Code...");

        // Vérifier le format
        if (!qrData.startsWith(qrFormatPrefix + "v1:")) {
            log.error("❌ Format QR non autorisé: {}", qrData.substring(0, Math.min(10, qrData.length())));
            throw new SecurityException("Format QR non autorisé - App tierce détectée");
        }

        try {
            // Extraire et déchiffrer
            String encryptedData = qrData.substring((qrFormatPrefix + "v1:").length());
            String decryptedData = decryptQRData(encryptedData);
            log.debug("🔓 Données déchiffrées: {}", decryptedData);

            // Parser le format propriétaire
            Map<String, Object> result = parseProprietaryFormat(decryptedData);
            log.info("✅ QR Code validé avec succès");

            return result;

        } catch (Exception e) {
            log.error("❌ Erreur validation QR: {}", e.getMessage());
            throw new SecurityException("QR Code corrompu ou lecture par app tierce: " + e.getMessage());
        }
    }

    /**
     * Parse le format propriétaire
     */
    private Map<String, Object> parseProprietaryFormat(String proprietaryData) {
        Map<String, Object> result = new HashMap<>();

        try {
            String[] parts = proprietaryData.split(";");
            for (String part : parts) {
                String[] keyValue = part.split(":", 2);
                if (keyValue.length == 2) {
                    result.put(keyValue[0], keyValue[1]);
                }
            }

            // Vérifier la signature
            String docId = (String) result.get("DOC");
            String empId = (String) result.get("EMP");
            String hash = (String) result.get("HASH");
            String receivedSig = (String) result.get("SIG");

            String calculatedSig = generateSignatureForValidation(docId, empId, hash);

            log.debug("🔐 Signature reçue: {}", receivedSig);
            log.debug("🔐 Signature calculée: {}", calculatedSig);

            if (!receivedSig.equals(calculatedSig)) {
                throw new SecurityException("Signature QR invalide");
            }

            return result;

        } catch (Exception e) {
            throw new SecurityException("Format propriétaire invalide: " + e.getMessage());
        }
    }

    /**
     * Génère une signature pour validation
     */
    private String generateSignatureForValidation(String docId, String empId, String hash) {
        try {
            String dataToSign = docId + ":" + empId + ":" + hash + ":" + qrSecret;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(dataToSign.getBytes());
            return bytesToHex(hashBytes).substring(0, 8);
        } catch (Exception e) {
            throw new RuntimeException("Erreur validation signature", e);
        }
    }

    /**
     * Chiffre les données avec AES
     */
    private String encryptQRData(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(getKeyBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
            String result = Base64.getEncoder().encodeToString(encrypted);

            log.debug("🔒 Chiffrement - Input: {} bytes, Output: {} bytes", data.length(), encrypted.length);

            return result;

        } catch (Exception e) {
            log.error("❌ Erreur chiffrement", e);
            throw new RuntimeException("Erreur chiffrement QR", e);
        }
    }

    /**
     * Déchiffre les données AES
     */
    private String decryptQRData(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(getKeyBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);

            return new String(decrypted, "UTF-8").trim();

        } catch (Exception e) {
            log.error("❌ Erreur déchiffrement", e);
            throw new SecurityException("Erreur déchiffrement QR: " + e.getMessage());
        }
    }

    /**
     * Prépare la clé de chiffrement (16 bytes pour AES-128)
     */
    private byte[] getKeyBytes() {
        byte[] keyBytes = qrSecret.getBytes();
        byte[] result = new byte[16]; // 128 bits

        for (int i = 0; i < Math.min(keyBytes.length, 16); i++) {
            result[i] = keyBytes[i];
        }

        return result;
    }

    /**
     * Convertit des bytes en hexadécimal
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * QR Code fallback simple (1x1 pixel blanc)
     */
    String generateSimpleQRBase64() {
        return "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
    }


    /**
     * Génère un QR Code sécurisé pour un contrat
     */
    public String generateSecureQRDataForContrat(ContratEmploye contrat) {
        try {
            log.info("🔐 Génération QR Code pour contrat: {}", contrat.getNumeroContrat());

            // 1. Construire le format propriétaire pour CONTRAT
            String proprietaryData = buildProprietaryFormatForContrat(contrat);
            log.debug("📝 Format propriétaire contrat: {}", proprietaryData);

            // 2. Chiffrer les données
            String encryptedData = encryptQRData(proprietaryData);
            log.debug("🔒 Données chiffrées (longueur: {})", encryptedData.length());

            // 3. Retourner avec préfixe
            String qrData = qrFormatPrefix + "v1:" + encryptedData;
            log.info("✅ QR Data contrat généré (longueur totale: {})", qrData.length());

            return qrData;

        } catch (Exception e) {
            log.error("❌ Erreur génération QR sécurisé pour contrat", e);
            throw new RuntimeException("Impossible de générer le QR Code sécurisé: " + e.getMessage());
        }
    }

    /**
     * Construit le format propriétaire pour un CONTRAT
     * Structure que seule votre app Flutter comprendra
     */
    private String buildProprietaryFormatForContrat(ContratEmploye contrat) {
        StringBuilder sb = new StringBuilder();

        // TYPE: Identifier qu'il s'agit d'un CONTRAT et non d'un DOCUMENT
        sb.append("TYPE:CONTRAT;");

        // CONTRAT INFO
        sb.append("CONTRAT_ID:").append(contrat.getId()).append(";");
        sb.append("NUMERO:").append(contrat.getNumeroContrat()).append(";");
        sb.append("TYPE_CONTRAT:").append(contrat.getType_contrat()).append(";");

        // EMPLOYÉ
        sb.append("EMP:")
                .append(contrat.getEmploye().getNom())
                .append(" ")
                .append(contrat.getEmploye().getPrenom())
                .append(";");
        sb.append("EMPMAT:").append(contrat.getEmploye().getMatricule()).append(";");
        sb.append("EMPID:").append(contrat.getEmploye().getId()).append(";");

        // ENTREPRISE
        sb.append("COMP:").append(contrat.getCompany().getName()).append(";");
        sb.append("PHONE:").append(contrat.getCompany().getPhone()).append(";");
        sb.append("ADDRESS:").append(contrat.getCompany().getAddress()).append(";");
        sb.append("DG:").append(contrat.getCompany().getDirectorName()).append(";");

        // DATES
        if (contrat.getDate_debut() != null) {
            sb.append("DATE_DEBUT:").append(contrat.getDate_debut().toString()).append(";");
        }
        if (contrat.getDate_fin() != null) {
            sb.append("DATE_FIN:").append(contrat.getDate_fin().toString()).append(";");
        }

        // POSTE & SALAIRE
        sb.append("POSTE:").append(contrat.getPoste().getLibelle()).append(";");
        sb.append("SALAIRE:").append(contrat.getSalaire_brut()).append(";");

        // HASH pour intégrité
        String contratHash = generateContratHash(contrat);
        sb.append("HASH:").append(contratHash.substring(0, 12)).append(";");

        // TIMESTAMP
        sb.append("TS:").append(System.currentTimeMillis()).append(";");

        // SIGNATURE
        sb.append("SIG:").append(generateContratSignature(contrat, contratHash));

        return sb.toString();
    }

    /**
     * Génère un hash unique pour le contrat
     */
    private String generateContratHash(ContratEmploye contrat) {
        try {
            String dataToHash = contrat.getId() + ":" +
                    contrat.getNumeroContrat() + ":" +
                    contrat.getEmploye().getMatricule() + ":" +
                    contrat.getDate_debut();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(dataToHash.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération hash contrat", e);
        }
    }

    /**
     * Génère une signature pour le contrat
     */
    private String generateContratSignature(ContratEmploye contrat, String hash) {
        try {
            String dataToSign = contrat.getId() + ":" +
                    contrat.getEmploye().getMatricule() + ":" +
                    hash.substring(0, 12) + ":" +
                    qrSecret;

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(dataToSign.getBytes());

            // Prendre seulement les 8 premiers caractères
            return bytesToHex(hashBytes).substring(0, 8);
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération signature contrat", e);
        }
    }

    /**
     * Génère un QR Code sécurisé pour une demande d'absence
     */
    public String generateSecureQRDataForDemandeAbsence(DemandeAbsence demande) {
        try {
            log.info("🔐 Génération QR Code pour demande d'absence ID: {}", demande.getId());

            // 1. Construire le format propriétaire pour DEMANDE_ABSENCE
            String proprietaryData = buildProprietaryFormatForDemandeAbsence(demande);
            log.debug("📝 Format propriétaire demande absence: {}", proprietaryData);

            // 2. Chiffrer les données
            String encryptedData = encryptQRData(proprietaryData);
            log.debug("🔒 Données chiffrées (longueur: {})", encryptedData.length());

            // 3. Retourner avec préfixe
            String qrData = qrFormatPrefix + "v1:" + encryptedData;
            log.info("✅ QR Data demande absence généré (longueur totale: {})", qrData.length());

            return qrData;

        } catch (Exception e) {
            log.error("❌ Erreur génération QR sécurisé pour demande d'absence", e);
            throw new RuntimeException("Impossible de générer le QR Code sécurisé: " + e.getMessage());
        }
    }

    /**
     * Construit le format propriétaire pour une DEMANDE_ABSENCE
     * Structure que seule votre app Flutter comprendra
     */
    private String buildProprietaryFormatForDemandeAbsence(DemandeAbsence demande) {
        StringBuilder sb = new StringBuilder();

        // TYPE: Identifier qu'il s'agit d'une DEMANDE_ABSENCE
        sb.append("TYPE:DEMANDE_ABSENCE;");

        // INFOS DE BASE
        sb.append("ID:").append(demande.getId()).append(";");
//        sb.append("LIBELLE:").append(demande.getLibelle() != null ? demande.getLibelle() : "").append(";");

        // STATUT
        sb.append("STATUT:").append(demande.getStatut().toString()).append(";");

        // EMPLOYÉ DEMANDEUR
        Employe demandeur = demande.getEmployeDemandeur();
        if (demandeur != null) {
            sb.append("EMP:")
                    .append(demandeur.getNom())
                    .append(" ")
                    .append(demandeur.getPrenom())
                    .append(";");
            sb.append("EMPMAT:").append(demandeur.getMatricule()).append(";");
            sb.append("EMPID:").append(demandeur.getId()).append(";");
        }

        // CONTRAT (si disponible)
        ContratEmploye contrat = demande.getContratEmploye();
        if (contrat != null) {
            sb.append("CONTRAT_ID:").append(contrat.getId()).append(";");
            sb.append("NUMERO_CONTRAT:").append(contrat.getNumeroContrat()).append(";");

            // ENTREPRISE depuis le contrat
            Company company = contrat.getCompany();
            if (company != null) {
                sb.append("COMP:").append(company.getName()).append(";");
                sb.append("PHONE:").append(company.getPhone() != null ? company.getPhone() : "").append(";");
                sb.append("ADDRESS:").append(company.getAddress() != null ? company.getAddress() : "").append(";");
                sb.append("DG:").append(company.getDirectorName() != null ? company.getDirectorName() : "").append(";");
            }
        } else {
            // Fallback sur l'entreprise depuis l'employé ou autre source
            sb.append("COMPANY_ID:").append(demande.getCompanyId() != null ? demande.getCompanyId() : "").append(";");
        }

        // TYPE ET MOTIF D'ABSENCE
        if (demande.getTypeAbsence() != null) {
            sb.append("TYPE_ABSENCE:").append(demande.getTypeAbsence().getLibelle()).append(";");
        }


        sb.append("MOTIF_DEMANDE:").append(demande.getMotifDemande() != null ? demande.getMotifDemande() : "").append(";");

        // PÉRIODE D'ABSENCE
        if (demande.getDateDebut() != null) {
            sb.append("DATE_DEBUT:").append(demande.getDateDebut().toString()).append(";");
        }

        if (demande.getDateFin() != null) {
            sb.append("DATE_FIN:").append(demande.getDateFin().toString()).append(";");
        }

        sb.append("NB_JOURS:").append(demande.getNombreJours() != null ? demande.getNombreJours() : 0).append(";");

        // HIÉRARCHIE DE VALIDATION



        // ANNULATION
        if (demande.getDateAnnulation() != null) {
            sb.append("DATE_ANNULATION:").append(demande.getDateAnnulation().toString()).append(";");
        }

        if (demande.getMotifAnnulation() != null) {
            sb.append("MOTIF_ANNULATION:").append(demande.getMotifAnnulation()).append(";");
        }

        // MÉTADONNÉES
        if (demande.getCreatedAt() != null) {
            sb.append("CREATED_AT:").append(demande.getCreatedAt().toString()).append(";");
        }

        sb.append("TS:").append(System.currentTimeMillis()).append(";");

        // HASH pour intégrité
        String demandeHash = generateDemandeAbsenceHash(demande);
        sb.append("HASH:").append(demandeHash.substring(0, 12)).append(";");

        // SIGNATURE
        sb.append("SIG:").append(generateDemandeAbsenceSignature(demande, demandeHash));

        return sb.toString();
    }

    /**
     * Génère un hash unique pour la demande d'absence
     */
    private String generateDemandeAbsenceHash(DemandeAbsence demande) {
        try {
            String dataToHash = demande.getId() + ":" +
                    (demande.getEmployeDemandeur() != null ? demande.getEmployeDemandeur().getMatricule() : "") + ":" +
                    (demande.getDateDebut() != null ? demande.getDateDebut().toString() : "") + ":" +
                    (demande.getDateFin() != null ? demande.getDateFin().toString() : "") + ":" +
                    demande.getStatut().toString();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(dataToHash.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération hash demande absence", e);
        }
    }

    /**
     * Génère une signature pour la demande d'absence
     */
    private String generateDemandeAbsenceSignature(DemandeAbsence demande, String hash) {
        try {
            String matricule = demande.getEmployeDemandeur() != null ?
                    demande.getEmployeDemandeur().getMatricule() : "UNKNOWN";

            String dataToSign = demande.getId() + ":" +
                    matricule + ":" +
                    hash.substring(0, 12) + ":" +
                    qrSecret;

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(dataToSign.getBytes());

            // Prendre seulement les 8 premiers caractères
            return bytesToHex(hashBytes).substring(0, 8);
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération signature demande absence", e);
        }
    }
}