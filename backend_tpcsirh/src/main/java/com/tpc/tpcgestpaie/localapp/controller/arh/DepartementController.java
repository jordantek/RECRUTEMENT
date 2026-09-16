package com.tpc.tpcgestpaie.localapp.controller.arh;

import com.tpc.tpcgestpaie.localapp.model.Departement;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.DepartementService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/arh/departements")
public class DepartementController {

    private final DepartementService departementService;
    private final CompanyService companyService;
    public DepartementController(DepartementService departementService, CompanyService companyService) {
        this.departementService = departementService;
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(value = "companyId", required = false) Long companyId) {
        try {
            List<Departement> list;
            if (companyId != null) {
                list = departementService.findByCompanyId(companyId);
            } else {
                list = departementService.findAll();
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des départements récupérée avec succès", list));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des départements", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer un département par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<Departement> departement = departementService.findById(id);
            if (departement.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Département trouvé", departement.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Département non trouvé", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la recherche du département", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Departement departement) {
        try {
            if (departement.getLibelle() == null || departement.getLibelle().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le libellé est vide", null), HttpStatus.BAD_REQUEST);
            }

            if (departement.getCompany_id() == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "L'ID de l'entreprise liée au département est obligatoire", null), HttpStatus.BAD_REQUEST);
            }

            String libelleFormate = departement.getLibelle().trim().toUpperCase();

            // **Vérification existence par libellé + company_id**
            if (departementService.existsByLibelleAndCompanyId(libelleFormate, departement.getCompany_id())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Ce département existe déjà pour cette entreprise.", null));
            }

            Departement saved = departementService.save(departement);
            return new ResponseEntity<>(new ApiResponse<>(true, "Département créé avec succès", saved), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création du département", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Mettre à jour un département existant
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Departement updatedDepartement) {
        try {
            Optional<Departement> existing = departementService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Département non trouvé", null), HttpStatus.NOT_FOUND);
            }
            Departement departement = existing.get();
            departement.setLibelle(updatedDepartement.getLibelle());
            departement.setDescription(updatedDepartement.getDescription());
            departement.setUpdatedAt(updatedDepartement.getUpdatedAt());
            departement.setCompany(updatedDepartement.getCompany());
            departement.setAdded_by(updatedDepartement.getAdded_by());

            Departement saved = departementService.save(departement);
            return ResponseEntity.ok(new ApiResponse<>(true, "Département mis à jour avec succès", saved));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour du département", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Supprimer un département
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<Departement> existing = departementService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Département non trouvé", null), HttpStatus.NOT_FOUND);
            }

            departementService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Département supprimé avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression du département", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
