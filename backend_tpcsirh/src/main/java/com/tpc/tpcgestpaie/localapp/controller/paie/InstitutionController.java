package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.model.Institution;
import com.tpc.tpcgestpaie.localapp.service.paie.InstitutionService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/paie/institutions")
public class InstitutionController {

    private final InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Institution> institutions = institutionService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des institutions récupérée avec succès", institutions));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des institutions", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<Institution> institution = institutionService.findById(id);
            if (institution.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Institution trouvée", institution.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Institution non trouvée", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de l'Institution", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Institution institution) {
        try {
            if (institution.getName() == null || institution.getName().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le nom de l'institution est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            String nomFormate = institution.getName().trim().toUpperCase();

            if (institutionService.existsByName(nomFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Le nom de l'Institution existe déjà.", null));
            }

            institution.setName(nomFormate);
            Institution saved = institutionService.save(institution);
            return new ResponseEntity<>(new ApiResponse<>(true, "Institution créée avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création de l'Institution", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Institution updatedInstitution) {
        try {
            Optional<Institution> existing = institutionService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Institution non trouvée", null), HttpStatus.NOT_FOUND);
            }

            Institution institution = existing.get();
            String nomFormate = updatedInstitution.getName() != null ? updatedInstitution.getName().trim().toUpperCase() : null;
            institution.setName(nomFormate);
            institution.setUpdated_at(updatedInstitution.getUpdated_at());

            Institution saved = institutionService.save(institution);
            return ResponseEntity.ok(new ApiResponse<>(true, "Institution mise à jour avec succès", saved));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour de l'Institution", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<Institution> existing = institutionService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Institution non trouvée", null), HttpStatus.NOT_FOUND);
            }

            institutionService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Institution supprimée avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression de l'Institution", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
