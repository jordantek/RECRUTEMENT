package com.tpc.tpcgestpaie.localapp.controller.numerisation;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.DocumentProcessRequest;
import com.tpc.tpcgestpaie.localapp.dto.numerisation.DocumentProcessResponse;
import com.tpc.tpcgestpaie.localapp.dto.numerisation.QRValidationRequest;
import com.tpc.tpcgestpaie.localapp.dto.numerisation.DocumentScanResult;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentQRCode;
import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.DocumentQRCodeRepository;
import com.tpc.tpcgestpaie.localapp.service.numerisation.DiagnosticService;
import com.tpc.tpcgestpaie.localapp.service.numerisation.JavaDocumentDigitalizationService;
import com.tpc.tpcgestpaie.localapp.service.numerisation.SecureQRService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/digitalization")
@Slf4j
public class DocumentDigitalizationController {

    private final JavaDocumentDigitalizationService digitalizationService;
    private final DocumentQRCodeRepository documentQRCodeRepository;
    private final SecureQRService secureQRService;
    private final DiagnosticService diagnosticService;

    public DocumentDigitalizationController(
            JavaDocumentDigitalizationService digitalizationService,
            DocumentQRCodeRepository documentQRCodeRepository,
            SecureQRService secureQRService,
            DiagnosticService diagnosticService) {
        this.digitalizationService = digitalizationService;
        this.documentQRCodeRepository = documentQRCodeRepository;
        this.secureQRService = secureQRService;
        this.diagnosticService = diagnosticService;
    }

    /**
     * 🔥 ENDPOINT PRINCIPAL - Upload et traitement de document avec TOUS les champs
     *
     * @param file Fichier à uploader (PDF, Image, etc.)
     * @param employeId ID de l'employé
     * @param subCategoryId ID de la catégorie du document
     * @param replaceSignatures Supprimer les signatures existantes (default: true)
     * @param generateQR Générer un QR Code sécurisé (default: true)
     * @param reference Référence personnalisée (optionnel, auto-généré si absent)
     * @param signedBy Nom/Email de la personne qui signe (optionnel)
     * @param expirationDateStr Date d'expiration format YYYY-MM-DD (optionnel)
     * @param isVerified Document déjà vérifié (default: false)
     * @param notes Notes additionnelles (optionnel, non stocké)
     * @return DocumentProcessResponse avec toutes les infos du document traité
     */
    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> processDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("employeId") Long employeId,
            @RequestParam("subCategoryId") Long subCategoryId,
            @RequestParam(value = "replaceSignatures", defaultValue = "true") Boolean replaceSignatures,
            @RequestParam(value = "generateQR", defaultValue = "true") Boolean generateQR,

