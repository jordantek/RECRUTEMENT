package com.tpc.tpcgestpaie.localapp.controller.arh.employe;

import com.tpc.tpcgestpaie.localapp.model.LienParente;
import com.tpc.tpcgestpaie.localapp.service.LienParenteService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/liens_parente")
public class LienParenteController {

    private final LienParenteService lienParenteService;

    public LienParenteController(LienParenteService lienParenteService) {
        this.lienParenteService = lienParenteService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<LienParente> list = lienParenteService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste récupérée avec succès", list));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de la liste", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<LienParente> lienParente = lienParenteService.findById(id);
            if (lienParente.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Lien de parenté trouvé", lienParente.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Lien de parenté non trouvé", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la recherche", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LienParente lienParente) {
        try {
            if (lienParente.getLibelle() == null || lienParente.getLibelle().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le libellé est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            lienParente.setLibelle(lienParente.getLibelle().toUpperCase());

            if (lienParenteService.existsByLibelle(lienParente.getLibelle())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Ce lien de parenté existe déjà.", null));
            }

            LienParente saved = lienParenteService.save(lienParente);
            return new ResponseEntity<>(new ApiResponse<>(true, "Lien de parenté créé avec succès", saved), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody LienParente updatedLienParente) {
        try {
            Optional<LienParente> existing = lienParenteService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Lien de parenté non trouvé", null), HttpStatus.NOT_FOUND);
            }

            LienParente lienParente = existing.get();
            lienParente.setLibelle(updatedLienParente.getLibelle().toUpperCase());
            lienParente.setUpdated_at(updatedLienParente.getUpdated_at());

            LienParente saved = lienParenteService.save(lienParente);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lien de parenté mis à jour avec succès", saved));

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<LienParente> existing = lienParenteService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Lien de parenté non trouvé", null), HttpStatus.NOT_FOUND);
            }
            lienParenteService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lien de parenté supprimé avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
