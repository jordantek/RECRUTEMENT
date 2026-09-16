package com.tpc.tpcgestpaie.localapp.controller.common;

import com.tpc.tpcgestpaie.localapp.model.Diplome;
import com.tpc.tpcgestpaie.localapp.service.DiplomeService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diplomes")
public class DiplomeController {

    private final DiplomeService diplomeService;

    public DiplomeController(DiplomeService diplomeService) {
        this.diplomeService = diplomeService;
    }

    // Liste de tous les diplômes
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Diplome> list = diplomeService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des diplômes récupérée avec succès", list));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des diplômes", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer un diplôme par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<Diplome> diplome = diplomeService.findById(id);
            if (diplome.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Diplôme trouvé", diplome.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Diplôme non trouvé", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la recherche du diplôme", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Créer un nouveau diplôme
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Diplome diplome) {
        try {
            if (diplome.getName() == null || diplome.getName().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le nom est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            String nomFormate = diplome.getName().trim().toUpperCase();

            if (diplomeService.existsByName(nomFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Ce diplôme existe déjà.", null));
            }

            diplome.setName(nomFormate); // Conversion ici
            Diplome saved = diplomeService.save(diplome);
            return new ResponseEntity<>(new ApiResponse<>(true, "Diplôme créé avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création du diplôme", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Mettre à jour un diplôme existant
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Diplome updatedDiplome) {
        try {

            if (updatedDiplome.getName() == null || updatedDiplome.getName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.LENGTH_REQUIRED)
                        .body(new ApiResponse<>(false, "Le nom est vide", null));
            }

            Optional<Diplome> existing = diplomeService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Diplôme non trouvé", null));
            }

            Diplome diplome = existing.get();
            diplome.setName(updatedDiplome.getName());
            diplome.setUpdated_at(LocalDateTime.now());

            Diplome saved = diplomeService.save(diplome);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Diplôme mis à jour avec succès", saved)
            );

        } catch (DataIntegrityViolationException e) {

            // Message SQL lisible
            String message = "Ce type de diplôme existe déjà.";
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(false, message, null));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur interne du serveur", null));
        }
    }
    // Supprimer un diplôme
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<Diplome> existing = diplomeService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Diplôme non trouvé", null), HttpStatus.NOT_FOUND);
            }

//            boolean isUsed = employeDiplomeRepository.existsByDiplomeId(id);
//            if (isUsed) {
//                // Retourner une erreur ou un message indiquant que la suppression est impossible
//                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Impossible de supprimer : diplôme utilisé par des employés", null));
//            }

            diplomeService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Diplôme supprimé avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression du diplôme", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
