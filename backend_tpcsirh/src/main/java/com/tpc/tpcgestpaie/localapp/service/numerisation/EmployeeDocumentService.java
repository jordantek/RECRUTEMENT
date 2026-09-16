package com.tpc.tpcgestpaie.localapp.service.numerisation;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.DocumentExplorerDTO;
import com.tpc.tpcgestpaie.localapp.dto.numerisation.CategoryWithDocumentsDTO;
import com.tpc.tpcgestpaie.localapp.dto.numerisation.EmployeeDocumentDTO;
import com.tpc.tpcgestpaie.localapp.dto.numerisation.SubCategoryWithDocumentsDTO;
import com.tpc.tpcgestpaie.localapp.model.numerisation.*;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.*;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.util.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class EmployeeDocumentService {

    @Autowired private EmployeeDocumentRepository documentRepository;
    @Autowired private EmployeRepository employeRepository;
    @Autowired private DocumentSubCategoryRepository subCategoryRepository;
    @Autowired private DocumentCategoryRepository categoryRepository;
    @Autowired private DocumentQRCodeRepository qrCodeRepository;
    @Autowired private FileStorageService fileStorageService;
    @Autowired private QRCodeGenerationService qrCodeService;
    @Autowired private ContratEmployeRepository contratEmployeRepository;

    public DocumentExplorerDTO getEmployeeDocumentExplorer(Long employeId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        Optional<ContratEmploye> contratEmploye = contratEmployeRepository.findActifByEmployeId(employeId);

        // Récupérer les catégories (pour l'instant toutes, sans filtre entreprise)
        List<DocumentCategory> categories = categoryRepository.findAll();

        List<CategoryWithDocumentsDTO> categoriesWithDocs = categories.stream()
                .map(category -> {
                    CategoryWithDocumentsDTO categoryDTO = new CategoryWithDocumentsDTO();
                    categoryDTO.setCategoryId(category.getId());
                    categoryDTO.setCategoryName(category.getName());

                    // Sous-catégories avec documents
                    List<SubCategoryWithDocumentsDTO> subCategoriesDTO = category.getSubCategories()
                            .stream()
                            .sorted(Comparator.comparing(DocumentSubCategory::getDisplayOrder))
                            .map(subCategory -> {
                                SubCategoryWithDocumentsDTO subCategoryDTO = new SubCategoryWithDocumentsDTO();
                                subCategoryDTO.setSubCategoryId(subCategory.getId());
                                subCategoryDTO.setSubCategoryName(subCategory.getName());
                                subCategoryDTO.setMandatory(subCategory.getIsMandatory());

                                // Documents de cette sous-catégorie pour cet employé
                                List<EmployeeDocument> documents = documentRepository
                                        .findByEmployeIdAndSubCategoryId(employeId, subCategory.getId());
                                subCategoryDTO.setDocuments(
                                        documents.stream()
                                                .map(doc -> {
                                                    EmployeeDocumentDTO dto = new EmployeeDocumentDTO();
                                                    dto.setId(doc.getId());
                                                    dto.setFileName(doc.getFileName());
                                                    dto.setUploadDate(doc.getCreatedAt());
                                                    dto.setVerified(doc.getIsVerified());
                                                    return dto;
                                                })
                                                .collect(Collectors.toList())
                                );
                                subCategoryDTO.setComplete(
                                        subCategory.getIsMandatory() && !documents.isEmpty()
                                );

                                return subCategoryDTO;
                            })
                            .collect(Collectors.toList());

                    categoryDTO.setSubCategories(subCategoriesDTO);
                    return categoryDTO;
                })
                .collect(Collectors.toList());

        DocumentExplorerDTO explorer = new DocumentExplorerDTO();
        explorer.setEmployeeId(employeId);
        explorer.setEmployeeFullName(employe.getPrenom() + " " + employe.getNom());
        explorer.setEmployeeMatricule(employe.getMatricule());

        // Position depuis le contrat
        String position = "Non défini";
        if (contratEmploye.isPresent()) {
            ContratEmploye contrat = contratEmploye.get();
            if (contrat.getPoste() != null) {
                position = contrat.getPoste().getLibelle();
            }
        }
        explorer.setPosition(position);
        explorer.setCategories(categoriesWithDocs);

        return explorer;
    }
    /**
     * Récupérer un document par son ID
     */
    @Transactional
    public Optional<EmployeeDocument> getDocumentById(Long documentId) {
        return documentRepository.findById(documentId);
    }

    /**
     * Récupérer les documents d'un employé par sous-catégorie
     */
    public List<EmployeeDocument> getDocumentsBySubCategory(Long employeId, Long subCategoryId) {
        return documentRepository.findByEmployeIdAndSubCategoryId(employeId, subCategoryId);
    }

    public EmployeeDocument uploadEmployeeDocument(MultipartFile file, Long employeId, Long subCategoryId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        DocumentSubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new RuntimeException("Sous-catégorie non trouvée"));

        // Vérifier les doublons - même nom de fichier pour le même employé dans la même sous-catégorie
        String originalFilename = file.getOriginalFilename();
        if (documentRepository.existsByEmployeIdAndSubCategoryIdAndFileName(employeId, subCategoryId, originalFilename)) {
            throw new RuntimeException("Un document avec le nom '" + originalFilename + "' existe déjà pour cet employé dans cette sous-catégorie");
        }

        // Vérifier les doublons - même contenu (hash) pour le même employé
        try {
            String fileHash = calculateFileHash(file.getBytes());
            if (documentRepository.existsByEmployeIdAndFileHash(employeId, fileHash)) {
                throw new RuntimeException("Ce document existe déjà pour cet employé (contenu identique)");
            }

            // Sauvegarde physique du fichier
            String storedFileName = fileStorageService.storeFile(file, employe.getMatricule());

            // Création de l'entité Document
            EmployeeDocument document = new EmployeeDocument();
            document.setEmploye(employe);
            document.setSubCategory(subCategory);
            document.setFileName(file.getOriginalFilename());
            document.setFilePath(storedFileName);
            document.setMimeType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setFileHash(fileHash); //Stocker le hash pour détection future des doublons
            document.setDocumentType(EmployeeDocument.DocumentType.ORIGINAL);

            // Calcul de la date d'expiration
            if (subCategory.getRetentionPeriod() != null) {
                document.setExpirationDate(LocalDate.now().plusMonths(subCategory.getRetentionPeriod()));
            }

            EmployeeDocument savedDocument = documentRepository.save(document);

            // Génération du QR Code pour les documents obligatoires
            if (subCategory.getIsMandatory()) {
                generateDocumentWithQRCode(savedDocument);
            }

            log.info("Document uploadé avec succès: {} pour l'employé {}",
                    file.getOriginalFilename(), employe.getMatricule());

            return savedDocument;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier", e);
        }
    }

    public EmployeeDocument updateEmployeeDocument(Long documentId, MultipartFile file, Long subCategoryId) {
        EmployeeDocument existingDocument = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();

            // Vérifier les doublons pour le nouveau fichier
            if (documentRepository.existsByEmployeIdAndSubCategoryIdAndFileNameAndIdNot(
                    existingDocument.getEmploye().getId(),
                    existingDocument.getSubCategory().getId(),
                    originalFilename,
                    documentId)) {
                throw new RuntimeException("Un document avec le nom '" + originalFilename + "' existe déjà pour cet employé dans cette sous-catégorie");
            }

            // Vérifier le hash du nouveau fichier
            try {
                String newFileHash = calculateFileHash(file.getBytes());
                if (documentRepository.existsByEmployeIdAndFileHashAndIdNot(
                        existingDocument.getEmploye().getId(), newFileHash, documentId)) {
                    throw new RuntimeException("Ce document existe déjà pour cet employé (contenu identique)");
                }
                existingDocument.setFileHash(newFileHash);

                // Sauvegarder le nouveau fichier
                String storedFileName = fileStorageService.storeFile(file, existingDocument.getEmploye().getMatricule());

                // Supprimer l'ancien fichier
                fileStorageService.deleteFile(existingDocument.getFilePath());

                existingDocument.setFilePath(storedFileName);
                existingDocument.setFileName(originalFilename);
                existingDocument.setFileSize(file.getSize());
                existingDocument.setMimeType(file.getContentType());

            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la lecture du fichier", e);
            }
        }

        if (subCategoryId != null) {
            DocumentSubCategory newSubCategory = subCategoryRepository.findById(subCategoryId)
                    .orElseThrow(() -> new RuntimeException("Sous-catégorie non trouvée"));

            // Vérifier les doublons dans la nouvelle sous-catégorie
            if (file != null && !file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                if (documentRepository.existsByEmployeIdAndSubCategoryIdAndFileNameAndIdNot(
                        existingDocument.getEmploye().getId(),
                        subCategoryId,
                        originalFilename,
                        documentId)) {
                    throw new RuntimeException("Un document avec le nom '" + originalFilename + "' existe déjà pour cet employé dans la nouvelle sous-catégorie");
                }
            }

            existingDocument.setSubCategory(newSubCategory);

            // Recalculer la date d'expiration si la période de retention change
            if (newSubCategory.getRetentionPeriod() != null) {
                existingDocument.setExpirationDate(LocalDate.now().plusMonths(newSubCategory.getRetentionPeriod()));
            }
        }

        return documentRepository.save(existingDocument);
    }

    private String calculateFileHash(byte[] fileContent) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(fileContent);
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de calcul du hash", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    @Transactional
    public List<EmployeeDocument> getEmployeeDocuments(Long employeId) {
//        return documentRepository.findByEmployeIdOrderByCreatedAtDesc(employeId);

        List<EmployeeDocument> documents = documentRepository.findByEmployeIdWithRelations(employeId);

        // Initialiser les relations lazy manuellement
        for (EmployeeDocument document : documents) {
            if (document.getSubCategory() != null) {
                // Force l'initialisation de la sous-catégorie
                Hibernate.initialize(document.getSubCategory());
            }
        }

        return documents;
    }

    public List<EmployeeDocument> searchEmployeeDocuments(Long employeId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getEmployeeDocuments(employeId);
        }
        return documentRepository.searchByEmployeeAndKeywordWithRelations(employeId, keyword);
    }

    public void verifyDocumentCompleteness(Long employeId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        // Récupérer toutes les sous-catégories obligatoires
        List<DocumentSubCategory> mandatorySubCategories = subCategoryRepository.findAll()
                .stream()
                .filter(DocumentSubCategory::getIsMandatory)
                .collect(Collectors.toList());

        List<String> missingDocuments = new ArrayList<>();

        for (DocumentSubCategory subCategory : mandatorySubCategories) {
            boolean exists = documentRepository.existsByEmployeIdAndSubCategoryId(employeId, subCategory.getId());
            if (!exists) {
                missingDocuments.add(subCategory.getName());
            }
        }

        if (!missingDocuments.isEmpty()) {
            log.warn("Documents obligatoires manquants pour l'employé {}: {}",
                    employe.getMatricule(), String.join(", ", missingDocuments));
        } else {
            log.info("Tous les documents obligatoires sont présents pour l'employé {}", employe.getMatricule());
        }
    }

    @Transactional
    public Resource downloadDocument(Long documentId) {
        EmployeeDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        return fileStorageService.loadFileAsResource(document.getFilePath());
    }

    public void deleteDocument(Long documentId) {
        EmployeeDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        // Supprimer le fichier physique
        fileStorageService.deleteFile(document.getFilePath());

        // Supprimer le QR Code associé
        if (document.getQrCode() != null) {
            qrCodeRepository.delete(document.getQrCode());
        }

        // Supprimer le document de la base
        documentRepository.delete(document);

        log.info("Document {} supprimé avec succès", documentId);
    }

    public EmployeeDocument markAsVerified(Long documentId) {
        EmployeeDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        document.setIsVerified(true);
        return documentRepository.save(document);
    }

    @Async
    public void generateDocumentWithQRCode(EmployeeDocument document) {
        try {
            DocumentQRCode qrCode = qrCodeService.generateQRCodeForDocument(document);
            document.setQrCode(qrCode);
            documentRepository.save(document);
            log.info("QR Code généré pour le document {}", document.getId());
        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR Code pour le document {}", document.getId(), e);
        }
    }

    /**
     * Vérifier si un document existe déjà pour un employé dans une sous-catégorie
     */
    public boolean documentExists(Long employeId, Long subCategoryId, String fileName) {
        return documentRepository.existsByEmployeIdAndSubCategoryIdAndFileName(employeId, subCategoryId, fileName);
    }

    /**
     * Vérifier si un document avec le même contenu existe déjà pour un employé
     */
    public boolean documentContentExists(Long employeId, String fileHash) {
        return documentRepository.existsByEmployeIdAndFileHash(employeId, fileHash);
    }
}