package com.tpc.tpcgestpaie.localapp.controller.administration;

import com.tpc.tpcgestpaie.localapp.dto.administration.AbsenceDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.SoldeCongeResponseDTO;
import com.tpc.tpcgestpaie.localapp.helper.AbsenceHelper;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.AbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.TypeAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.TempsDeTravailRepository;
import com.tpc.tpcgestpaie.localapp.service.*;
import com.tpc.tpcgestpaie.localapp.service.administration.AbsenceService;
import com.tpc.tpcgestpaie.localapp.service.administration.CreditCongeService;
import com.tpc.tpcgestpaie.localapp.service.paie.SoldeCongeCalculator;
import com.tpc.tpcgestpaie.localapp.service.paie.TempsDeTravailService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(
        name = "Gestion administrative/Gestion des Absences",
        description = "API complète de gestion des absences : création, modification, suppression, consultation paginée et calcul des soldes de congé par employé ou entreprise"
)
@RestController
@RequestMapping("/api/administration/absences")
public class AbsenceController {

    private final AbsenceService absenceService;
    private final AbsenceHelper absenceHelper;
    private final UserService userService;
    private final EmployeService employeService;
    private final ContratEmployeService contratEmployeService;
    private final CreditCongeService creditCongeService;
    private final RequestHelper requestHelper;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final TypeAbsenceRepository typeAbsenceRepository;
    private final AbsenceRepository absenceRepository;
    private final SoldeCongeCalculator soldeCongeCalculator;
    private final TempsDeTravailService tempsDeTravailService;
    private final TempsDeTravailRepository tempsDeTravailRepository;

    public AbsenceController(
            AbsenceService absenceService,
            AbsenceHelper absenceHelper,
            UserService userService,
            EmployeService employeService, ContratEmployeService contratEmployeService, CreditCongeService creditCongeService,
            RequestHelper requestHelper,
            AuditLogService auditService,
            NotificationService notificationService, EmployeRepository employeRepository, ContratEmployeRepository contratEmployeRepository,
            TypeAbsenceRepository typeAbsenceRepository, AbsenceRepository absenceRepository, SoldeCongeCalculator soldeCongeCalculator, TempsDeTravailService tempsDeTravailService, TempsDeTravailRepository tempsDeTravailRepository) {
        this.absenceService = absenceService;
        this.absenceHelper = absenceHelper;
        this.userService = userService;
        this.employeService = employeService;
        this.contratEmployeService = contratEmployeService;
        this.creditCongeService = creditCongeService;
        this.requestHelper = requestHelper;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.employeRepository = employeRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.typeAbsenceRepository = typeAbsenceRepository;
        this.absenceRepository = absenceRepository;
        this.soldeCongeCalculator = soldeCongeCalculator;
        this.tempsDeTravailService = tempsDeTravailService;
        this.tempsDeTravailRepository = tempsDeTravailRepository;
    }

    // === ENDPOINTS PAGINÉS ===

