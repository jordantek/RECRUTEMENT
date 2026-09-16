package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.model.NiveauAffichage;
import com.tpc.tpcgestpaie.localapp.service.NiveauAffichageService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/paie/niveau-affichages")
public class NiveauAffichageController {

    private final NiveauAffichageService niveauAffichageService;

    public NiveauAffichageController(NiveauAffichageService niveauAffichageService) {
        this.niveauAffichageService = niveauAffichageService;
    }


    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<NiveauAffichage> niveauAffichages = niveauAffichageService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des niveaux d'affichage récupérée avec succès", niveauAffichages));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des niveaux d'affichage", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<NiveauAffichage> niveauAffichage = niveauAffichageService.findById(id);
            if (niveauAffichage.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Niveau d'affichage trouvé", niveauAffichage.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Niveau d'affichage non trouvé", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération du Niveau d'affichage", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NiveauAffichage niveauAffichage) {
        try {
            if (niveauAffichage.getLibelle() == null || niveauAffichage.getLibelle().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le libelle du niveau d'affichage est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            String libelleFormate = niveauAffichage.getLibelle().trim().toUpperCase();

            if (niveauAffichageService.existsByLibelle(libelleFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Le libelle du niveau d'affichage existe déjà.", null));
            }

            niveauAffichage.setLibelle(libelleFormate);
            NiveauAffichage saved = niveauAffichageService.save(niveauAffichage);
            return new ResponseEntity<>(new ApiResponse<>(true, "Niveau d'affichage créé avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création du niveau d'affichage", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody NiveauAffichage updatedNiveauAffichage) {
        try {
            Optional<NiveauAffichage> existing = niveauAffichageService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Niveau d'affichage non trouvé", null), HttpStatus.NOT_FOUND);
            }

            NiveauAffichage niveauAffichage = existing.get();
            String libelleFormate = updatedNiveauAffichage.getLibelle() != null ? updatedNiveauAffichage.getLibelle().trim().toUpperCase() : null;
            niveauAffichage.setLibelle(libelleFormate);
            niveauAffichage.setUpdated_at(updatedNiveauAffichage.getUpdated_at());

            NiveauAffichage saved = niveauAffichageService.save(niveauAffichage);
            return ResponseEntity.ok(new ApiResponse<>(true, "Niveau d'affichage mise à jour avec succès", saved));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour du Niveau d'affichage", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<NiveauAffichage> existing = niveauAffichageService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Niveau d'affichage non trouvé", null), HttpStatus.NOT_FOUND);
            }

            niveauAffichageService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Niveau d'affichage supprimé avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression du Niveau d'affichage", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
