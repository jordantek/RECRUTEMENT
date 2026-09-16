package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.model.MotifArretContrat;
import com.tpc.tpcgestpaie.localapp.service.MotifArretContratService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/motifs_arret_contrat")
public class MotifArretContratController {

    private final MotifArretContratService motifService;

    public MotifArretContratController(MotifArretContratService motifService) {
        this.motifService = motifService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<MotifArretContrat> list = motifService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des motifs récupérée avec succès", list));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de la liste", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<MotifArretContrat> motif = motifService.findById(id);
            if (motif.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Motif trouvé", motif.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Motif non trouvé", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la recherche", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MotifArretContrat motif) {
        try {
            if (motif.getLibelle() == null || motif.getLibelle().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le libellé est vide", null),
                        HttpStatus.LENGTH_REQUIRED);
            }

            motif.setLibelle(motif.getLibelle().toUpperCase());

            if (motifService.existsByLibelle(motif.getLibelle())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Ce motif existe déjà.", null));
            }

            MotifArretContrat saved = motifService.save(motif);
            return new ResponseEntity<>(new ApiResponse<>(true, "Motif créé avec succès", saved), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MotifArretContrat updatedMotif) {
        try {
            Optional<MotifArretContrat> existing = motifService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Motif non trouvé", null), HttpStatus.NOT_FOUND);
            }

            MotifArretContrat motif = existing.get();
            motif.setLibelle(updatedMotif.getLibelle().toUpperCase());
            motif.setDescription(updatedMotif.getDescription());
            motif.setUpdated_at(updatedMotif.getUpdated_at());

            MotifArretContrat saved = motifService.save(motif);
            return ResponseEntity.ok(new ApiResponse<>(true, "Motif mis à jour avec succès", saved));

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<MotifArretContrat> existing = motifService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Motif non trouvé", null), HttpStatus.NOT_FOUND);
            }
            motifService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Motif supprimé avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
