package com.tpc.tpcgestpaie.localapp.controller.numerisation;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.DocumentExplorerDTO;
import com.tpc.tpcgestpaie.localapp.dto.numerisation.EmployeeDocumentDTO;
import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.EmployeeDocumentRepository;
import com.tpc.tpcgestpaie.localapp.service.numerisation.EmployeeDocumentService;
import com.tpc.tpcgestpaie.localapp.service.numerisation.QRCodeGenerationService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee-documents")
@Slf4j
public class EmployeeDocumentController {

    private final EmployeeDocumentService documentService;
    private final QRCodeGenerationService qrCodeService;
    private final EmployeeDocumentRepository employeeDocumentRepository;

    public EmployeeDocumentController(EmployeeDocumentService documentService, QRCodeGenerationService qrCodeService, EmployeeDocumentRepository employeeDocumentRepository) {
        this.documentService = documentService;
        this.qrCodeService = qrCodeService;
        this.employeeDocumentRepository = employeeDocumentRepository;
    }

    /**
     * Récupérer un document par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<EmployeeDocument> document = documentService.getDocumentById(id);
            if (document.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Document trouvé", EmployeeDocumentDTO.fromEntity(document.get())));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Document non trouvé", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du document {}", id, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération du document", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Explorateur de documents pour un employé
     */
    @GetMapping("/explorer/employee/{employeId}")
    public ResponseEntity<?> getDocumentExplorer(@PathVariable Long employeId) {
        try {
            DocumentExplorerDTO explorer = documentService.getEmployeeDocumentExplorer(employeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Explorateur de documents récupéré avec succès", explorer));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'explorateur pour l'employé {}", employeId, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de l'explorateur de documents", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Upload d'un document
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long employeId,
            @RequestParam Long subCategoryId) {
        try {
            // Validation des paramètres obligatoires
            if (file == null || file.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le fichier est obligatoire", null), HttpStatus.BAD_REQUEST);
            }
            if (employeId == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "L'ID de l'employé est obligatoire", null), HttpStatus.BAD_REQUEST);
            }
            if (subCategoryId == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "L'ID de la sous-catégorie est obligatoire", null), HttpStatus.BAD_REQUEST);
            }

