package com.tpc.tpcgestpaie.localapp.controller.arh;

import com.tpc.tpcgestpaie.localapp.model.Poste;
import com.tpc.tpcgestpaie.localapp.model.Departement;
import com.tpc.tpcgestpaie.localapp.service.DepartementService;
import com.tpc.tpcgestpaie.localapp.service.PosteService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/arh/postes")
public class PosteController {

    private final PosteService posteService;
    private final DepartementService departementService;

    public PosteController(PosteService posteService, DepartementService departementService) {
        this.posteService = posteService;
        this.departementService = departementService;
    }

    // Liste des postes (optionnellement par département)
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(value = "departementId", required = false) Long departementId) {
        try {
            List<Poste> list;
            if (departementId != null) {
                Optional<Departement> dept = departementService.findById(departementId);
                if (dept.isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse<>(false, "Département non trouvé", null), HttpStatus.NOT_FOUND);
                }
                list = posteService.getPostesByDepartement(dept.get());
            } else {
                // Suppose qu'une méthode getAllPostes existe sinon ajoute-la
                list = posteService.getAllPostes();
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des postes récupérée avec succès", list));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des postes", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping
    public ResponseEntity<?> create(@RequestBody Poste poste) {
        try {
            if (poste.getLibelle() == null || poste.getLibelle().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le libellé est obligatoire", null), HttpStatus.BAD_REQUEST);
            }

            if (poste.getDepartement_id() == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "L'ID du département est obligatoire", null), HttpStatus.BAD_REQUEST);
            }

            Optional<Departement> departement = departementService.findById(poste.getDepartement_id());
            if (departement.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Département non trouvé", null), HttpStatus.NOT_FOUND);
            }

            String libelleFormate = poste.getLibelle().trim().toUpperCase();

            // Vérifier si un poste avec ce libellé existe déjà dans le même département
            if (posteService.existsByLibelleAndDepartementId(libelleFormate, poste.getDepartement_id())) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Ce poste existe déjà dans ce département", null), HttpStatus.CONFLICT);
            }

            poste.setLibelle(libelleFormate);
            poste.setDepartement(departement.get());
            poste.setCreatedAt(LocalDateTime.now());

            Poste saved = posteService.createPoste(poste);
            return new ResponseEntity<>(new ApiResponse<>(true, "Poste créé avec succès", saved), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création du poste", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer un poste par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<Poste> poste = posteService.getPosteById(id);
            return poste.map(value -> ResponseEntity.ok(new ApiResponse<>(true, "Poste trouvé", value)))
                    .orElseGet(() -> new ResponseEntity<>(new ApiResponse<>(false, "Poste non trouvé", null), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération du poste", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Mettre à jour un poste
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Poste posteDetails) {
        try {
            if (posteDetails.getDepartement_id() != null) {
                Optional<Departement> departement = departementService.findById(posteDetails.getDepartement_id());
                if (departement.isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse<>(false, "Département non trouvé", null), HttpStatus.NOT_FOUND);
                }
                posteDetails.setDepartement(departement.get());
            }

            Poste updated = posteService.updatePoste(id, posteDetails);
            if (updated == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Poste non trouvé", null), HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Poste mis à jour avec succès", updated));

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour du poste", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Supprimer un poste
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            boolean deleted = posteService.deletePoste(id);
            if (!deleted) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Poste non trouvé", null), HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Poste supprimé avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression du poste", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
