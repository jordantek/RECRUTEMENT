package com.tpc.tpcgestpaie.localapp.service.numerisation;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.DocumentProcessRequest;
import com.tpc.tpcgestpaie.localapp.dto.numerisation.DocumentProcessResponse;
import com.tpc.tpcgestpaie.localapp.dto.numerisation.ProcessedDocument;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentQRCode;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentSubCategory;
import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.DocumentQRCodeRepository;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.DocumentSubCategoryRepository;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.EmployeeDocumentRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class JavaDocumentDigitalizationService {

    private final EmployeeDocumentRepository documentRepository;
    private final DocumentQRCodeRepository qrCodeRepository;
    private final EmployeRepository employeRepository;
    private final DocumentSubCategoryRepository subCategoryRepository;
    private final SecureQRService secureQRService;
    private final DocumentProcessingService documentProcessingService;
    private final NumerisationFileStorageService fileStorageService;

    public JavaDocumentDigitalizationService(
            EmployeeDocumentRepository documentRepository,
            DocumentQRCodeRepository qrCodeRepository,
            EmployeRepository employeRepository,
            DocumentSubCategoryRepository subCategoryRepository,
            SecureQRService secureQRService,
            DocumentProcessingService documentProcessingService,
            NumerisationFileStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.qrCodeRepository = qrCodeRepository;
        this.employeRepository = employeRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.secureQRService = secureQRService;
        this.documentProcessingService = documentProcessingService;
        this.fileStorageService = fileStorageService;
    }

    public DocumentProcessResponse processAndDigitalizeDocument(
            MultipartFile file, DocumentProcessRequest request) {

        log.info("Début digitalisation 100% Java: {}", file.getOriginalFilename());

        try {
            // 1. VALIDATIONS
            Employe employe = employeRepository.findById(request.getEmployeId())
                    .orElseThrow(() -> new RuntimeException("Employé non trouvé ID: " + request.getEmployeId()));

            if (employe.getMatricule() == null || employe.getMatricule().trim().isEmpty()) {
                throw new RuntimeException("L'employé doit avoir un matricule pour générer un document sécurisé");
            }

            DocumentSubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée ID: " + request.getSubCategoryId()));

            // 2. CRÉER UN DOCUMENT TEMPORAIRE (non sauvegardé en BDD)
            EmployeeDocument tempDoc = createTempDocument(file, employe, subCategory, request);
            log.info("Document temporaire créé pour traitement");

            // 3. TRAITEMENT 100% JAVA (directement depuis le MultipartFile)
            ProcessedDocument processedDoc = documentProcessingService.processDocumentFromUpload(
                    file, tempDoc, request.getReplaceSignatures(), request.getGenerateQR()
            );

            // 4. SAUVEGARDE UNIQUEMENT DU DOCUMENT RECONSTRUIT
            EmployeeDocument reconstructedDoc = saveReconstructedDocument(
                    tempDoc, processedDoc, employe, subCategory, request
            );
            log.info("Document reconstruit sauvegardé: {} - Référence: {}",
                    reconstructedDoc.getId(), reconstructedDoc.getReference());

            // 5. GÉNÉRATION QR CODE SÉCURISÉ
            DocumentQRCode qrCode = generateSecureQRCode(reconstructedDoc, processedDoc);
            log.info("QR Code généré: {} pour document: {}",
                    qrCode.getDigitalId(), reconstructedDoc.getReference());

            // 6. CONSTRUCTION RÉPONSE
            DocumentProcessResponse response = buildResponse(
                    reconstructedDoc, qrCode, processedDoc
            );

            log.info("✅ Digitalisation terminée - SANS stockage de l'original");

            return response;

        } catch (Exception e) {
            log.error("❌ Erreur digitalisation Java: {}", e.getMessage(), e);
            throw new RuntimeException("Échec digitalisation: " + e.getMessage());
        }
    }

    /**
     * 🔥 CRÉER UN DOCUMENT TEMPORAIRE (sans BDD)
     */
    private EmployeeDocument createTempDocument(
            MultipartFile file,
            Employe employe,
            DocumentSubCategory subCategory,
            DocumentProcessRequest request) throws IOException {

        String fileHash = fileStorageService.calculateFileHash(file);

        EmployeeDocument doc = new EmployeeDocument();
        doc.setEmploye(employe);
        doc.setSubCategory(subCategory);
        doc.setFileName(file.getOriginalFilename());
        doc.setMimeType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setDocumentType(EmployeeDocument.DocumentType.ORIGINAL);
        doc.setFileHash(fileHash);
        doc.setContentHash(fileHash);

        // Référence
        if (request.getReference() != null && !request.getReference().trim().isEmpty()) {
            doc.setReference(request.getReference());
        } else {
            doc.setReference("DOC-" + employe.getMatricule() + "-" + System.currentTimeMillis());
        }

        // Autres champs
        if (request.getSignedBy() != null && !request.getSignedBy().trim().isEmpty()) {
            doc.setSignedBy(request.getSignedBy());
            doc.setSignatureTimestamp(LocalDateTime.now());
        }
        if (request.getExpirationDate() != null) {
            doc.setExpirationDate(request.getExpirationDate());
        }
        doc.setIsVerified(request.getIsVerified() != null ? request.getIsVerified() : false);
        doc.setIsActive(true);
        doc.setVerificationCount(0);

        log.info("📝 Document temp créé - Référence: {}", doc.getReference());
        return doc;
    }

    /**
     * 🔥 SAUVEGARDE UNIQUEMENT DU DOCUMENT RECONSTRUIT
     */
//    private EmployeeDocument saveReconstructedDocument(
//            EmployeeDocument tempDoc,
//            ProcessedDocument processedDoc,
//            Employe employe,
//            DocumentSubCategory subCategory,
//            DocumentProcessRequest request) throws IOException {
//
//        EmployeeDocument reconstructedDoc = new EmployeeDocument();
//
//        reconstructedDoc.setEmploye(employe);
//        reconstructedDoc.setSubCategory(subCategory);
//        reconstructedDoc.setFileName(Paths.get(processedDoc.getFilePath()).getFileName().toString());
//        reconstructedDoc.setFilePath(processedDoc.getFilePath());
//        reconstructedDoc.setMimeType(tempDoc.getMimeType());
//        reconstructedDoc.setFileSize(Files.size(Paths.get(processedDoc.getFilePath())));
//        reconstructedDoc.setDocumentType(EmployeeDocument.DocumentType.RECONSTRUCTED);
//        reconstructedDoc.setFileHash(tempDoc.getFileHash());
//        reconstructedDoc.setContentHash(tempDoc.getContentHash());
//
//        // Copier métadonnées
//        reconstructedDoc.setReference(tempDoc.getReference());
//        reconstructedDoc.setSignedBy(tempDoc.getSignedBy());
//        reconstructedDoc.setSignatureTimestamp(tempDoc.getSignatureTimestamp());
//        reconstructedDoc.setExpirationDate(tempDoc.getExpirationDate());
//        reconstructedDoc.setIsVerified(tempDoc.getIsVerified());
//        reconstructedDoc.setIsActive(true);
//        reconstructedDoc.setVerificationCount(0);
//
//        log.info("📝 Document reconstruit sauvegardé (original non stocké)");
//
//        return documentRepository.save(reconstructedDoc);
//    }

    // Adapter buildResponse pour n'utiliser que reconstructedDoc
    private DocumentProcessResponse buildResponse(
            EmployeeDocument reconstructedDoc,
            DocumentQRCode qrCode,
            ProcessedDocument processedDoc) {

        DocumentProcessResponse response = new DocumentProcessResponse();
        response.setDocumentId(reconstructedDoc.getId());
        response.setDigitalId(qrCode.getDigitalId());
        response.setProcessedFileUrl(reconstructedDoc.getFilePath());
        response.setQrCodeImage(qrCode.getQrImageBase64());
        response.setOriginalFileName(reconstructedDoc.getFileName());
        response.setSignaturesRemoved(processedDoc.getSignaturesRemoved());
        response.setStampsRemoved(processedDoc.getStampsRemoved());
        response.setProcessedAt(LocalDateTime.now());

        Map<String, Object> digitalData = new HashMap<>();
        digitalData.put("document_id", reconstructedDoc.getId());
        digitalData.put("reference", reconstructedDoc.getReference());
        digitalData.put("employe_id", reconstructedDoc.getEmploye().getId());
        digitalData.put("employe_matricule", reconstructedDoc.getEmploye().getMatricule());
        digitalData.put("employe_nom", reconstructedDoc.getEmploye().getNom());
        digitalData.put("employe_prenom", reconstructedDoc.getEmploye().getPrenom());
        digitalData.put("document_type", reconstructedDoc.getDocumentType().name());
        digitalData.put("file_type", processedDoc.getFileType());
        digitalData.put("pages_processed", processedDoc.getPagesProcessed());
        digitalData.put("security_level", "JAVA_NATIVE");
        digitalData.put("processing_date", LocalDateTime.now().toString());
        digitalData.put("signed_by", reconstructedDoc.getSignedBy());
        digitalData.put("expiration_date", reconstructedDoc.getExpirationDate());
        digitalData.put("is_verified", reconstructedDoc.getIsVerified());
        digitalData.put("category_name", reconstructedDoc.getSubCategory().getName());
        digitalData.put("company_name", reconstructedDoc.getEmploye().getCompany().getName());
        digitalData.put("storage_optimization", "ORIGINAL_NOT_STORED");

        response.setDigitalData(digitalData);

        return response;
    }
    /**
     * 🔥 SAUVEGARDE DOCUMENT ORIGINAL avec TOUS les champs
     */
    private EmployeeDocument saveOriginalDocument(
            MultipartFile file,
            Employe employe,
            DocumentSubCategory subCategory,
            DocumentProcessRequest request) throws IOException {

        String safeFileName = System.currentTimeMillis() + "_" +
                file.getOriginalFilename().replace(" ", "_").replace("/", "_");

        Path filePath = fileStorageService.storeFile(file, "originals", safeFileName);
        String fileHash = fileStorageService.calculateFileHash(file);

        EmployeeDocument doc = new EmployeeDocument();

        // Champs obligatoires
        doc.setEmploye(employe);
        doc.setSubCategory(subCategory);
        doc.setFileName(file.getOriginalFilename());
        doc.setFilePath(filePath.toString());
        doc.setMimeType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setDocumentType(EmployeeDocument.DocumentType.ORIGINAL);
        doc.setFileHash(fileHash);
        doc.setContentHash(fileHash); // Même hash pour le contenu

        // 🔥 CHAMPS ADDITIONNELS depuis la requête
        // Référence: auto-générée si non fournie
        if (request.getReference() != null && !request.getReference().trim().isEmpty()) {
            doc.setReference(request.getReference());
        } else {
            // Auto-générer une référence unique
            String autoRef = "DOC-" + employe.getMatricule() + "-" + System.currentTimeMillis();
            doc.setReference(autoRef);
            log.info("Référence auto-générée: {}", autoRef);
        }

        // Signé par
        if (request.getSignedBy() != null && !request.getSignedBy().trim().isEmpty()) {
            doc.setSignedBy(request.getSignedBy());
            doc.setSignatureTimestamp(LocalDateTime.now());
        }

        // Date d'expiration
        if (request.getExpirationDate() != null) {
            doc.setExpirationDate(request.getExpirationDate());
        }

        // Statut de vérification
        if (request.getIsVerified() != null) {
            doc.setIsVerified(request.getIsVerified());
        } else {
            doc.setIsVerified(false); // Par défaut non vérifié
        }

        // Initialisation des champs par défaut
        doc.setIsActive(true);
        doc.setVerificationCount(0);

        log.info("📝 Document original créé avec:");
        log.info("   - Référence: {}", doc.getReference());
        log.info("   - Signé par: {}", doc.getSignedBy());
        log.info("   - Date expiration: {}", doc.getExpirationDate());
        log.info("   - Vérifié: {}", doc.getIsVerified());

        return documentRepository.save(doc);
    }

    /**
     * 🔥 SAUVEGARDE DOCUMENT RECONSTRUIT avec TOUS les champs
     */
    private EmployeeDocument saveReconstructedDocument(
            EmployeeDocument originalDoc,
            ProcessedDocument processedDoc,
            Employe employe,
            DocumentSubCategory subCategory,
            DocumentProcessRequest request) throws IOException {

        EmployeeDocument reconstructedDoc = new EmployeeDocument();

        // Champs obligatoires
        reconstructedDoc.setEmploye(employe);
        reconstructedDoc.setSubCategory(subCategory);
        reconstructedDoc.setFileName(Paths.get(processedDoc.getFilePath()).getFileName().toString());
        reconstructedDoc.setFilePath(processedDoc.getFilePath());
        reconstructedDoc.setMimeType(originalDoc.getMimeType());
        reconstructedDoc.setFileSize(Files.size(Paths.get(processedDoc.getFilePath())));
        reconstructedDoc.setDocumentType(EmployeeDocument.DocumentType.RECONSTRUCTED);
        reconstructedDoc.setFileHash(originalDoc.getFileHash()); // Même hash que l'original
        reconstructedDoc.setContentHash(originalDoc.getContentHash());

        // 🔥 COPIER TOUS LES CHAMPS ADDITIONNELS depuis l'original
        reconstructedDoc.setReference(originalDoc.getReference());
        reconstructedDoc.setSignedBy(originalDoc.getSignedBy());
        reconstructedDoc.setSignatureTimestamp(originalDoc.getSignatureTimestamp());
        reconstructedDoc.setExpirationDate(originalDoc.getExpirationDate());
        reconstructedDoc.setIsVerified(originalDoc.getIsVerified());
        reconstructedDoc.setIsActive(true);
        reconstructedDoc.setVerificationCount(0);

        log.info("📝 Document reconstruit créé avec les mêmes métadonnées que l'original");

        return documentRepository.save(reconstructedDoc);
    }

    private DocumentQRCode generateSecureQRCode(EmployeeDocument document, ProcessedDocument processedDoc) {
        DocumentQRCode qrCode = new DocumentQRCode();
        qrCode.setDocument(document);
        qrCode.setQrData(secureQRService.generateSecureQRData(document));
        qrCode.setQrImageBase64(processedDoc.getQrCodeBase64());
        return qrCodeRepository.save(qrCode);
    }

    private DocumentProcessResponse buildResponse(
            EmployeeDocument originalDoc,
            EmployeeDocument reconstructedDoc,
            DocumentQRCode qrCode,
            ProcessedDocument processedDoc) {

        DocumentProcessResponse response = new DocumentProcessResponse();
        response.setDocumentId(reconstructedDoc.getId());
        response.setDigitalId(qrCode.getDigitalId());
        response.setProcessedFileUrl(reconstructedDoc.getFilePath());
        response.setQrCodeImage(qrCode.getQrImageBase64());
        response.setOriginalFileName(originalDoc.getFileName());
        response.setSignaturesRemoved(processedDoc.getSignaturesRemoved());
        response.setStampsRemoved(processedDoc.getStampsRemoved());
        response.setProcessedAt(LocalDateTime.now());

        // 🔥 DIGITAL DATA ENRICHI avec tous les champs
        Map<String, Object> digitalData = new HashMap<>();
        digitalData.put("document_id", reconstructedDoc.getId());
        digitalData.put("reference", reconstructedDoc.getReference());
        digitalData.put("employe_id", originalDoc.getEmploye().getId());
        digitalData.put("employe_matricule", originalDoc.getEmploye().getMatricule());
        digitalData.put("employe_nom", originalDoc.getEmploye().getNom());
        digitalData.put("employe_prenom", originalDoc.getEmploye().getPrenom());
        digitalData.put("document_type", reconstructedDoc.getDocumentType().name());
        digitalData.put("file_type", processedDoc.getFileType());
        digitalData.put("pages_processed", processedDoc.getPagesProcessed());
        digitalData.put("security_level", "JAVA_NATIVE");
        digitalData.put("processing_date", LocalDateTime.now().toString());

        // Informations additionnelles
        digitalData.put("signed_by", reconstructedDoc.getSignedBy());
        digitalData.put("expiration_date", reconstructedDoc.getExpirationDate());
        digitalData.put("is_verified", reconstructedDoc.getIsVerified());
        digitalData.put("category_name", reconstructedDoc.getSubCategory().getName());
        digitalData.put("company_name", originalDoc.getEmploye().getCompany().getName());

        response.setDigitalData(digitalData);

        return response;
    }

    public String getSystemStatus() {
        StringBuilder status = new StringBuilder();
        status.append("=== SYSTÈME DÉMATÉRIALISATION JAVA ===\n");
        status.append("Service QR: ").append(secureQRService != null ? "OK" : "NOK").append("\n");
        status.append("Service Processing: ").append(documentProcessingService != null ? "OK" : "NOK").append("\n");
        status.append("Service Storage: ").append(fileStorageService != null ? "OK" : "NOK").append("\n");
        status.append("Documents en base: ").append(documentRepository.count()).append("\n");
        status.append("QR Codes en base: ").append(qrCodeRepository.count()).append("\n");

        return status.toString();
    }
}