package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.model.ColonneAffichage;
import com.tpc.tpcgestpaie.localapp.service.ColonneAffichageService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/paie/colonne-affichages")
public class ColonneAffichageController {

    private final ColonneAffichageService colonneAffichageService;

    public ColonneAffichageController(ColonneAffichageService colonneAffichageService) {
        this.colonneAffichageService = colonneAffichageService;
    }


    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<ColonneAffichage> colonneAffichages = colonneAffichageService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des colonnes d'affichage récupérée avec succès", colonneAffichages));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des colonnes  d'affichages", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<ColonneAffichage> colonneAffichage = colonneAffichageService.findById(id);
            if (colonneAffichage.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Colonne d'affichage trouvé", colonneAffichage.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Colonne d'affichage non trouvé", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de la Colonne d'affichage", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ColonneAffichage colonneAffichage) {
        try {
            if (colonneAffichage.getLibelle() == null || colonneAffichage.getLibelle().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le libelle de la Colonne d'affichage est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            String libelleFormate = colonneAffichage.getLibelle().trim().toUpperCase();

            if (colonneAffichageService.existsByLibelle(libelleFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Le libelle de la Colonne d'affichage existe déjà.", null));
            }

            colonneAffichage.setLibelle(libelleFormate);
            ColonneAffichage saved = colonneAffichageService.save(colonneAffichage);
            return new ResponseEntity<>(new ApiResponse<>(true, "Colonne d'affichage créée avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création de la Colonne d'affichage", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ColonneAffichage updateColonneAffichage) {
        try {
            Optional<ColonneAffichage> existing = colonneAffichageService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Colonne d'affichage non trouvée", null), HttpStatus.NOT_FOUND);
            }

            ColonneAffichage colonneAffichage = existing.get();
            String libelleFormate = updateColonneAffichage.getLibelle() != null ? updateColonneAffichage.getLibelle().trim().toUpperCase() : null;
            colonneAffichage.setLibelle(libelleFormate);
            colonneAffichage.setUpdated_at(updateColonneAffichage.getUpdated_at());

            ColonneAffichage saved = colonneAffichageService.save(colonneAffichage
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "Colonne d'affichage mise à jour avec succès", saved));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour de  la Colonne d'affichage", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<ColonneAffichage> existing = colonneAffichageService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Colonne d'affichage non trouvée", null), HttpStatus.NOT_FOUND);
            }

            colonneAffichageService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Colonne d'affichage supprimé avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression de  la  colonne  d'affichage", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
