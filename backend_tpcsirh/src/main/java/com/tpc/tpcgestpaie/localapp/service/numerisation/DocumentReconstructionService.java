package com.tpc.tpcgestpaie.localapp.service.numerisation;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.ProcessusReconstructionDTO;
import com.tpc.tpcgestpaie.localapp.model.numerisation.*;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DocumentReconstructionService {

    private final EmployeeDocumentRepository documentRepository;
    private final DocumentReconstructionProcessRepository processusRepository;
    private final DocumentTemplateRepository templateRepository;
    private final DocumentQRCodeRepository qrCodeRepository;
    private final QRCodeGenerationService qrCodeService;
    private final Map<Long, ProcessusReconstructionDTO> processusCache = new ConcurrentHashMap<>();

    public DocumentReconstructionService(EmployeeDocumentRepository documentRepository,
                                         DocumentReconstructionProcessRepository processusRepository,
                                         DocumentTemplateRepository templateRepository,
                                         DocumentQRCodeRepository qrCodeRepository,
                                         QRCodeGenerationService qrCodeService) {
        this.documentRepository = documentRepository;
        this.processusRepository = processusRepository;
        this.templateRepository = templateRepository;
        this.qrCodeRepository = qrCodeRepository;
        this.qrCodeService = qrCodeService;
    }

    @Transactional
    public ProcessusReconstructionDTO launchDocumentReconstruction(Long documentId, Long modeleId) {
        try {
            log.info("Lancement reconstruction document {} avec modèle {}", documentId, modeleId);

            // Récupération des entités
            EmployeeDocument document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document non trouvé: " + documentId));

            DocumentTemplate template = templateRepository.findById(modeleId)
                    .orElseThrow(() -> new RuntimeException("Modèle non trouvé: " + modeleId));

            // Création du processus
            DocumentReconstructionProcess processus = new DocumentReconstructionProcess();
            processus.setOriginalDocument(document);
            processus.setProcessType(DocumentReconstructionProcess.ProcessType.SCAN_RECONSTRUCTION);
            processus.setStatus(DocumentReconstructionProcess.ProcessStatus.PROCESSING);
            processus.setStartDate(LocalDateTime.now());

            DocumentReconstructionProcess savedProcessus = processusRepository.save(processus);

            // Lancement asynchrone de la reconstruction
            startReconstructionAsync(savedProcessus, template);

            ProcessusReconstructionDTO dto = ProcessusReconstructionDTO.fromEntity(savedProcessus);
            processusCache.put(savedProcessus.getId(), dto);

            return dto;

        } catch (Exception e) {
            log.error("Erreur lors du lancement de la reconstruction", e);
            throw new RuntimeException("Échec du lancement de la reconstruction", e);
        }
    }

    private void startReconstructionAsync(DocumentReconstructionProcess processus, DocumentTemplate template) {
        new Thread(() -> {
            try {
                reconstructDocument(processus, template);
            } catch (Exception e) {
                log.error("Erreur lors de la reconstruction asynchrone du processus {}", processus.getId(), e);
                updateProcessusStatus(processus, DocumentReconstructionProcess.ProcessStatus.ERROR,
                        "Erreur lors de la reconstruction: " + e.getMessage());
            }
        }).start();
    }

    private void reconstructDocument(DocumentReconstructionProcess processus, DocumentTemplate template) {
        try {
            EmployeeDocument originalDocument = processus.getOriginalDocument();

            // Étape 1: Analyse du document original
            updateProcessusStatus(processus, "Analyse du document original");
            DocumentAnalysisResult analysis = analyzeDocument(originalDocument);

            // Étape 2: Suppression des signatures manuscrites si nécessaire
            if (analysis.hasHandwrittenSignatures()) {
                updateProcessusStatus(processus, "Suppression des signatures manuscrites");
                removeHandwrittenSignatures(originalDocument);
            }

            // Étape 3: Application du modèle
            updateProcessusStatus(processus, "Application du modèle de document");
            EmployeeDocument reconstructedDocument = applyDocumentModel(originalDocument, template, processus);

            // Étape 4: Génération et ajout du QR Code
            updateProcessusStatus(processus, "Génération du QR Code");
            addQRCodeToDocument(reconstructedDocument, template);

            // Étape 5: Finalisation
            processus.setReconstructedDocument(reconstructedDocument);
            processus.setStatus(DocumentReconstructionProcess.ProcessStatus.COMPLETED);
            processus.setEndDate(LocalDateTime.now());
            processusRepository.save(processus);

            // Mettre à jour le cache
            ProcessusReconstructionDTO dto = ProcessusReconstructionDTO.fromEntity(processus);
            processusCache.put(processus.getId(), dto);

            log.info("Reconstruction terminée avec succès pour le processus {}", processus.getId());

        } catch (Exception e) {
            log.error("Erreur lors de la reconstruction du document pour le processus {}", processus.getId(), e);
            updateProcessusStatus(processus, DocumentReconstructionProcess.ProcessStatus.ERROR,
                    "Erreur: " + e.getMessage());
        }
    }

    private DocumentAnalysisResult analyzeDocument(EmployeeDocument document) {
        DocumentAnalysisResult result = new DocumentAnalysisResult();
        result.setDocumentQuality(estimateDocumentQuality(document));
        result.setHasHandwrittenSignatures(detectHandwrittenSignatures(document));
        result.setNeedsReconstruction(needsReconstruction(document));
        return result;
    }

    private boolean detectHandwrittenSignatures(EmployeeDocument document) {
        // Logique de détection des signatures manuscrites
        return document.getFilePath() != null &&
                document.getFilePath().toLowerCase().contains("scan");
    }

    private DocumentQuality estimateDocumentQuality(EmployeeDocument document) {
        // Logique d'estimation de la qualité
        return DocumentQuality.MOYEN;
    }

    private boolean needsReconstruction(EmployeeDocument document) {
        return document.getDocumentType() == EmployeeDocument.DocumentType.ORIGINAL;
    }

    private void removeHandwrittenSignatures(EmployeeDocument document) {
        try {
            // Implémentation de la suppression des signatures manuscrites
            log.info("Suppression des signatures manuscrites pour le document {}", document.getId());

            // Pour l'instant, logique simulée
            // byte[] cleanedContent = signatureRemovalService.removeSignatures(document.getFileContent());
            // document.setFileContent(cleanedContent);

        } catch (Exception e) {
            log.warn("Échec de la suppression des signatures manuscrites pour le document {}", document.getId(), e);
        }
    }

    private EmployeeDocument applyDocumentModel(EmployeeDocument originalDocument, DocumentTemplate template,
                                                DocumentReconstructionProcess processus) {
        try {
            // Créer un nouveau document reconstruit
            EmployeeDocument reconstructedDocument = new EmployeeDocument();
            reconstructedDocument.setEmploye(originalDocument.getEmploye());
            reconstructedDocument.setSubCategory(originalDocument.getSubCategory());
            reconstructedDocument.setFileName("reconstructed_" + originalDocument.getFileName());
            reconstructedDocument.setFilePath(generateReconstructedFilePath(originalDocument));
            reconstructedDocument.setMimeType(originalDocument.getMimeType());
            reconstructedDocument.setFileSize(originalDocument.getFileSize());
            reconstructedDocument.setDocumentType(EmployeeDocument.DocumentType.RECONSTRUCTED);
            reconstructedDocument.setCreatedAt(LocalDateTime.now());

            // Appliquer le modèle (logique de reconstruction)
            byte[] reconstructedContent = reconstructContent(originalDocument, template);
            // reconstructedDocument.setFileContent(reconstructedContent);

            return documentRepository.save(reconstructedDocument);

        } catch (Exception e) {
            log.error("Erreur lors de l'application du modèle au document {}", originalDocument.getId(), e);
            throw new RuntimeException("Échec de l'application du modèle", e);
        }
    }

    private byte[] reconstructContent(EmployeeDocument document, DocumentTemplate template) {
        // Implémentation de la reconstruction du contenu
        try {
            // 1. Extraction des données via OCR
            Map<String, String> extractedData = extractDataViaOCR(document);

            // 2. Application du template
            byte[] reconstructed = applyTemplate(extractedData, template);

            return reconstructed;

        } catch (Exception e) {
            log.error("Erreur lors de la reconstruction du contenu", e);
            throw new RuntimeException("Échec de la reconstruction du contenu", e);
        }
    }

    private Map<String, String> extractDataViaOCR(EmployeeDocument document) {
        // Implémentation de l'extraction OCR
        Map<String, String> data = new HashMap<>();
        data.put("nom_employe", document.getEmploye().getNom()+" " + document.getEmploye().getPrenom() );
        data.put("matricule", document.getEmploye().getMatricule());
        data.put("date_document", LocalDateTime.now().toString());
        data.put("categorie", document.getSubCategory().getCategory().getName());
        data.put("sous_categorie", document.getSubCategory().getName());
        return data;
    }
    private byte[] applyTemplate(Map<String, String> data, DocumentTemplate template) {
        // Implémentation de l'application du template
        return new byte[0];
    }
    private void addQRCodeToDocument(EmployeeDocument document, DocumentTemplate template) {
        try {
            // Générer le QR Code
            String uniqueCode = generateUniqueCode();

            // Créer l'entité QR Code
            DocumentQRCode qrCode = new DocumentQRCode();
            qrCode.setDocument(document);
//            qrCode.setUniqueCode(uniqueCode);
//            qrCode.setValidationHash(generateValidationHash(document));
//            qrCode.setGeneratedAt(LocalDateTime.now());
//            qrCode.setValidUntil(LocalDateTime.now().plusYears(1).toLocalDate());
            qrCodeRepository.save(qrCode);
            // Associer le QR Code au document
            document.setQrCode(qrCode);
            documentRepository.save(document);

            log.info("QR Code ajouté au document {}", document.getId());

        } catch (Exception e) {
            log.error("Erreur lors de l'ajout du QR Code au document {}", document.getId(), e);
            throw new RuntimeException("Échec de l'ajout du QR Code", e);
        }
    }

    private String generateUniqueCode() {
        return "DOC_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateValidationHash(EmployeeDocument document) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }

    private String generateReconstructedFilePath(EmployeeDocument originalDocument) {
        String originalPath = originalDocument.getFilePath();
        String extension = originalPath.substring(originalPath.lastIndexOf("."));
        return originalPath.replace(extension, "_reconstructed" + extension);
    }

    public ProcessusReconstructionDTO getProcessStatus(Long processId) {
        try {
            // Vérifier le cache d'abord
            ProcessusReconstructionDTO cached = processusCache.get(processId);
            if (cached != null) {
                return cached;
            }

            DocumentReconstructionProcess processus = processusRepository.findById(processId)
                    .orElseThrow(() -> new RuntimeException("Processus non trouvé: " + processId));

            ProcessusReconstructionDTO dto = ProcessusReconstructionDTO.fromEntity(processus);
            processusCache.put(processId, dto);

            return dto;

        } catch (Exception e) {
            log.error("Erreur lors de la récupération du statut du processus {}", processId, e);
            throw new RuntimeException("Impossible de récupérer le statut du processus", e);
        }
    }

    public List<ProcessusReconstructionDTO> getEmployeeProcesses(Long employeId) {
        try {
            List<DocumentReconstructionProcess> processus = processusRepository
                    .findByOriginalDocument_Employe_IdOrderByStartDateDesc(employeId);

            return ProcessusReconstructionDTO.fromEntities(processus);

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des processus de l'employé {}", employeId, e);
            return new ArrayList<>();
        }
    }

    @Transactional
    public void cancelProcess(Long processId) {
        try {
            DocumentReconstructionProcess processus = processusRepository.findById(processId)
                    .orElseThrow(() -> new RuntimeException("Processus non trouvé: " + processId));
            if (processus.getStatus() == DocumentReconstructionProcess.ProcessStatus.PROCESSING) {
                processus.setStatus(DocumentReconstructionProcess.ProcessStatus.PENDING);
                processus.setEndDate(LocalDateTime.now());
                processusRepository.save(processus);

                // Mettre à jour le cache
                ProcessusReconstructionDTO dto = ProcessusReconstructionDTO.fromEntity(processus);
                processusCache.put(processId, dto);

                log.info("Processus {} annulé", processId);
            } else {
                log.warn("Impossible d'annuler le processus {} - statut: {}", processId, processus.getStatus());
            }

        } catch (Exception e) {
            log.error("Erreur lors de l'annulation du processus {}", processId, e);
            throw new RuntimeException("Échec de l'annulation du processus", e);
        }
    }

    @Transactional
    public ProcessusReconstructionDTO retryProcess(Long processId) {
        try {
            DocumentReconstructionProcess processus = processusRepository.findById(processId)
                    .orElseThrow(() -> new RuntimeException("Processus non trouvé: " + processId));

            if (processus.getStatus() == DocumentReconstructionProcess.ProcessStatus.ERROR) {
                processus.setStatus(DocumentReconstructionProcess.ProcessStatus.PROCESSING);
                processus.setEndDate(null);

                DocumentReconstructionProcess updated = processusRepository.save(processus);

                // Relancer la reconstruction avec le template original
                DocumentTemplate template = templateRepository.findBySubCategory(
                        processus.getOriginalDocument().getSubCategory()
                ).orElse(null);

                if (template != null) {
                    startReconstructionAsync(updated, template);
                }

                ProcessusReconstructionDTO dto = ProcessusReconstructionDTO.fromEntity(updated);
                processusCache.put(processId, dto);

                return dto;
            } else {
                throw new RuntimeException("Seuls les processus en erreur peuvent être relancés");
            }

        } catch (Exception e) {
            log.error("Erreur lors de la relance du processus {}", processId, e);
            throw new RuntimeException("Échec de la relance du processus", e);
        }
    }

    private void updateProcessusStatus(DocumentReconstructionProcess processus, String message) {
        // Mettre à jour le processus
        processusRepository.save(processus);

        // Mettre à jour le cache
        ProcessusReconstructionDTO dto = ProcessusReconstructionDTO.fromEntity(processus);
        processusCache.put(processus.getId(), dto);

        log.info("Processus {} - {}", processus.getId(), message);
    }

    private void updateProcessusStatus(DocumentReconstructionProcess processus,
                                       DocumentReconstructionProcess.ProcessStatus status,
                                       String message) {
        processus.setStatus(status);
        if (status == DocumentReconstructionProcess.ProcessStatus.COMPLETED ||
                status == DocumentReconstructionProcess.ProcessStatus.ERROR) {
            processus.setEndDate(LocalDateTime.now());
        }
        processusRepository.save(processus);

        // Mettre à jour le cache
        ProcessusReconstructionDTO dto = ProcessusReconstructionDTO.fromEntity(processus);
        processusCache.put(processus.getId(), dto);

        log.info("Processus {} - {} - {}", processus.getId(), status, message);
    }

    // Classes internes pour l'analyse
    private static class DocumentAnalysisResult {
        private DocumentQuality documentQuality;
        private boolean hasHandwrittenSignatures;
        private boolean needsReconstruction;

        public DocumentQuality getDocumentQuality() { return documentQuality; }
        public void setDocumentQuality(DocumentQuality documentQuality) { this.documentQuality = documentQuality; }
        public boolean hasHandwrittenSignatures() { return hasHandwrittenSignatures; }
        public void setHasHandwrittenSignatures(boolean hasHandwrittenSignatures) { this.hasHandwrittenSignatures = hasHandwrittenSignatures; }
        public boolean isNeedsReconstruction() { return needsReconstruction; }
        public void setNeedsReconstruction(boolean needsReconstruction) { this.needsReconstruction = needsReconstruction; }
    }

    private enum DocumentQuality {
        EXCELLENT, BON, MOYEN, MAUVAIS, ILLISIBLE
    }
}