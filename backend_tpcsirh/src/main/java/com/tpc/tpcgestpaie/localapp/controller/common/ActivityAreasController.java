package com.tpc.tpcgestpaie.localapp.controller.common;

import com.tpc.tpcgestpaie.localapp.model.ActivityArea;
import com.tpc.tpcgestpaie.localapp.service.ActivityAreaService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.PaginatedResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/common/activity-areas")
public class ActivityAreasController {

    private final ActivityAreaService activityAreaService;
    public ActivityAreasController(ActivityAreaService activityAreaService) {
        this.activityAreaService = activityAreaService;
    }

//    @Transactional
//    @GetMapping("/list")
//    public ResponseEntity<?> getAllActivityAreas() {
//        try {
//            List<ActivityArea> areas = activityAreaService.getAll();
//            ApiResponse<List<ActivityArea>> response = new ApiResponse<>(true, "Liste des zones d'activité récupérée avec succès", areas);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            ApiResponse<Object> errorResponse = new ApiResponse<>(false, "Une erreur est survenue lors de la récupération des zones d'activité : " + e.getMessage(), null);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }

    // NOUVELLE méthode avec pagination
    @Transactional
    @GetMapping("/list")
    public ResponseEntity<?> getAllActivityAreasPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ActivityArea> areasPage = activityAreaService.getAll(pageable);

            // CORRECTION : Utiliser List<ActivityArea> comme type générique
            PaginatedResponse<List<ActivityArea>> response = new PaginatedResponse<>(
                    true,
                    "Liste paginée des zones d'activité récupérée avec succès",
                    areasPage.getContent(),  // ← List<ActivityArea>
                    areasPage.getNumber(),
                    areasPage.getSize(),
                    areasPage.getTotalElements(),
                    areasPage.getTotalPages(),
                    areasPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Object> errorResponse = new ApiResponse<>(false,
                    "Une erreur est survenue lors de la récupération des zones d'activité : " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    // Récupérer un diplôme par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        try {
            Optional<ActivityArea> activityArea = Optional.ofNullable(activityAreaService.getById(id));
            if (activityArea.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Domaine trouvé", activityArea.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Domaine non trouvé", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la recherche du domaine", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Créer un nouveau diplôme
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ActivityArea activityArea) {
        try {
            if (activityArea.getName() == null || activityArea.getName().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le nom est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            String nomFormate = activityArea.getName().trim().toUpperCase();

            if (activityAreaService.existsByName(nomFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(false, "Ce Domaine existe déjà.", null));
            }

            activityArea.setName(nomFormate); // Conversion ici
            ActivityArea saved = activityAreaService.create(activityArea);

            return new ResponseEntity<>(new ApiResponse<>(true, "Domaine créé avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création du Domaine", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody ActivityArea updateActivityArea) {
        try {
            ActivityArea activityArea = activityAreaService.getById(id);
            if (activityArea == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Domaine non trouvé", null), HttpStatus.NOT_FOUND);
            }

            // Vérifier si un autre domaine a déjà ce nom
            boolean existsWithName = activityAreaService.existsByName(updateActivityArea.getName());
            if (existsWithName && !updateActivityArea.getName().equals(activityArea.getName())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Le nom du domaine existe déjà", null));
            }

            activityArea.setName(updateActivityArea.getName());
            activityArea.setUpdatedAt(updateActivityArea.getUpdatedAt());

            ActivityArea saved = activityAreaService.create(activityArea);
            return ResponseEntity.ok(new ApiResponse<>(true, "Domaine mis à jour avec succès", saved));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour du domaine", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Supprimer un diplôme
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            Optional<ActivityArea> existing = Optional.ofNullable(activityAreaService.getById(id));
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Domaine non trouvé", null), HttpStatus.NOT_FOUND);
            }

            activityAreaService.delete(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Domaine supprimé avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression du Domaine", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}