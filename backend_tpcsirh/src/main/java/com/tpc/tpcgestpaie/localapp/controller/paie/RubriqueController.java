package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.dto.accessoire.RubriqueResponseDTO;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.helper.RubriqueHelper;
import com.tpc.tpcgestpaie.localapp.model.Rubrique;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.service.AuditLogService;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import com.tpc.tpcgestpaie.localapp.service.RubriqueService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import com.tpc.tpcgestpaie.localapp.util.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
    @RequestMapping("/api/paie/rubriques")
public class RubriqueController {

    private final RubriqueService rubriqueService;
    private final RubriqueHelper rubriqueHelper;
    private final UserService userService;
    private final RequestHelper resquestHelper;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private final CompanyRepository companyRepository;

    public RubriqueController(RubriqueService rubriqueService, RequestHelper rubriqueHelper, RubriqueHelper rubriqueHelper1, UserService userService, RequestHelper resquestHelper, AuditLogService auditService, NotificationService notificationService, CompanyRepository companyRepository) {
        this.rubriqueService = rubriqueService;
        this.rubriqueHelper = rubriqueHelper1;
        this.userService = userService;
        this.resquestHelper = resquestHelper;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.companyRepository = companyRepository;
    }


    // 1. Pagination pour toutes les rubriques
    @GetMapping("/paginated")
    public ResponseEntity<?> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "libelle") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Rubrique> rubriquesPage = rubriqueService.findAllPaginated(pageable);

            PaginatedResponse<List<Rubrique>> response = new PaginatedResponse<>(
                    true,
                    "Liste paginée des rubriques récupérée avec succès",
                    rubriquesPage.getContent(),
                    rubriquesPage.getNumber(),
                    rubriquesPage.getSize(),
                    rubriquesPage.getTotalElements(),
                    rubriquesPage.getTotalPages(),
                    rubriquesPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des rubriques", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2. Pagination pour les rubriques fixes par entreprise
    @GetMapping("/fixes/{companyId}/paginated")
    public ResponseEntity<?> getRubriquesDistinctParEntreprisePaginated(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "libelle") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            // Vérifier si l'entreprise existe
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Entreprise introuvable", null)
                );
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<RubriqueResponseDTO> rubriquesPage = rubriqueService.getRubriquesParEntreprisePaginated(companyId, pageable);

            PaginatedResponse<List<RubriqueResponseDTO>> response = new PaginatedResponse<>(
                    true,
                    "Liste paginée des rubriques distinctes récupérée",
                    rubriquesPage.getContent(),
                    rubriquesPage.getNumber(),
                    rubriquesPage.getSize(),
                    rubriquesPage.getTotalElements(),
                    rubriquesPage.getTotalPages(),
                    rubriquesPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la récupération des rubriques", null)
            );
        }
    }

    // 3. Pagination pour les rubriques variables par entreprise
    @GetMapping("/variables/{companyId}/paginated")
    public ResponseEntity<?> getRubriquesVariablesParEntreprisePaginated(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "libelle") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Entreprise introuvable", null)
                );
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<RubriqueResponseDTO> rubriquesPage = rubriqueService.getRubriquesVariablesParEntreprisePaginated(companyId, pageable);

            PaginatedResponse<List<RubriqueResponseDTO>> response = new PaginatedResponse<>(
                    true,
                    "Liste paginée des rubriques variables récupérée",
                    rubriquesPage.getContent(),
                    rubriquesPage.getNumber(),
                    rubriquesPage.getSize(),
                    rubriquesPage.getTotalElements(),
                    rubriquesPage.getTotalPages(),
                    rubriquesPage.isLast()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la récupération des rubriques variables", null)
            );
        }
    }

    @GetMapping("/13e-mois")
    public ResponseEntity<?> getId13eMois() {
        try {
            Long id = rubriqueService.getId13eMois();
            return ResponseEntity.ok(new ApiResponse<>(true, "ID 13e mois récupéré avec succès", id));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Rubrique 13e mois introuvable", null),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/salaire-moyen")
    public ResponseEntity<?> getIDSalaireMoyen() {
        try {
            Long id = rubriqueService.getId1SalalireMoyen();
            return ResponseEntity.ok(new ApiResponse<>(true, "ID salaire moyen récupéré avec succès", id));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Rubrique SALAIRE MOYEN introuvable", null),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/primes-exceptionnelles")
    public ResponseEntity<?> getIdPrimesExceptionnelles() {
        try {
            Long id = rubriqueService.getIdPrimesExceptionnelles();
            return ResponseEntity.ok(new ApiResponse<>(true, "ID primes exceptionnelles récupéré avec succès", id));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Rubrique primes exceptionnelles introuvable", null),
                    HttpStatus.NOT_FOUND);
        }
    }

    // Lister toutes les rubriques
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Rubrique> rubriques = rubriqueService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des rubriques récupérée avec succès", rubriques));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des rubriques", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fixes/{companyId}")
    public ResponseEntity<?> getRubriquesDistinctParEntreprise(@PathVariable Long companyId) {
        try {
            // Vérifier si l'entreprise existe
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Entreprise introuvable", null)
                );
            }

            // Récupérer la liste des rubriques au format DTO
            List<RubriqueResponseDTO> rubriques = rubriqueService.getRubriquesParEntreprise(companyId);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des rubriques distinctes récupérée", rubriques)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la récupération des rubriques", null)
            );
        }
    }

    @GetMapping("/variables/{companyId}")
    public ResponseEntity<?> getRubriquesVariablesParEntreprise(@PathVariable Long companyId) {
        try {
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Entreprise introuvable", null)
                );
            }

            List<RubriqueResponseDTO> rubriques = rubriqueService.getRubriquesVariablesParEntreprise(companyId);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des rubriques variables récupérée", rubriques)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Erreur lors de la récupération des rubriques variables", null)
            );
        }
    }


    // Récupérer une rubrique par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<Rubrique> rubrique = rubriqueService.findById(id);
            if (rubrique.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Rubrique trouvée", rubrique.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Rubrique non trouvée", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de la rubrique", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Créer une rubrique
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Rubrique rubrique) {
        try {
            User currentUser = userService.getCurrentUser();
            List<ErrorResponse> errors = rubriqueHelper.getInvalidFieldMessages(rubrique);

            if (!errors.isEmpty()) {
                // Retourne une réponse avec une liste d'erreurs
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            String libelleFormate = rubrique.getLibelle().trim().toUpperCase();

            rubrique.setLibelle(libelleFormate);
            rubrique.setCreated_at(LocalDateTime.now());
            rubrique.setRubriqueSysteme("NON");
//            rubrique.setAdded_by(currentUser);
            Rubrique savedRubrique = rubriqueService.save(rubrique);
            return new ResponseEntity<>(new ApiResponse<>(true, "Rubrique créée avec succès", savedRubrique), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création de la rubrique", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Rubrique updatedRubrique) {
        try {
            Optional<Rubrique> existing = rubriqueService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Rubrique non trouvée", null), HttpStatus.NOT_FOUND);
            }

            User currentUser = userService.getCurrentUser();
            List<ErrorResponse> errors = rubriqueHelper.getInvalidFieldMessages(updatedRubrique);

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            Rubrique rubrique = existing.get();

            String libelleFormate = updatedRubrique.getLibelle() != null ?
                    updatedRubrique.getLibelle().trim().toUpperCase() : rubrique.getLibelle();

            rubrique.setLibelle(libelleFormate);
            rubrique.setNature(updatedRubrique.getNature());
            rubrique.setRubriqueImposable(updatedRubrique.getRubriqueImposable());
            rubrique.setNiveauAffichage(updatedRubrique.getNiveauAffichage());
            rubrique.setCoefficient(updatedRubrique.getCoefficient());
            rubrique.setNumeroOrdre(updatedRubrique.getNumeroOrdre());
            rubrique.setUpdated_at(LocalDateTime.now());

            Rubrique savedRubrique = rubriqueService.save(rubrique);

            // Log d’audit
            String ipAddress = resquestHelper.getClientIp();
            String userAgent = resquestHelper.getUserAgent();

            auditService.log(
                    "Mise à jour d'une rubrique",
                    "rubriques",
                    currentUser.getId(),
                    "Mise à jour de la rubrique par " + currentUser.getFullName() + " avec le rôle",
                    currentUser,
                    ipAddress,
                    userAgent
            );

            // Notification
            notificationService.createNotification(
                    currentUser,
                    "Rubrique mise à jour",
                    "La rubrique a été modifiée avec succès. ✏️"
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Rubrique mise à jour avec succès", savedRubrique));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour de la rubrique", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  Suppression logique (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<Rubrique> existing = rubriqueService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Rubrique non trouvée", null), HttpStatus.NOT_FOUND);
            }

            User currentUser = userService.getCurrentUser();

            Rubrique rubrique = existing.get();
            rubrique.setDeleted_at(LocalDateTime.now());
            rubriqueService.save(rubrique);

            // Log d’audit
            String ipAddress = resquestHelper.getClientIp();
            String userAgent = resquestHelper.getUserAgent();

            auditService.log(
                    "Suppression d'une rubrique",
                    "rubriques",
                    currentUser.getId(),
                    "Rubrique supprimée par " + currentUser.getFullName() + " avec le rôle",
                    currentUser,
                    ipAddress,
                    userAgent
            );

            // Notification
            notificationService.createNotification(
                    currentUser,
                    "Rubrique supprimée",
                    "La rubrique a été supprimée avec succès 🗑️"
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Rubrique supprimée avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression de la rubrique", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
