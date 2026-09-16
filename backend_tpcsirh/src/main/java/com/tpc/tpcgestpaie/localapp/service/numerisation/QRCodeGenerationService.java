package com.tpc.tpcgestpaie.localapp.service.numerisation;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.QRCodeVerificationDTO;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentQRCode;
import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.DocumentQRCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class QRCodeGenerationService {

    @Autowired
    private DocumentQRCodeRepository qrCodeRepository;

    @Transactional
    public DocumentQRCode generateQRCodeForDocument(EmployeeDocument document) {
        String digitalId = generateDigitalDocumentCode(document);
        String qrData = generateQRCodeData(document, digitalId);
        String qrImageBase64 = generateQRCodeImageBase64(qrData);

        DocumentQRCode qrCode = new DocumentQRCode();
        qrCode.setDocument(document);
        qrCode.setDigitalId(digitalId);
        qrCode.setQrData(qrData);
        qrCode.setQrImageBase64(qrImageBase64);
        // generatedAt est automatiquement setté par @PrePersist

        return qrCodeRepository.save(qrCode);
    }

    @Transactional
    public void generateQRCodeForDocument(Long documentId) {
        // Implémentation si besoin de générer par ID
        log.info("Génération QR Code pour le document {}", documentId);
        // Tu devras récupérer le document par son ID d'abord
    }

    private String generateDigitalDocumentCode(EmployeeDocument document) {
        return String.format("DIGI-%s-%s-%d-%s",
                document.getEmploye().getMatricule(),
                document.getSubCategory().getName().toUpperCase().replace(" ", "_"),
                document.getId(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );
    }

    private String generateQRCodeData(EmployeeDocument document, String digitalId) {
        // Créer les données structurées pour le QR Code
        QRCodeData qrData = new QRCodeData();
        qrData.setDocumentId(digitalId);
        qrData.setEmployeeId(document.getEmploye().getId());
        qrData.setEmployeeMatricule(document.getEmploye().getMatricule());
        qrData.setEmployeeName(document.getEmploye().getNom() + " " + document.getEmploye().getPrenom());
        qrData.setDocumentType(document.getSubCategory().getName());
        qrData.setFileName(document.getFileName());
        qrData.setGeneratedAt(LocalDateTime.now().toString());
        qrData.setValidationHash(generateValidationHash(document, digitalId));

        // Convertir en JSON
        return convertToJson(qrData);
    }

    private String generateQRCodeImageBase64(String qrData) {
        try {
            // Utiliser une librairie QR Code comme ZXing ou QRGen
            // Pour l'instant, retourner une image base64 simulée
            log.info("Génération QR Code pour les données: {}", qrData);

            // Image PNG 1x1 blanche en base64 (simulation)
            return "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";

        } catch (Exception e) {
            log.error("Erreur génération QR Code image", e);
            return "";
        }
    }

    private String generateValidationHash(EmployeeDocument document, String digitalId) {
        String data = digitalId +
                document.getId() +
                document.getEmploye().getId() +
                document.getCreatedAt().toString() +
                "TPC_SECRET_SALT";

        return Integer.toHexString(data.hashCode());
    }

    private String convertToJson(QRCodeData qrData) {
        // Conversion simple en JSON
        return String.format(
                "{\"documentId\":\"%s\",\"employeeId\":%d,\"employeeMatricule\":\"%s\",\"employeeName\":\"%s\",\"documentType\":\"%s\",\"fileName\":\"%s\",\"generatedAt\":\"%s\",\"validationHash\":\"%s\"}",
                qrData.getDocumentId(),
                qrData.getEmployeeId(),
                qrData.getEmployeeMatricule(),
                qrData.getEmployeeName(),
                qrData.getDocumentType(),
                qrData.getFileName(),
                qrData.getGeneratedAt(),
                qrData.getValidationHash()
        );
    }

    public QRCodeVerificationDTO verifyQRCode(String digitalId) {
        DocumentQRCode qrCode = qrCodeRepository.findByDigitalId(digitalId)
                .orElseThrow(() -> new RuntimeException("QR Code invalide ou non trouvé"));

        EmployeeDocument document = qrCode.getDocument();

        QRCodeVerificationDTO verification = new QRCodeVerificationDTO();
        verification.setDigitalId(digitalId);
        verification.setValid(true);
        verification.setDocumentName(document.getFileName());
        verification.setEmployeeFullName(document.getEmploye().getNom() + " " + document.getEmploye().getPrenom());
        verification.setEmployeeMatricule(document.getEmploye().getMatricule());
        verification.setGenerationDate(qrCode.getGeneratedAt());
        verification.setDocumentType(document.getSubCategory().getName());

        // Vérification d'intégrité des données
        String calculatedHash = generateValidationHash(document, digitalId);
        String storedHash = extractValidationHashFromQRData(qrCode.getQrData());
        verification.setIntegrityValid(calculatedHash.equals(storedHash));

        log.info("Vérification QR Code: {}", verification.isValid() ? "VALIDE" : "INVALIDE");

        return verification;
    }

    private String extractValidationHashFromQRData(String qrData) {
        try {
            // Extraire le hash des données QR (simplifié)
            if (qrData.contains("\"validationHash\":\"")) {
                int start = qrData.indexOf("\"validationHash\":\"") + 17;
                int end = qrData.indexOf("\"", start);
                return qrData.substring(start, end);
            }
        } catch (Exception e) {
            log.error("Erreur extraction validation hash", e);
        }
        return "";
    }

    public byte[] getQRCodeImage(String digitalId) {
        DocumentQRCode qrCode = qrCodeRepository.findByDigitalId(digitalId)
                .orElseThrow(() -> new RuntimeException("QR Code non trouvé"));

        // Convertir base64 en bytes
        if (qrCode.getQrImageBase64() != null && !qrCode.getQrImageBase64().isEmpty()) {
            try {
                return java.util.Base64.getDecoder().decode(qrCode.getQrImageBase64());
            } catch (Exception e) {
                log.error("Erreur décodage QR Code image", e);
            }
        }

        // Fallback: image vide
        log.warn("QR Code image non disponible pour {}", digitalId);
        return new byte[0];
    }

    // Classe interne pour structurer les données QR
    private static class QRCodeData {
        private String documentId;
        private Long employeeId;
        private String employeeMatricule;
        private String employeeName;
        private String documentType;
        private String fileName;
        private String generatedAt;
        private String validationHash;

        // Getters et setters
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }

        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

        public String getEmployeeMatricule() { return employeeMatricule; }
        public void setEmployeeMatricule(String employeeMatricule) { this.employeeMatricule = employeeMatricule; }

        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

        public String getDocumentType() { return documentType; }
        public void setDocumentType(String documentType) { this.documentType = documentType; }

        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }

        public String getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }

        public String getValidationHash() { return validationHash; }
        public void setValidationHash(String validationHash) { this.validationHash = validationHash; }
    }
}