            // 🔥 NOUVEAUX PARAMÈTRES ADDITIONNELS
            @RequestParam(value = "reference", required = false) String reference,
            @RequestParam(value = "signedBy", required = false) String signedBy,
            @RequestParam(value = "expirationDate", required = false) String expirationDateStr,
            @RequestParam(value = "isVerified", defaultValue = "true") Boolean isVerified,
            @RequestParam(value = "notes", required = false) String notes) {

        log.info("=== DÉMARRAGE TRAITEMENT DOCUMENT ===");
        log.info("Fichier: {} ({} bytes)", file.getOriginalFilename(), file.getSize());
        log.info("EmployeId: {}, SubCategoryId: {}", employeId, subCategoryId);
        log.info("Référence: {}, Signé par: {}, Vérifié: {}", reference, signedBy, isVerified);

        try {
            // Construire la requête avec TOUS les champs
            DocumentProcessRequest request = new DocumentProcessRequest();
            request.setEmployeId(employeId);
            request.setSubCategoryId(subCategoryId);
            request.setReplaceSignatures(replaceSignatures);
            request.setGenerateQR(generateQR);

            // 🔥 AJOUTER LES NOUVEAUX CHAMPS
            request.setReference(reference);
            request.setSignedBy(signedBy);
            request.setIsVerified(isVerified);
//            request.setNotes(notes);

            // Parser la date d'expiration si fournie
            if (expirationDateStr != null && !expirationDateStr.trim().isEmpty()) {
                try {
                    LocalDate expirationDate = LocalDate.parse(expirationDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                    request.setExpirationDate(expirationDate);
                    log.info("Date d'expiration: {}", expirationDate);
                } catch (Exception e) {
                    log.warn("⚠️ Format date d'expiration invalide: {} - Document créé sans date d'expiration", expirationDateStr);
                }
            }

            // Traiter le document
            DocumentProcessResponse response = digitalizationService.processAndDigitalizeDocument(file, request);

            log.info("✅ Traitement réussi - Document ID: {}, Référence: {}",
                    response.getDocumentId(),
                    reference != null ? reference : "Auto-générée");

            return ResponseEntity.ok(ApiResponse.success("Document dématérialisé avec succès", response));

        } catch (RuntimeException e) {
            log.error("❌ Erreur traitement document: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Erreur lors du traitement: " + e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Erreur technique traitement document: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erreur technique: " + e.getMessage()));
        }
    }

    /**
     * Validation d'un QR Code scanné depuis l'application mobile
     *
     * @param validationRequest Contient le QR data et le device ID
     * @return DocumentScanResult avec les infos du document si valide
     */
    @PostMapping("/validate-qr")
    public ResponseEntity<ApiResponse<?>> validateQRCode(@RequestBody QRValidationRequest validationRequest) {
        log.info("🔍 Validation QR Code - Device: {}", validationRequest.getDeviceId());

        try {
            // Déchiffrer et valider le QR Code
            Map<String, Object> qrData = secureQRService.validateAndDecryptQR(validationRequest.getQrData());

            Long documentId = Long.valueOf(qrData.get("DOC").toString());
            String expectedHash = qrData.get("HASH").toString();

            // Vérifier que le document existe
            Optional<DocumentQRCode> qrCodeOpt = documentQRCodeRepository.findByDocumentId(documentId);
            if (qrCodeOpt.isEmpty()) {
                log.warn("⚠️ Document non trouvé: {}", documentId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Document non trouvé"));
            }

            DocumentQRCode qrCode = qrCodeOpt.get();
            EmployeeDocument document = qrCode.getDocument();

            // Vérifier l'intégrité (comparer les 12 premiers caractères)
            String documentHashShort = document.getFileHash().substring(0, 12);
            if (!expectedHash.equals(documentHashShort)) {
                log.warn("⚠️ Hash mismatch - Document possiblement altéré: {}", documentId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Document corrompu ou altéré"));
            }

            // Construire la réponse enrichie
            DocumentScanResult scanResult = DocumentScanResult.builder()
                    .valid(true)
                    .documentId(document.getId().toString())
                    .digitalId(qrCode.getDigitalId())
                    .employeId(document.getEmploye().getId())
                    .employeNom(document.getEmploye().getNom())
                    .employePrenom(document.getEmploye().getPrenom())
                    .documentType(document.getDocumentType().name())
                    .originalFileName(document.getFileName())
                    .uploadedAt(document.getCreatedAt())
                    .processedAt(qrCode.getGeneratedAt())
                    .securityLevel("SECURE_JAVA")
                    .remainingScans(100)
                    .validationMessage("Document authentique et intègre")
                    .build();

            // Incrémenter le compteur de vérifications
            document.setVerificationCount(document.getVerificationCount() + 1);
            // Note: Sauvegarder si nécessaire via le repository

            log.info("✅ QR Code validé - Document: {}, Référence: {}, Vérifications: {}",
                    documentId, document.getReference(), document.getVerificationCount());

            return ResponseEntity.ok(ApiResponse.success("QR Code valide", scanResult));

        } catch (SecurityException e) {
            log.warn("❌ QR Code invalide: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("QR Code invalide: " + e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Erreur validation QR: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erreur technique lors de la validation"));
        }
    }

    /**
     * Récupère l'image du QR Code d'un document
     *
     * @param digitalId ID digital unique du document
     * @return Image QR Code en base64
     */
    @GetMapping("/document/{digitalId}/qr")
    public ResponseEntity<ApiResponse<?>> getQRCode(@PathVariable String digitalId) {
        log.info("📷 Récupération QR Code: {}", digitalId);

        Optional<DocumentQRCode> qrCodeOpt = documentQRCodeRepository.findByDigitalId(digitalId);

        if (qrCodeOpt.isPresent()) {
            DocumentQRCode qr = qrCodeOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("qrImage", qr.getQrImageBase64());
            response.put("digitalId", qr.getDigitalId());
            response.put("documentId", qr.getDocument().getId());
            response.put("reference", qr.getDocument().getReference());
            response.put("generatedAt", qr.getGeneratedAt());

            return ResponseEntity.ok(ApiResponse.success("QR Code récupéré", response));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("QR Code non trouvé"));
    }

    /**
     * Récupère toutes les informations d'un document via son digitalId
     *
     * @param digitalId ID digital unique du document
     * @return Informations complètes du document
     */
    @GetMapping("/document/{digitalId}/info")
    public ResponseEntity<ApiResponse<?>> getDigitalInfo(@PathVariable String digitalId) {
        log.info("📄 Récupération info digitale: {}", digitalId);

        Optional<DocumentQRCode> qrCodeOpt = documentQRCodeRepository.findByDigitalId(digitalId);

        if (qrCodeOpt.isPresent()) {
            DocumentQRCode qr = qrCodeOpt.get();
            EmployeeDocument doc = qr.getDocument();

            Map<String, Object> info = new HashMap<>();

            // Informations document
            info.put("digitalId", qr.getDigitalId());
            info.put("documentId", doc.getId());
            info.put("reference", doc.getReference());
            info.put("fileName", doc.getFileName());
            info.put("documentType", doc.getDocumentType());
            info.put("mimeType", doc.getMimeType());
            info.put("fileSize", doc.getFileSize());

            // Informations employé
            info.put("employeId", doc.getEmploye().getId());
            info.put("employeNom", doc.getEmploye().getNom());
            info.put("employePrenom", doc.getEmploye().getPrenom());
            info.put("employeMatricule", doc.getEmploye().getMatricule());

            // Informations catégorie
            info.put("categoryId", doc.getSubCategory().getId());
            info.put("categoryName", doc.getSubCategory().getName());

            // Informations temporelles
            info.put("uploadedAt", doc.getCreatedAt());
            info.put("expirationDate", doc.getExpirationDate());
            info.put("qrGeneratedAt", qr.getGeneratedAt());

            // Informations de sécurité et vérification
            info.put("fileHash", doc.getFileHash());
            info.put("contentHash", doc.getContentHash());
            info.put("isVerified", doc.getIsVerified());
            info.put("signedBy", doc.getSignedBy());
            info.put("signatureTimestamp", doc.getSignatureTimestamp());
            info.put("verificationCount", doc.getVerificationCount());
            info.put("isActive", doc.getIsActive());

            log.info("✅ Informations récupérées - Document: {}, Référence: {}", doc.getId(), doc.getReference());
            return ResponseEntity.ok(ApiResponse.success("Informations récupérées", info));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Informations non trouvées"));
    }

    /**
     * Récupère le statut du système de dématérialisation
     *
     * @return Statut des services
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<?>> getSystemStatus() {
        try {
            String status = digitalizationService.getSystemStatus();
            return ResponseEntity.ok(ApiResponse.success("Statut système", status));
        } catch (Exception e) {
            log.error("❌ Erreur récupération statut: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erreur récupération statut"));
        }
    }

    /**
     * Health check du service
     *
     * @return Statut UP/DOWN avec version
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Document Digitalization");
        response.put("version", "2.0");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Initialise les répertoires de stockage
     *
     * @return Résultat de l'initialisation
     */
    @PostMapping("/initialize-storage")
    public ResponseEntity<ApiResponse<?>> initializeStorage() {
        try {
            log.info("🔧 Initialisation du stockage...");
            diagnosticService.initializeStorage();
            log.info("✅ Stockage initialisé avec succès");
            return ResponseEntity.ok(ApiResponse.success("Stockage initialisé avec succès", null));
        } catch (Exception e) {
            log.error("❌ Erreur initialisation stockage: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erreur initialisation stockage: " + e.getMessage()));
        }
    }

    /**
     * Diagnostic de santé du stockage
     *
     * @return Rapport détaillé de l'état du stockage
     */
    @GetMapping("/storage-health")
    public ResponseEntity<ApiResponse<?>> getStorageHealth() {
        try {
            log.info("🔍 Diagnostic du stockage...");
            String healthReport = diagnosticService.checkStorageHealth();
            return ResponseEntity.ok(ApiResponse.success("Diagnostic stockage", healthReport));
        } catch (Exception e) {
            log.error("❌ Erreur diagnostic: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Erreur diagnostic: " + e.getMessage()));
        }
    }
}