    @Operation(
            summary = "Lister toutes les absences avec pagination",
            description = "Récupère la liste complète de toutes les absences du système avec pagination, tri et filtrage"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Liste paginée des absences récupérée avec succès",
                                      "data": {
                                        "content": [
                                          {
                                            "id": 1,
                                            "employeId": 123,
                                            "typeAbsenceId": 5,
                                            "dateDebut": "2024-03-01",
                                            "dateFin": "2024-03-05",
                                            "libelle": "Congé maladie",
                                            "modeJouissance": "DEDUCTION",
                                            "conditionAcceptation": "A_DEDUIRE_DES_CONGES"
                                          }
                                        ],
                                        "currentPage": 0,
                                        "totalItems": 50,
                                        "totalPages": 5,
                                        "size": 10
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @Parameter(description = "Numéro de page (commence à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "desc", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<AbsenceDTO> absencesPage = absenceService.findAll(pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", absencesPage.getContent());
            responseData.put("currentPage", absencesPage.getNumber());
            responseData.put("totalItems", absencesPage.getTotalElements());
            responseData.put("totalPages", absencesPage.getTotalPages());
            responseData.put("size", absencesPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des absences récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des absences: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister les absences par entreprise avec pagination",
            description = "Récupère toutes les absences d'une entreprise spécifique avec pagination et tri"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des absences de l'entreprise récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/entreprise/{companyId}")
    public ResponseEntity<ApiResponse<?>> getByCompany(
            @Parameter(description = "Identifiant de l'entreprise", required = true, example = "1")
            @PathVariable Long companyId,
            @Parameter(description = "Numéro de page", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<AbsenceDTO> absencesPage = absenceService.findByCompany(companyId, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", absencesPage.getContent());
            responseData.put("currentPage", absencesPage.getNumber());
            responseData.put("totalItems", absencesPage.getTotalElements());
            responseData.put("totalPages", absencesPage.getTotalPages());
            responseData.put("size", absencesPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des absences de l'entreprise récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des absences de l'entreprise: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister les absences par employé avec pagination",
            description = "Récupère l'historique complet des absences d'un employé spécifique avec pagination et tri"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des absences de l'employé récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/employe/{employeId}")
    public ResponseEntity<ApiResponse<?>> getByEmploye(
            @Parameter(description = "Identifiant de l'employé", required = true, example = "123")
            @PathVariable Long employeId,
            @Parameter(description = "Numéro de page", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<AbsenceDTO> absencesPage = absenceService.findByEmploye(employeId, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", absencesPage.getContent());
            responseData.put("currentPage", absencesPage.getNumber());
            responseData.put("totalItems", absencesPage.getTotalElements());
            responseData.put("totalPages", absencesPage.getTotalPages());
            responseData.put("size", absencesPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des absences de l'employé récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des absences de l'employé: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer une absence par son identifiant",
            description = "Retourne les détails complets d'une absence spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Absence trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Absence non trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": false,
                                      "message": "Absence non trouvée avec l'ID: 999",
                                      "data": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(
            @Parameter(description = "Identifiant de l'absence", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Optional<AbsenceDTO> absenceOpt = absenceService.findById(id);
            if (absenceOpt.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Absence trouvée", absenceOpt.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Absence non trouvée avec l'ID: " + id, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération de l'absence: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Créer une nouvelle absence",
            description = """
                    Enregistre une nouvelle absence pour un employé.
                    
                    Processus automatique :
                    - Validation des champs obligatoires
                    - Vérification du contrat employé
                    - Attribution automatique de l'employé, entreprise et crédit congé
                    - Enregistrement de l'audit (qui a créé, quand, depuis quelle IP)
                    - Envoi de notification
                    
                    Requis :
                    - contratEmployeId : ID du contrat
                    - dateDebut : Date de début
                    - dateFin : Date de fin
                    - typeAbsenceId : Type d'absence
                    - modeJouissance : Mode de jouissance
                    - conditionAcceptation : Condition d'acceptation
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Absence créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Absence créée avec succès",
                                      "data": {
                                        "id": 1,
                                        "employeId": 123,
                                        "contratEmployeId": 45,
                                        "typeAbsenceId": 5,
                                        "dateDebut": "2024-03-01",
                                        "dateFin": "2024-03-05",
                                        "libelle": "Congé maladie",
                                        "modeJouissance": "DEDUCTION",
                                        "conditionAcceptation": "A_DEDUIRE_DES_CONGES",
                                        "creditCongeId": 10
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Erreurs de validation",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": false,
                                      "message": "Erreurs de validation",
                                      "data": [
                                        {
                                          "field": "dateDebut",
                                          "message": "La date de début est obligatoire"
                                        },
                                        {
                                          "field": "dateFin",
                                          "message": "La date de fin est obligatoire"
                                        }
                                      ]
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur (crédit congé non trouvé ou autre)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": false,
                                      "message": "Aucun crédit congé actif trouvé pour l'employé avec l'ID: 123",
                                      "data": null
                                    }
                                    """
                            )
                    )
            )
    })
    @Transactional
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(
            @Parameter(
                    description = "Données de l'absence à créer",
                    required = true,
                    schema = @Schema(implementation = AbsenceDTO.class)
            )
            @RequestBody AbsenceDTO absenceDTO) {
        try {
            User currentUser = userService.getCurrentUser();

            List<ErrorResponse> errors = absenceHelper.getInvalidFieldMessages(absenceDTO);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            absenceDTO.setCreatedAt(LocalDateTime.now());
            absenceDTO.setAddedById(currentUser.getId());

            ContratEmploye contratEmploye = contratEmployeRepository.findById(absenceDTO.getContratEmployeId())
                    .orElseThrow(() -> new RuntimeException("ContratEmploye non trouvé"));
            Employe employe = contratEmploye.getEmploye();
            Company company = contratEmploye.getCompany();

            absenceDTO.setEmployeId(employe.getId());
            absenceDTO.setCompanyId(company.getId());
            absenceDTO.setContratEmployeId(contratEmploye.getId());

            Optional<TypeAbsence> typeAbsenceOpt = typeAbsenceRepository.findById(absenceDTO.getTypeAbsenceId());

            CreditConge creditConge = creditCongeService.getCreditCongeActifByIdEmploye(employe.getId());

            if (creditConge != null) {
                absenceDTO.setCreditCongeId(creditConge.getId());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(false,
                                "Aucun crédit congé actif trouvé pour l'employé avec l'ID: " + employe.getId(), null));
            }

            AbsenceDTO saved = absenceService.save(absenceDTO);

            // Log & Notification
            auditService.log(
                    "Création d'une absence",
                    "absences",
                    currentUser.getId(),
                    "Ajout d'une absence par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            notificationService.createNotification(
                    currentUser,
                    "Nouvelle absence",
                    "Une absence a été enregistrée avec succès 📆"
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Absence créée avec succès", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la création de l'absence: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Mettre à jour une absence existante",
            description = """
                    Modifie les informations d'une absence existante.
                    
                    Processus :
                    - Vérification de l'existence de l'absence
                    - Validation des nouvelles données
                    - Mise à jour des champs modifiables
                    - Enregistrement de l'audit
                    - Notification
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Absence mise à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Erreurs de validation",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Absence non trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": false,
                                      "message": "Absence non trouvée avec l'ID: 999",
                                      "data": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(
            @Parameter(description = "Identifiant de l'absence à modifier", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de l'absence", required = true)
            @RequestBody AbsenceDTO updatedDto) {
        try {
            Optional<AbsenceDTO> existingOpt = absenceService.findById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Absence non trouvée avec l'ID: " + id, null));
            }

            AbsenceDTO existingAbsence = existingOpt.get();

            // Validation des champs
            List<ErrorResponse> errors = absenceHelper.getInvalidFieldMessagesForUpdate(id, updatedDto);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            User currentUser = userService.getCurrentUser();

            // Mettre à jour les champs
            existingAbsence.setDateDebut(updatedDto.getDateDebut());
            existingAbsence.setDateFin(updatedDto.getDateFin());
            existingAbsence.setTypeAbsenceId(updatedDto.getTypeAbsenceId());
            existingAbsence.setLibelle(updatedDto.getLibelle());
            existingAbsence.setModeJouissance(updatedDto.getModeJouissance());
            existingAbsence.setConditionAcceptation(updatedDto.getConditionAcceptation());
            existingAbsence.setAddedById(currentUser.getId());

            AbsenceDTO saved = absenceService.save(existingAbsence);

            // Log
            auditService.log(
                    "Mise à jour d'une absence",
                    "absences",
                    currentUser.getId(),
                    "Mise à jour d'une absence par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            // Notification
            notificationService.createNotification(
                    currentUser,
                    "Absence modifiée",
                    "Une absence a été modifiée ✏️"
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Absence mise à jour avec succès", saved));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la mise à jour de l'absence: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Supprimer une absence",
            description = "Supprime définitivement une absence du système. Cette action est irréversible et enregistre un audit trail."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Absence supprimée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Absence supprimée avec succès",
                                      "data": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Absence non trouvée",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(
            @Parameter(description = "Identifiant de l'absence à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Optional<AbsenceDTO> absenceOpt = absenceService.findById(id);
            if (absenceOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Absence non trouvée avec l'ID: " + id, null));
            }

            User currentUser = userService.getCurrentUser();

            // Suppression
            absenceService.deleteById(id);

            // Log
            auditService.log(
                    "Suppression d'une absence",
                    "absences",
                    currentUser.getId(),
                    "Suppression d'une absence par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            // Notification
            notificationService.createNotification(
                    currentUser,
                    "Absence supprimée",
                    "Une absence a été supprimée 🗑️"
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Absence supprimée avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la suppression de l'absence: " + e.getMessage(), null));
        }
    }

    // === ENDPOINTS SPÉCIFIQUES (non paginés) ===

    @Operation(
            summary = "Calculer le solde de congés d'un contrat employé",
            description = """
                    Calcule le solde de congés actuel pour un contrat employé spécifique.
                    
                    Retourne :
                    - Nombre de jours de congés acquis
                    - Nombre de jours consommés
                    - Solde restant
                    - Détails du calcul
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Solde de congés récupéré avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Solde de congés récupéré avec succès.",
                                      "data": {
                                        "contratEmployeId": 45,
                                        "employeNom": "Dupont",
                                        "employePrenom": "Jean",
                                        "joursAcquis": 30,
                                        "joursConsommes": 12,
                                        "soldeRestant": 18,
                                        "details": {...}
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Aucun solde trouvé pour ce contrat",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": false,
                                      "message": "Aucun solde de congé trouvé pour ce contrat.",
                                      "data": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur lors du calcul",
                    content = @Content
            )
    })
    @GetMapping("/solde-conge/{idContratEmploye}")
    public ResponseEntity<ApiResponse<SoldeCongeResponseDTO>> getSoldeConge(
            @Parameter(description = "Identifiant du contrat employé", required = true, example = "45")
            @PathVariable Long idContratEmploye) {
        try {
            // Calcul du solde de congés pour le contrat donné
            SoldeCongeResponseDTO dto = soldeCongeCalculator.calculerSoldeConge(idContratEmploye);

            if (dto == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Aucun solde de congé trouvé pour ce contrat.", null));
            }

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Solde de congés récupéré avec succès.", dto)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Erreur lors de la récupération du solde de congés : " + e.getMessage(),
                            null
                    ));
        }
    }

    @Operation(
            summary = "Calculer les soldes de congés par entreprise",
            description = "Retourne le solde de congés de tous les employés d'une entreprise donnée (utile pour les rapports RH)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des soldes récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Liste des soldes congés de l'entreprise récupérée avec succès",
                                      "data": [
                                        {
                                          "contratEmployeId": 45,
                                          "employeNom": "Dupont",
                                          "employePrenom": "Jean",
                                          "soldeRestant": 18
                                        },
                                        {
                                          "contratEmployeId": 46,
                                          "employeNom": "Martin",
                                          "employePrenom": "Marie",
                                          "soldeRestant": 25
                                        }
                                      ]
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/solde-conge/entreprise/{idEntreprise}")
    public ResponseEntity<?> getSoldeParEntreprise(
            @Parameter(description = "Identifiant de l'entreprise", required = true, example = "1")
            @PathVariable Long idEntreprise) {
        try {
            List<SoldeCongeResponseDTO> solde = contratEmployeService.calculerSoldeCongeParEntreprise(idEntreprise);
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des soldes congés de l'entreprise récupérée avec succès", solde));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des soldes congés de l'entreprise: " + e.getMessage(), null));
        }
    }
}