            EmployeeDocument document = documentService.uploadEmployeeDocument(file, employeId, subCategoryId);
            return new ResponseEntity<>(new ApiResponse<>(true, "Document uploadé avec succès", EmployeeDocumentDTO.fromEntity(document)), HttpStatus.CREATED);

        } catch (RuntimeException e) {
            log.error("Erreur de validation lors de l'upload du document", e);
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Erreur technique lors de l'upload du document", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur technique lors de l'upload du document", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mettre à jour un document
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDocument(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(required = false) Long subCategoryId) {
        try {
            // Vérifier l'existence du document
            Optional<EmployeeDocument> existing = documentService.getDocumentById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Document non trouvé", null), HttpStatus.NOT_FOUND);
            }

            EmployeeDocument updatedDocument = documentService.updateEmployeeDocument(id, file, subCategoryId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Document mis à jour avec succès", EmployeeDocumentDTO.fromEntity(updatedDocument)));

        } catch (RuntimeException e) {
            log.error("Erreur de validation lors de la mise à jour du document {}", id, e);
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Erreur technique lors de la mise à jour du document {}", id, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur technique lors de la mise à jour du document", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Liste des documents d'un employé
     */
    @GetMapping("/employee/{employeId}")
    public ResponseEntity<?> getEmployeeDocuments(@PathVariable Long employeId) {
        try {
            List<EmployeeDocument> documents = documentService.getEmployeeDocuments(employeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Documents récupérés avec succès", EmployeeDocumentDTO.fromEntities(documents)));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des documents de l'employé {}", employeId, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des documents", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        try {
            EmployeeDocument document = employeeDocumentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Document non trouvé"));

            // 🔥 Normaliser le chemin (gérer .\ et / )
            String filePath = document.getFilePath();

            // Si le chemin commence par .\ ou ./, le résoudre depuis le répertoire courant
            Path path;
            if (filePath.startsWith(".\\") || filePath.startsWith("./")) {
                path = Paths.get(filePath).toAbsolutePath().normalize();
            } else {
                path = Paths.get(filePath).normalize();
            }

            log.info("Chemin résolu: {}", path);

            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Fichier introuvable: " + path);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getMimeType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Erreur téléchargement document {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Documents par sous-catégorie pour un employé
     */
    @GetMapping("/employee/{employeId}/subcategory/{subCategoryId}")
    public ResponseEntity<?> getDocumentsBySubCategory(
            @PathVariable Long employeId,
            @PathVariable Long subCategoryId) {
        try {
            List<EmployeeDocument> documents = documentService.getDocumentsBySubCategory(employeId, subCategoryId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Documents récupérés avec succès", EmployeeDocumentDTO.fromEntities(documents)));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des documents par sous-catégorie", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des documents", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Recherche de documents
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchDocuments(
            @RequestParam Long employeId,
            @RequestParam String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le terme de recherche est obligatoire", null), HttpStatus.BAD_REQUEST);
            }

            List<EmployeeDocument> documents = documentService.searchEmployeeDocuments(employeId, keyword.trim());
            return ResponseEntity.ok(new ApiResponse<>(true, "Recherche de documents effectuée avec succès", EmployeeDocumentDTO.fromEntities(documents)));
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des documents", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la recherche des documents", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Vérification de la complétude des documents
     */
    @GetMapping("/verify-completeness/{employeId}")
    public ResponseEntity<?> verifyDocumentCompleteness(@PathVariable Long employeId) {
        try {
            documentService.verifyDocumentCompleteness(employeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Vérification de complétude terminée avec succès", null));
        } catch (RuntimeException e) {
            log.error("Erreur métier lors de la vérification des documents", e);
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification des documents", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la vérification des documents", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Téléchargement d'un document
     */
//    @GetMapping("/download/{id}")
//    public ResponseEntity<?> downloadDocument(@PathVariable Long id) {
//        try {
//            Resource resource = documentService.downloadDocument(id);
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .header(HttpHeaders.CONTENT_DISPOSITION,
//                            "attachment; filename=\"" + resource.getFilename() + "\"")
//                    .body(resource);
//
//        } catch (RuntimeException e) {
//            log.error("Erreur métier lors du téléchargement du document {}", id, e);
//            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            log.error("Erreur technique lors du téléchargement du document {}", id, e);
//            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors du téléchargement du document", null), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * Prévisualisation d'un document
     */
    @GetMapping("/preview/{id}")
    public ResponseEntity<?> previewDocument(@PathVariable Long id) {
        try {
            Resource resource = documentService.downloadDocument(id);

            MediaType mediaType = determineMediaType(resource);

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (RuntimeException e) {
            log.error("Erreur métier lors de la prévisualisation du document {}", id, e);
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Erreur technique lors de la prévisualisation du document {}", id, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la prévisualisation du document", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Suppression d'un document
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        try {
            // Vérifier l'existence du document
            Optional<EmployeeDocument> existing = documentService.getDocumentById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Document non trouvé", null), HttpStatus.NOT_FOUND);
            }

            documentService.deleteDocument(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Document supprimé avec succès", null));

        } catch (RuntimeException e) {
            log.error("Erreur métier lors de la suppression du document {}", id, e);
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Erreur technique lors de la suppression du document {}", id, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression du document", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Marquer un document comme vérifié
     */
    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifyDocument(@PathVariable Long id) {
        try {
            // Vérifier l'existence du document
            Optional<EmployeeDocument> existing = documentService.getDocumentById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Document non trouvé", null), HttpStatus.NOT_FOUND);
            }

            EmployeeDocument document = documentService.markAsVerified(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Document marqué comme vérifié", EmployeeDocumentDTO.fromEntity(document)));

        } catch (RuntimeException e) {
            log.error("Erreur métier lors de la vérification du document {}", id, e);
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Erreur technique lors de la vérification du document {}", id, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la vérification du document", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Vérifier si un document existe déjà
     */
    @GetMapping("/check-duplicate")
    public ResponseEntity<?> checkDuplicateDocument(
            @RequestParam Long employeId,
            @RequestParam Long subCategoryId,
            @RequestParam String fileName) {
        try {
            // Validation des paramètres
            if (employeId == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "L'ID de l'employé est obligatoire", null), HttpStatus.BAD_REQUEST);
            }
            if (subCategoryId == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "L'ID de la sous-catégorie est obligatoire", null), HttpStatus.BAD_REQUEST);
            }
            if (fileName == null || fileName.trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le nom du fichier est obligatoire", null), HttpStatus.BAD_REQUEST);
            }

            boolean exists = documentService.documentExists(employeId, subCategoryId, fileName.trim());

            if (exists) {
                return ResponseEntity.ok(new ApiResponse<>(false, "Un document avec ce nom existe déjà dans cette sous-catégorie", true));
            } else {
                return ResponseEntity.ok(new ApiResponse<>(true, "Aucun doublon détecté", false));
            }

        } catch (Exception e) {
            log.error("Erreur lors de la vérification des doublons", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la vérification des doublons", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private MediaType determineMediaType(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        if (filename.toLowerCase().endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        } else if (filename.toLowerCase().matches(".*\\.(jpg|jpeg)$")) {
            return MediaType.IMAGE_JPEG;
        } else if (filename.toLowerCase().endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        } else if (filename.toLowerCase().matches(".*\\.(doc|docx)$")) {
            return MediaType.valueOf("application/msword");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}