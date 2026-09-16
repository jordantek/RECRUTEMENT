package com.tpc.tpcgestpaie.localapp.controller.arh;

import com.tpc.tpcgestpaie.localapp.model.NatureContrat;
import com.tpc.tpcgestpaie.localapp.service.NatureContratService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/arh/nature-contrats")
public class NatureContratController {

    private final NatureContratService natureContratService;

    public NatureContratController(NatureContratService natureContratService) {
        this.natureContratService = natureContratService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<NatureContrat> list = natureContratService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des natures de contrat récupérée avec succès", list));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<NatureContrat> nc = natureContratService.findById(id);
            return nc.map(natureContrat ->
                    ResponseEntity.ok(new ApiResponse<>(true, "Nature de contrat trouvée", natureContrat))
            ).orElseGet(() ->
                    new ResponseEntity<>(new ApiResponse<>(false, "Nature de contrat non trouvée", null), HttpStatus.NOT_FOUND)
            );
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NatureContrat natureContrat) {
        try {
            if (natureContrat.getLibelle() == null || natureContrat.getLibelle().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le libellé est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            String libelleFormate = natureContrat.getLibelle().trim().toUpperCase();

            if (natureContratService.existsByLibelle(libelleFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Ce libellé existe déjà.", null));
            }

            natureContrat.setLibelle(libelleFormate);
            NatureContrat saved = natureContratService.save(natureContrat);
            return new ResponseEntity<>(new ApiResponse<>(true, "Nature de contrat créée avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody NatureContrat updated) {
        try {
            Optional<NatureContrat> existing = natureContratService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Nature de contrat non trouvée", null), HttpStatus.NOT_FOUND);
            }

            NatureContrat natureContrat = existing.get();
            String libelleFormate = updated.getLibelle() != null ? updated.getLibelle().trim().toUpperCase() : null;
            natureContrat.setLibelle(libelleFormate);
            natureContrat.setUpdated_at(updated.getUpdated_at());

            NatureContrat saved = natureContratService.save(natureContrat);
            return ResponseEntity.ok(new ApiResponse<>(true, "Nature de contrat mise à jour avec succès", saved));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<NatureContrat> existing = natureContratService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Nature de contrat non trouvée", null), HttpStatus.NOT_FOUND);
            }

            natureContratService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Nature de contrat supprimée avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
