package com.tpc.tpcgestpaie.localapp.controller;


import com.tpc.tpcgestpaie.localapp.dto.DocumentsModel.DocumentModelRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.DocumentsModel.DocumentModelResponseDTO;
import com.tpc.tpcgestpaie.localapp.service.DocumentModelService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/document-models")
@RequiredArgsConstructor
public class DocumentModelController {

    private final DocumentModelService documentModelService;

    // =========================
    // 💾 Création d'un modèle
    // =========================
    @PostMapping
    public ResponseEntity<ApiResponse<DocumentModelResponseDTO>> createDocumentModel(
            @Valid @RequestBody DocumentModelRequestDTO dto
    ) {

        try {

            DocumentModelResponseDTO model = documentModelService.create(dto);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Modèle de document créé avec succès", model)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 📄 Modèles global + entreprise
    // =========================
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<List<DocumentModelResponseDTO>>> getModelsByCompany(
            @PathVariable Long companyId
    ) {

        try {

            List<DocumentModelResponseDTO> response =
                    documentModelService.findAllByCompanyOrGlobal(companyId);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Modèles récupérés avec succès", response)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 📄 Tous les modèles
    // =========================
    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentModelResponseDTO>>> getAllModels() {

        try {

            List<DocumentModelResponseDTO> models =
                    documentModelService.findAll();

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Modèles récupérés avec succès", models)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 🔎 Modèle par ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentModelResponseDTO>> getModelById(
            @PathVariable Long id
    ) {

        try {

            DocumentModelResponseDTO model =
                    documentModelService.findById(id);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Modèle récupéré avec succès", model)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // ✏️ Mise à jour
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentModelResponseDTO>> updateModel(
            @PathVariable Long id,
            @Valid @RequestBody DocumentModelRequestDTO dto
    ) {

        try {

            DocumentModelResponseDTO model =
                    documentModelService.update(id, dto);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Modèle mis à jour avec succès", model)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 🗑 Soft delete
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteModel(
            @PathVariable Long id
    ) {

        try {

            documentModelService.delete(id);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Modèle supprimé avec succès", null)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

}