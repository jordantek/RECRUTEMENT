package com.tpc.tpcgestpaie.localapp.controller.arh.employe;

import com.tpc.tpcgestpaie.localapp.model.CategorieEvenement;
import com.tpc.tpcgestpaie.localapp.service.CategorieEvenementService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorie_evenements")
public class CategorieEvenementController {

    private final CategorieEvenementService service;

    public CategorieEvenementController(CategorieEvenementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<CategorieEvenement> list = service.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste récupérée avec succès", list));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de la liste", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<CategorieEvenement> item = service.findById(id);
            if (item.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Catégorie trouvée", item.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Catégorie non trouvée", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la recherche", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CategorieEvenement categorie) {
        try {
            if (categorie.getLibelle() == null || categorie.getLibelle().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le libellé est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            categorie.setLibelle(categorie.getLibelle().toUpperCase());

            if (service.existsByLibelle(categorie.getLibelle())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Cette catégorie existe déjà.", null));
            }

            CategorieEvenement saved = service.save(categorie);
            return new ResponseEntity<>(new ApiResponse<>(true, "Catégorie créée avec succès", saved), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CategorieEvenement updated) {
        try {
            Optional<CategorieEvenement> existing = service.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Catégorie non trouvée", null), HttpStatus.NOT_FOUND);
            }

            String newLibelle = updated.getLibelle() != null ? updated.getLibelle().trim().toUpperCase() : null;

            if (newLibelle == null || newLibelle.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le libellé est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            // Vérifier si un autre élément a déjà ce libellé
            if (service.existsByLibelle(newLibelle) &&
                    !existing.get().getLibelle().equalsIgnoreCase(newLibelle)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Ce libellé existe déjà.", null));
            }

            CategorieEvenement categorie = existing.get();
            categorie.setLibelle(newLibelle);
            categorie.setDescription(updated.getDescription());
            categorie.setUpdatedAt(updated.getUpdatedAt());

            CategorieEvenement saved = service.save(categorie);
            return ResponseEntity.ok(new ApiResponse<>(true, "Catégorie mise à jour avec succès", saved));

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<CategorieEvenement> existing = service.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Catégorie non trouvée", null), HttpStatus.NOT_FOUND);
            }

            CategorieEvenement categorie = existing.get();
            if (categorie.getDeletedAt() != null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Catégorie déjà supprimée", null), HttpStatus.BAD_REQUEST);
            }

            categorie.setDeletedAt(java.time.LocalDateTime.now());
            service.save(categorie);

            return ResponseEntity.ok(new ApiResponse<>(true, "Catégorie supprimée (soft delete) avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
