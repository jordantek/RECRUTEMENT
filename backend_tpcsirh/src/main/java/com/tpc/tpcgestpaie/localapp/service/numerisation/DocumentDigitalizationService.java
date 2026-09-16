package com.tpc.tpcgestpaie.localapp.service.numerisation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.dto.numerisation.DocumentProcessRequest;
import com.tpc.tpcgestpaie.localapp.dto.numerisation.DocumentProcessResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class DocumentDigitalizationService {

    private final EmployeeDocumentRepository documentRepository;
    private final DocumentQRCodeRepository qrCodeRepository;
    private final EmployeRepository employeRepository;
    private final DocumentSubCategoryRepository subCategoryRepository;
    private final PythonIntegrationService pythonService;

    @Value("${app.storage.processed-dir:./storage/processed}")
    private String processedDir;

    public DocumentDigitalizationService(EmployeeDocumentRepository documentRepository,
                                         DocumentQRCodeRepository qrCodeRepository,
                                         EmployeRepository employeRepository,
                                         DocumentSubCategoryRepository subCategoryRepository,
                                         PythonIntegrationService pythonService) {
        this.documentRepository = documentRepository;
        this.qrCodeRepository = qrCodeRepository;
        this.employeRepository = employeRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.pythonService = pythonService;
    }

    public DocumentProcessResponse processAndDigitalizeDocument(
            MultipartFile file,
            DocumentProcessRequest request) {

        log.info("Début digitalisation document: {}", file.getOriginalFilename());

        try {
            // 1. Vérifier l'employé et la catégorie
            Employe employe = employeRepository.findById(request.getEmployeId())
                    .orElseThrow(() -> new RuntimeException("Employé non trouvé ID: " + request.getEmployeId()));

            DocumentSubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée ID: " + request.getSubCategoryId()));

            log.info("Employé: {}, Catégorie: {}", employe.getNom(), subCategory.getName());

            // 2. Sauvegarder le document original
            EmployeeDocument originalDoc = saveOriginalDocument(file, employe, subCategory);
            log.info("Document original sauvegardé: {}", originalDoc.getFilePath());

            // 3. Vérifier que le fichier existe physiquement
            Path originalFilePath = Paths.get(originalDoc.getFilePath());
            if (!Files.exists(originalFilePath)) {
                throw new RuntimeException("Fichier original non trouvé: " + originalFilePath);
            }
            log.info("Fichier original existe, taille: {} bytes", Files.size(originalFilePath));

            // 4. Traiter le document avec Python
            Map<String, Object> pythonResult = pythonService.processDocument(
                    originalDoc.getFilePath(),
                    employe.getId(),
                    subCategory.getName()
            );

            log.info("Résultat Python: status={}", pythonResult.get("status"));

            // 5. Vérifier le résultat Python
            if (!"success".equals(pythonResult.get("status"))) {
                throw new RuntimeException("Échec traitement Python: " + pythonResult.get("error_message"));
            }

            // 6. Vérifier que le fichier traité existe
            String processedFilePath = (String) pythonResult.get("processed_file_path");
            if (processedFilePath == null) {
                throw new RuntimeException("Chemin du fichier traité manquant dans la réponse Python");
            }

            Path processedPath = Paths.get(processedFilePath);
            if (!Files.exists(processedPath)) {
                log.warn("Fichier traité non trouvé: {}, utilisation du fichier original", processedFilePath);
                processedFilePath = originalDoc.getFilePath(); // Fallback
            } else {
                log.info("Fichier traité trouvé: {}, taille: {} bytes",
                        processedFilePath, Files.size(processedPath));
            }

            // 7. Sauvegarder le document reconstruit en BDD
            EmployeeDocument reconstructedDoc = saveReconstructedDocument(
                    originalDoc, pythonResult, employe, subCategory, processedFilePath);

            // 8. Générer et sauvegarder le QR Code
            DocumentQRCode qrCode = saveQRCode(reconstructedDoc, pythonResult);

            // 9. Construire la réponse
            DocumentProcessResponse response = buildResponse(originalDoc, reconstructedDoc, qrCode, pythonResult);

            log.info("Digitalisation terminée avec succès - DigitalID: {}", response.getDigitalId());
            return response;

        } catch (Exception e) {
            log.error("❌ Erreur digitalisation document: {}", e.getMessage(), e);
            throw new RuntimeException("Échec digitalisation: " + e.getMessage());
        }
    }

    private EmployeeDocument saveOriginalDocument(MultipartFile file, Employe employe, DocumentSubCategory subCategory) throws IOException {
        // Créer le répertoire des originaux
        Path originalsDir = Paths.get(processedDir, "originals");
        Files.createDirectories(originalsDir);

        // Nom de fichier sécurisé
        String safeFileName = System.currentTimeMillis() + "_" +
                file.getOriginalFilename().replace(" ", "_").replace("/", "_");
        Path filePath = originalsDir.resolve(safeFileName);

        // Sauvegarder physiquement
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        log.info("Fichier original sauvegardé: {}", filePath);

        // Sauvegarder en BDD
        EmployeeDocument doc = new EmployeeDocument();
        doc.setEmploye(employe);
        doc.setSubCategory(subCategory);
        doc.setFileName(file.getOriginalFilename());
        doc.setFilePath(filePath.toAbsolutePath().toString());
        doc.setMimeType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setDocumentType(EmployeeDocument.DocumentType.ORIGINAL);

        return documentRepository.save(doc);
    }

    /**
     * Extrait l'extension d'un fichier de manière sécurisée
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return ".pdf"; // extension par défaut
        }

        // Supprimer les espaces et normaliser
        String cleanName = filename.trim().toLowerCase();

        // Trouver la dernière occurrence du point
        int lastDotIndex = cleanName.lastIndexOf(".");

        if (lastDotIndex > 0 && lastDotIndex < cleanName.length() - 1) {
            return cleanName.substring(lastDotIndex);
        }

        // Extension par défaut selon le type de contenu connu
        return ".pdf";
    }

    private EmployeeDocument saveReconstructedDocument(
            EmployeeDocument originalDoc,
            Map<String, Object> pythonResult,
            Employe employe,
            DocumentSubCategory subCategory,
            String processedFilePath) throws IOException {

        // 1. Créer le répertoire reconstructed
        Path reconstructedDir = Paths.get(processedDir, "reconstructed");
        Files.createDirectories(reconstructedDir);

        // 2. Générer un nom de fichier sécurisé avec extension
        String fileExtension = getFileExtension(originalDoc.getFileName());
        String reconstructedFileName = "RECONSTRUCTED_" +
                System.currentTimeMillis() + fileExtension;
        Path targetPath = reconstructedDir.resolve(reconstructedFileName);

        // 3. Copier le fichier traité OU l'original en fallback
        Path sourcePath = Paths.get(processedFilePath);

        if (Files.exists(sourcePath) && !sourcePath.equals(targetPath)) {
            // Copier le fichier traité par Python
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("✅ Fichier reconstruit copié: {} -> {}", sourcePath, targetPath);
        } else {
            // Fallback: copier l'original
            Path originalPath = Paths.get(originalDoc.getFilePath());
            if (Files.exists(originalPath)) {
                Files.copy(originalPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                log.warn("⚠️ Utilisation fichier original comme fallback: {}", originalPath);
            } else {
                throw new IOException("Aucun fichier source disponible");
            }
        }

        // 4. Sauvegarder en BDD
        EmployeeDocument reconstructedDoc = new EmployeeDocument();
        reconstructedDoc.setEmploye(employe);
        reconstructedDoc.setSubCategory(subCategory);
        reconstructedDoc.setFileName(reconstructedFileName);
        reconstructedDoc.setFilePath(targetPath.toAbsolutePath().toString()); // ✅ Chemin correct
        reconstructedDoc.setMimeType(originalDoc.getMimeType());
        reconstructedDoc.setDocumentType(EmployeeDocument.DocumentType.RECONSTRUCTED);
        reconstructedDoc.setFileSize(Files.size(targetPath));

        // ❌ SUPPRIMEZ cette ligne - Votre entité n'a pas cette relation
        // reconstructedDoc.setOriginalDocument(originalDoc);

        return documentRepository.save(reconstructedDoc);
    }


    private DocumentQRCode saveQRCode(EmployeeDocument document, Map<String, Object> pythonResult) {
        DocumentQRCode qrCode = new DocumentQRCode();
        qrCode.setDocument(document);
        qrCode.setQrData(jsonifyMap((Map<String, Object>) pythonResult.get("digital_data")));
        qrCode.setQrImageBase64((String) pythonResult.get("qr_code_base64"));

        DocumentQRCode savedQr = qrCodeRepository.save(qrCode);
        log.info("QR Code généré - DigitalID: {}", savedQr.getDigitalId());
        return savedQr;
    }

    private DocumentProcessResponse buildResponse(
            EmployeeDocument originalDoc,
            EmployeeDocument reconstructedDoc,
            DocumentQRCode qrCode,
            Map<String, Object> pythonResult) {

        DocumentProcessResponse response = new DocumentProcessResponse();
        response.setDocumentId(reconstructedDoc.getId());
        response.setDigitalId(qrCode.getDigitalId());
        response.setProcessedFileUrl(reconstructedDoc.getFilePath());
        response.setQrCodeImage(qrCode.getQrImageBase64());
        response.setOriginalFileName(originalDoc.getFileName());

        // Gérer les valeurs nullables
        response.setSignaturesRemoved(getIntegerSafe(pythonResult, "signatures_removed"));
        response.setStampsRemoved(getIntegerSafe(pythonResult, "stamps_removed"));

        response.setProcessedAt(LocalDateTime.now());
        response.setDigitalData((Map<String, Object>) pythonResult.get("digital_data"));

        return response;
    }

    private Integer getIntegerSafe(Map<String, Object> map, String key) {
        try {
            Object value = map.get(key);
            if (value instanceof Integer) return (Integer) value;
            if (value instanceof String) return Integer.parseInt((String) value);
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private String jsonifyMap(Map<String, Object> map) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            log.warn("Erreur conversion JSON, retour objet vide");
            return "{}";
        }
    }

    // Méthode de diagnostic
    public String getStorageStatus() {
        try {
            Path originals = Paths.get(processedDir, "originals");
            Path reconstructed = Paths.get(processedDir, "reconstructed");

            StringBuilder status = new StringBuilder();
            status.append("=== STATUT STOCKAGE ===\n");
            status.append("Répertoire originals: ").append(Files.exists(originals))
                    .append(" - ").append(originals.toAbsolutePath()).append("\n");
            status.append("Répertoire reconstructed: ").append(Files.exists(reconstructed))
                    .append(" - ").append(reconstructed.toAbsolutePath()).append("\n");

            if (Files.exists(originals)) {
                long originalsCount = Files.list(originals).count();
                status.append("Fichiers dans originals: ").append(originalsCount).append("\n");
            }

            if (Files.exists(reconstructed)) {
                long reconstructedCount = Files.list(reconstructed).count();
                status.append("Fichiers dans reconstructed: ").append(reconstructedCount).append("\n");
            }

            return status.toString();

        } catch (Exception e) {
            return "Erreur statut stockage: " + e.getMessage();
        }
    }
}