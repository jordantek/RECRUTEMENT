package com.tpc.tpcgestpaie.localapp.controller.arh.employe;

import com.tpc.tpcgestpaie.localapp.model.CategorieEmploye;
import com.tpc.tpcgestpaie.localapp.service.CategorieEmployeService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories_employes")
public class CategorieEmployeController {

    private final CategorieEmployeService categorieEmployeService;

    public CategorieEmployeController(CategorieEmployeService service) {
        this.categorieEmployeService = service;
    }

    // Liste de toutes les catégories
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<CategorieEmploye> list = categorieEmployeService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des catégories récupérée avec succès", list));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des catégories", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer une catégorie par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<CategorieEmploye> categorie = categorieEmployeService.findById(id);
            if (categorie.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Catégorie trouvée", categorie.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Catégorie non trouvée", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de la catégorie", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Créer une nouvelle catégorie
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CategorieEmploye categorieEmploye) {
        try {
            if (categorieEmploye.getName() == null || categorieEmploye.getName().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le nom est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            String nomFormate = categorieEmploye.getName().trim().toUpperCase();

            if (categorieEmployeService.existsByName(nomFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Le nom existe déjà.", null));
            }

            categorieEmploye.setName(nomFormate);
            CategorieEmploye saved = categorieEmployeService.save(categorieEmploye);
            return new ResponseEntity<>(new ApiResponse<>(true, "Catégorie créée avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création de la catégorie", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Mettre à jour une catégorie existante
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CategorieEmploye updatedCategorie) {
        try {

            if (updatedCategorie.getName() == null || updatedCategorie.getName().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le nom est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            Optional<CategorieEmploye> existing = categorieEmployeService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Catégorie non trouvée", null), HttpStatus.NOT_FOUND);
            }

            CategorieEmploye categorie = existing.get();
            String nomFormate = updatedCategorie.getName() != null ? updatedCategorie.getName().trim().toUpperCase() : null;

            if (categorieEmployeService.existsByName(nomFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Le nom existe déjà.", null));
            }

            categorie.setName(nomFormate);
            categorie.setUpdatedAt(updatedCategorie.getUpdatedAt());

            CategorieEmploye saved = categorieEmployeService.save(categorie);
            return ResponseEntity.ok(new ApiResponse<>(true, "Catégorie mise à jour avec succès", saved));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour de la catégorie", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Supprimer une catégorie
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<CategorieEmploye> existing = categorieEmployeService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Catégorie non trouvée", null), HttpStatus.NOT_FOUND);
            }

            categorieEmployeService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Catégorie supprimée avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression de la catégorie", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
