package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.Documents.*;
import com.tpc.tpcgestpaie.localapp.model.Document;
import com.tpc.tpcgestpaie.localapp.model.DocumentModel;
import com.tpc.tpcgestpaie.localapp.repository.DocumentRepository;
import com.tpc.tpcgestpaie.localapp.repository.DocumentModelRepository;
import com.tpc.tpcgestpaie.localapp.util.FileManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentModelRepository documentModelRepository;
    private final FileManager fileManager = new FileManager("storage/archives");

    // ================================
    // Création de documents
    // ================================
    public List<Document> createDocuments(DocumentCreationDTO creationDTO) throws IOException {
        Long companyId = creationDTO.companyId();
        Long employeeId = creationDTO.employeeId();

        return creationDTO.documents().stream()
                .map(docReq -> {
                    try {
                        return createDocument(companyId, employeeId, docReq);
                    } catch (IOException e) {
                        throw new RuntimeException("Erreur lors de la création du document", e);
                    }
                })
                .collect(Collectors.toList());
    }

    private Document createDocument(Long companyId, Long employeeId, DocumentRequestDTO dto) throws IOException {
        DocumentModel model = getDocumentModel(dto.modelId());

        // Sauvegarde le fichier et récupère le chemin complet
        Path savedFile = saveFile(companyId, employeeId, dto.file());

        // Récupérer le nom unique généré pour l'enregistrement
        String uniqueFileName = savedFile.getFileName().toString();

        Document document = Document.builder()
                .reference(dto.reference() != null ? dto.reference() : generateReference())
                .title(dto.title())
                .description(dto.description())
                .employeeId(employeeId)
                .model(model)
                .signatureDate(dto.signatureDate())
                .validFrom(dto.validFrom())
                .validTo(dto.validTo())
                .fileName(uniqueFileName)  // <-- ici on met le nom unique
                .filePath(savedFile.toString())
                .fileSize(dto.file().getSize())
                .fileType(dto.file().getContentType())
                .companyId(companyId)
                .isScanned(true)
                .showOnEmployeeProfile(model.getShowOnEmployeeProfile())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return documentRepository.save(document);
    }
    // ================================
// Mise à jour d'un document
// ================================
    public Document updateDocument(Long documentId, DocumentRequestDTO dto) throws IOException {
        // Récupérer le document existant
        Document document = findById(documentId);

        // Mettre à jour le modèle si nécessaire
        DocumentModel model = null;
        if (dto.modelId() != null) {
            model = getDocumentModel(dto.modelId());
            document.setModel(model);

            // Synchroniser le flag showOnEmployeeProfile avec le modèle
            document.setShowOnEmployeeProfile(model.getShowOnEmployeeProfile());
        }

        // Mettre à jour les champs texte et référence
        document.setReference(dto.reference() != null ? dto.reference() : document.getReference());
        document.setTitle(dto.title());
        document.setDescription(dto.description());
        document.setSignatureDate(dto.signatureDate());
        document.setValidFrom(dto.validFrom());
        document.setValidTo(dto.validTo());

        // Gestion du fichier : sauvegarde et mise à jour des informations
        if (dto.file() != null) {
            Path savedFile = saveFile(document.getCompanyId(), document.getEmployeeId(), dto.file());
            String uniqueFileName = savedFile.getFileName().toString();

            document.setFileName(uniqueFileName);
            document.setFilePath(savedFile.toString());
            document.setFileSize(dto.file().getSize());
            document.setFileType(dto.file().getContentType());
            document.setIsScanned(true);
        }

        // Mise à jour de la date de modification
        document.setUpdatedAt(LocalDateTime.now());

        // Sauvegarde et retour
        return documentRepository.save(document);
    }
    // ================================
    // Suppression & restauration
    // ================================
    public void deleteDocument(Long id) {
        Document doc = findById(id);
        doc.softDelete();
        documentRepository.save(doc);
    }

    public void restoreDocument(Long id) {
        Document doc = findById(id);
        doc.restore();
        documentRepository.save(doc);
    }

    public void hardDeleteDocument(Long id) throws IOException {
        Document doc = findById(id);
        Path path = Path.of(doc.getFilePath());
        if (Files.exists(path)) Files.delete(path);
        documentRepository.delete(doc);
    }

    // ================================
    // Recherche & filtres
    // ================================
    public List<Document> searchDocuments(Long companyId, Long employeeId, Long modelId,
                                          Boolean scanned, Boolean expired) {
        return documentRepository.findAll().stream()
                .filter(d -> companyId == null || d.getCompanyId().equals(companyId))
                .filter(d -> employeeId == null || d.getEmployeeId().equals(employeeId))
                .filter(d -> modelId == null || d.getModel().getId().equals(modelId))
                .filter(d -> scanned == null || d.getIsScanned().equals(scanned))
                .filter(d -> expired == null || (expired && d.getValidTo() != null && d.getValidTo().isBefore(LocalDate.now())))
                .collect(Collectors.toList());
    }

    // ================================
    // Récupération
    // ================================
    public Document findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));
    }

    public List<Document> findByCompany(Long companyId) {
        return documentRepository.findByCompanyIdAndDeletedAtIsNull(companyId);
    }

    public List<Document> findByEmployee(Long employeeId) {
        return documentRepository.findByEmployeeId(employeeId);
    }

    public List<Document> findByModel(Long modelId) {
        return documentRepository.findByModel_Id(modelId);
    }

    public List<Document> findScannedByCompany(Long companyId) {
        return documentRepository.findByCompanyIdAndIsScannedTrue(companyId);
    }

    public List<Document> findExpiredByCompany(Long companyId) {
        return documentRepository.findByCompanyIdAndValidToBefore(companyId, LocalDate.now());
    }

    public List<Document> getSignedDocumentsForProfile(Long employeeId) {
        return documentRepository
                .findSignedDocumentsForEmployeeProfileByEmployee(employeeId);
    }


    // ================================
    // Mapping vers DTO
    // ================================
    public static DocumentResponseDTO mapToResponse(Document doc) {

        return new DocumentResponseDTO(
                doc.getId(),
                doc.getReference(),
                doc.getTitle(),
                doc.getDescription(),
                doc.getFileName(),
                doc.getFilePath(),
                doc.getFileSize(),
                doc.getFileType(),
                doc.getEmployeeId(),
                doc.getModel() != null ? doc.getModel().getId() : null,
                doc.getModel() != null ? doc.getModel().getName() : null,
                null,
                 "",
                 doc.getSignatureDate(),
                doc.getValidFrom(),
                doc.getValidTo(),
                doc.getIsScanned(),
                doc.getCompanyId(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }

    // ================================
    // Helpers privés
    // ================================
    private DocumentModel getDocumentModel(Long modelId) {
        return documentModelRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("Modèle de document introuvable"));
    }

    private Path saveFile(Long companyId, Long employeeId,MultipartFile file) throws IOException {
        String relativePath = "company_" + companyId + "/employees/EMP" + employeeId;
        return fileManager.saveFile(file, relativePath, false, false);
    }

    private String generateReference() {
        String ref;
        do {
            ref = "DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (documentRepository.existsByReference(ref));
        return ref;
    }
}