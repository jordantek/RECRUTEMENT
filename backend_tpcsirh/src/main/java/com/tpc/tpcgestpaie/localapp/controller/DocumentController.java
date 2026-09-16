package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.dto.Documents.DocumentCreationDTO;
import com.tpc.tpcgestpaie.localapp.dto.Documents.DocumentRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.Documents.DocumentResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.Document;
import com.tpc.tpcgestpaie.localapp.service.DocumentService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // -----------------------------
    // 💾 Création d'un ou plusieurs documents
    // -----------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<List<DocumentResponseDTO>>> createDocuments(
            @Valid @ModelAttribute DocumentCreationDTO request
    ) {
        try {
            List<Document> savedDocuments = documentService.createDocuments(request);

            // Mapper chaque Document vers DocumentResponseDTO
            List<DocumentResponseDTO> response = savedDocuments.stream()
                    .map(DocumentService::mapToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Documents créés avec succès", response)
            );
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur lors de l'enregistrement des fichiers : " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ---------------------------------------------------
    // 📄 Lister les documents d'un employé
    // ---------------------------------------------------
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<DocumentResponseDTO>>> getDocumentsByEmployee(
            @PathVariable Long employeeId
    ) {

        try {

            List<Document> documents = documentService.findByEmployee(employeeId);

            List<DocumentResponseDTO> response = documents.stream()
                    .map(DocumentService::mapToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Documents récupérés avec succès", response)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        }
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<?> downloadDocument(@PathVariable Long documentId) {

        try {

            Document document = documentService.findById(documentId);

            if (document == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Document introuvable", null));
            }

            java.nio.file.Path path = java.nio.file.Paths.get(document.getFilePath());

            if (!java.nio.file.Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Fichier introuvable sur le serveur", null));
            }

            org.springframework.core.io.Resource resource =
                    new org.springframework.core.io.UrlResource(path.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getFileType()))
                    .header(
                            org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getFileName() + "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        }
    }

    // ---------------------------------------------------
// 📄 Récupérer les informations d'un document
// ---------------------------------------------------
    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<DocumentResponseDTO>> getDocumentById(
            @PathVariable Long documentId
    ) {

        try {

            Document document = documentService.findById(documentId);

            if (document == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Document introuvable", null));
            }

            DocumentResponseDTO response = DocumentService.mapToResponse(document);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Document récupéré avec succès", response)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        }
    }

    // ---------------------------------------------------
// 📄 Documents signés visibles sur le profil employé
// ---------------------------------------------------
    @GetMapping("/employee/signede/{employeeId}")
    public ResponseEntity<ApiResponse<List<DocumentResponseDTO>>> getSignedDocumentsForProfile(
            @PathVariable Long employeeId
    ) {
        try {

            List<Document> documents = documentService
                    .getSignedDocumentsForProfile(employeeId);

            List<DocumentResponseDTO> response = documents.stream()
                    .map(DocumentService::mapToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Documents signés récupérés avec succès", response)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        }
    }

    // ---------------------------------------------------
    // 🔄 Mise à jour d'un document existant
    // ---------------------------------------------------
    @PutMapping("/{documentId}")
    public ResponseEntity<ApiResponse<DocumentResponseDTO>> updateDocument(
            @PathVariable Long documentId,
            @Valid @ModelAttribute DocumentRequestDTO request
    ) {
        try {
            // Vérifier si le document existe
            Document existingDocument = documentService.findById(documentId);
            if (existingDocument == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Document introuvable", null));
            }

            // Mettre à jour le document via le service
            Document updatedDocument = documentService.updateDocument(documentId, request);

            // Mapper vers DTO pour la réponse
            DocumentResponseDTO response = DocumentService.mapToResponse(updatedDocument);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Document mis à jour avec succès", response)
            );

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur lors de la mise à jour des fichiers : " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

}