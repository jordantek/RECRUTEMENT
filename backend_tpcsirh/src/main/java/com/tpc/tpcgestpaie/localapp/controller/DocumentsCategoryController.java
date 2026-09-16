package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.dto.DocumentCategory.DocumentCategoryRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.DocumentCategory.DocumentCategoryResponseDTO;
import com.tpc.tpcgestpaie.localapp.service.DocumentsCategoryService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents-categories")
@RequiredArgsConstructor
public class DocumentsCategoryController {

    private final DocumentsCategoryService documentsCategoryService;

    // =========================
    // 💾 Création d'une catégorie
    // =========================
    @PostMapping
    public ResponseEntity<ApiResponse<DocumentCategoryResponseDTO>> createCategory(
            @Valid @RequestBody DocumentCategoryRequestDTO dto
    ) {

        try {

            DocumentCategoryResponseDTO category = documentsCategoryService.create(dto);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Catégorie créée avec succès", category)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 📄 Catégories globales + entreprise
    // =========================
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<List<DocumentCategoryResponseDTO>>> getCategoriesByCompany(
            @PathVariable Long companyId
    ) {

        try {

            List<DocumentCategoryResponseDTO> response =
                    documentsCategoryService.findAllByCompanyOrGlobal(companyId);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Catégories récupérées avec succès", response)
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
    public ResponseEntity<ApiResponse<DocumentCategoryResponseDTO>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody DocumentCategoryRequestDTO dto
    ) {

        try {

            DocumentCategoryResponseDTO category = documentsCategoryService.update(id, dto);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Catégorie mise à jour avec succès", category)
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
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long id
    ) {

        try {

            documentsCategoryService.delete(id);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Catégorie supprimée avec succès", null)
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

}