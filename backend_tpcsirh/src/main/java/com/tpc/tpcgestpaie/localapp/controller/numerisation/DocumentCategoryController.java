package com.tpc.tpcgestpaie.localapp.controller.numerisation;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.DocumentCategoryDTO;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentSubCategory;
import com.tpc.tpcgestpaie.localapp.service.numerisation.DocumentCategoryService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/document-categories")
@Slf4j
public class DocumentCategoryController {

    private final DocumentCategoryService categoryService;

    public DocumentCategoryController(DocumentCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Créer une nouvelle catégorie
     */

    @Transactional
    @PostMapping
    public ResponseEntity<ApiResponse<DocumentCategoryDTO>> createCategory(@RequestBody DocumentCategoryDTO categoryDTO) {

        log.info("📍 Début création catégorie - CompanyId: {}, Name: {}",
                categoryDTO.getCompanyId(), categoryDTO.getName());

        try {
            // Validation des champs obligatoires
            if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Le nom de la catégorie est obligatoire", null));
            }

            // ⭐ CORRECTION : Maintenant ça retourne un DTO
            DocumentCategoryDTO savedCategory = categoryService.createCategoryFromDTO(categoryDTO);

            log.info("✅ Catégorie créée avec succès - ID: {}", savedCategory.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Catégorie créée avec succès", savedCategory));

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier lors de la création de la catégorie", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("💥 Erreur technique inattendue lors de la création de la catégorie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur serveur interne: " + e.getMessage(), null));
        }
    }

    /**
     * Liste des catégories par entreprise
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<List<DocumentCategoryDTO>>> getCategoriesByCompany(@PathVariable Long companyId) {
        log.info("📍 Récupération catégories pour entreprise ID: {}", companyId);

        try {
            // ⭐ CORRECTION : Maintenant ça retourne des DTOs
            List<DocumentCategoryDTO> categories = categoryService.getCategoriesByCompany(companyId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des catégories récupérée avec succès", categories)
            );
        } catch (Exception e) {
            log.error("💥 Erreur lors de la récupération des catégories de l'entreprise {}", companyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des catégories: " + e.getMessage(), null));
        }
    }

    /**
     * Récupérer une catégorie par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentCategoryDTO>> getById(@PathVariable Long id) {
        log.info("📍 Récupération catégorie ID: {}", id);

        try {
            // ⭐ CORRECTION : Maintenant ça retourne un DTO
            Optional<DocumentCategoryDTO> category = categoryService.findById(id);
            if (category.isPresent()) {
                return ResponseEntity.ok(
                        new ApiResponse<>(true, "Catégorie trouvée", category.get())
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Catégorie non trouvée", null));
        } catch (Exception e) {
            log.error("💥 Erreur lors de la recherche de la catégorie {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la recherche de la catégorie: " + e.getMessage(), null));
        }
    }

    /**
     * Mettre à jour une catégorie
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentCategoryDTO>> updateCategory(@PathVariable Long id, @RequestBody DocumentCategoryDTO categoryDTO) {
        log.info("📍 Mise à jour catégorie ID: {}", id);

        try {
            // ⭐ CORRECTION : Vérifier si la catégorie existe
            Optional<DocumentCategoryDTO> existing = categoryService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Catégorie non trouvée", null));
            }

            if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Le nom est obligatoire", null));
            }

            // ⭐ CORRECTION : S'assurer que l'ID est bien défini
            categoryDTO.setId(id);

            // ⭐ CORRECTION : Créer une méthode updateCategoryFromDTO dans le service
            // Pour l'instant, on va créer une méthode temporaire
            DocumentCategoryDTO updatedCategory = categoryService.updateCategoryFromDTO(categoryDTO);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Catégorie mise à jour avec succès", updatedCategory)
            );

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier lors de la mise à jour de la catégorie {}", id, e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("💥 Erreur technique lors de la mise à jour de la catégorie {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la mise à jour de la catégorie: " + e.getMessage(), null));
        }
    }

    /**
     * Supprimer une catégorie
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        log.info("📍 Suppression catégorie ID: {}", id);

        try {
            // ⭐ CORRECTION : Vérifier si la catégorie existe
            Optional<DocumentCategoryDTO> existing = categoryService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Catégorie non trouvée", null));
            }

            categoryService.deleteCategory(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Catégorie supprimée avec succès", null)
            );

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier lors de la suppression de la catégorie {}", id, e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("💥 Erreur technique lors de la suppression de la catégorie {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la suppression de la catégorie: " + e.getMessage(), null));
        }
    }

    /**
     * Sous-catégories d'une catégorie
     */
    @GetMapping("/{id}/subcategories")
    public ResponseEntity<ApiResponse<List<DocumentSubCategory>>> getSubCategories(@PathVariable Long id) {
        log.info("📍 Récupération sous-catégories pour catégorie ID: {}", id);

        try {
            List<DocumentSubCategory> subCategories = categoryService.getSubCategoriesByCategory(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des sous-catégories récupérée avec succès", subCategories)
            );
        } catch (Exception e) {
            log.error("💥 Erreur lors de la récupération des sous-catégories de la catégorie {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des sous-catégories: " + e.getMessage(), null));
        }
    }

    /**
     * Créer une sous-catégorie
     */
    @PostMapping("/{id}/subcategories")
    public ResponseEntity<ApiResponse<DocumentSubCategory>> createSubCategory(@PathVariable Long id, @RequestBody DocumentSubCategory subCategory) {
        log.info("📍 Création sous-catégorie pour catégorie ID: {}", id);

        try {
            if (subCategory.getName() == null || subCategory.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Le nom de la sous-catégorie est obligatoire", null));
            }

            DocumentSubCategory savedSubCategory = categoryService.createSubCategory(id, subCategory);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Sous-catégorie créée avec succès", savedSubCategory));

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier lors de la création de la sous-catégorie", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("💥 Erreur technique lors de la création de la sous-catégorie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la création de la sous-catégorie: " + e.getMessage(), null));
        }
    }

    // ⭐ NOUVEAUX ENDPOINTS POUR LA GESTION AVANCÉE

    @GetMapping("/company/{companyId}/shared")
    public ResponseEntity<ApiResponse<List<DocumentCategoryDTO>>> getSharedCategories(@PathVariable Long companyId) {
        try {
            // ⭐ CORRECTION : Maintenant ça retourne des DTOs
            List<DocumentCategoryDTO> categories = categoryService.getSharedCategories(companyId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Catégories partagées récupérées avec succès", categories)
            );
        } catch (Exception e) {
            log.error("💥 Erreur lors de la récupération des catégories partagées", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des catégories partagées: " + e.getMessage(), null));
        }
    }

    @GetMapping("/company/{companyId}/specific")
    public ResponseEntity<ApiResponse<List<DocumentCategoryDTO>>> getCompanySpecificCategories(@PathVariable Long companyId) {
        try {
            // ⭐ CORRECTION : Maintenant ça retourne des DTOs
            List<DocumentCategoryDTO> categories = categoryService.getCompanySpecificCategories(companyId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Catégories spécifiques récupérées avec succès", categories)
            );
        } catch (Exception e) {
            log.error("💥 Erreur lors de la récupération des catégories spécifiques", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des catégories spécifiques: " + e.getMessage(), null));
        }
    }
}