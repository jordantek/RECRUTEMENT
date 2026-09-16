package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.model.ModeDePaiement;
import com.tpc.tpcgestpaie.localapp.service.ModeDePaiementService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/arh/mode-paiements")
public class ModeDePaiementController {

    private final ModeDePaiementService modeDePaiementService;

    public ModeDePaiementController(ModeDePaiementService modeDePaiementService) {
        this.modeDePaiementService = modeDePaiementService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<ModeDePaiement> list = modeDePaiementService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des modes de paiement récupérée avec succès", list));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<ModeDePaiement> mode = modeDePaiementService.findById(id);
            return mode.map(value ->
                    ResponseEntity.ok(new ApiResponse<>(true, "Mode de paiement trouvé", value))
            ).orElseGet(() ->
                    new ResponseEntity<>(new ApiResponse<>(false, "Mode de paiement non trouvé", null), HttpStatus.NOT_FOUND)
            );
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ModeDePaiement modeDePaiement) {
        try {
            if (modeDePaiement.getLibelle() == null || modeDePaiement.getLibelle().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le libellé est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            String libelleFormate = modeDePaiement.getLibelle().trim().toUpperCase();

            if (modeDePaiementService.existsByLibelle(libelleFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Ce libellé existe déjà.", null));
            }

            modeDePaiement.setLibelle(libelleFormate);
            ModeDePaiement saved = modeDePaiementService.save(modeDePaiement);
            return new ResponseEntity<>(new ApiResponse<>(true, "Mode de paiement créé avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ModeDePaiement updated) {
        try {
            Optional<ModeDePaiement> existing = modeDePaiementService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Mode de paiement non trouvé", null), HttpStatus.NOT_FOUND);
            }

            ModeDePaiement mode = existing.get();
            String libelleFormate = updated.getLibelle() != null ? updated.getLibelle().trim().toUpperCase() : null;

            if (modeDePaiementService.existsByLibelle(libelleFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Ce libellé existe déjà.", null));
            }
            mode.setLibelle(libelleFormate);

            mode.setDescription(updated.getDescription());
            mode.setUpdated_at(updated.getUpdated_at());

            ModeDePaiement saved = modeDePaiementService.save(mode);
            return ResponseEntity.ok(new ApiResponse<>(true, "Mode de paiement mis à jour avec succès", saved));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<ModeDePaiement> existing = modeDePaiementService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Mode de paiement non trouvé", null), HttpStatus.NOT_FOUND);
            }

            modeDePaiementService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Mode de paiement supprimé avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
