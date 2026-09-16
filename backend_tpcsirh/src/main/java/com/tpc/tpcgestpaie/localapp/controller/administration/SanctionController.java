package com.tpc.tpcgestpaie.localapp.controller.administration;

import com.tpc.tpcgestpaie.localapp.dto.administration.SanctionDTO;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.helper.SanctionHelper;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.AuditLogService;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.administration.SanctionService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import com.tpc.tpcgestpaie.localapp.util.JsonCleaner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(
        name = "Gestion administrative/Sanctions",
        description = "API de gestion des sanctions disciplinaires : déclaration, suivi, consultation par entreprise/employé et gestion des mesures disciplinaires"
)
@RestController
@RequestMapping("/api/administration/sanctions")
public class SanctionController {

    private final SanctionService sanctionService;
    private final SanctionHelper sanctionHelper;
    private final UserService userService;
    private final RequestHelper requestHelper;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private final CompanyRepository companyRepository;
    private final EmployeRepository employeRepository;

    public SanctionController(
            SanctionService sanctionService,
            SanctionHelper sanctionHelper,
            UserService userService,
            RequestHelper requestHelper,
            AuditLogService auditService,
            NotificationService notificationService,
            CompanyRepository companyRepository,
            EmployeRepository employeRepository
    ) {
        this.sanctionService = sanctionService;
        this.sanctionHelper = sanctionHelper;
        this.userService = userService;
        this.requestHelper = requestHelper;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.companyRepository = companyRepository;
        this.employeRepository = employeRepository;
    }

    // === ENDPOINTS PAGINÉS ===

    @Operation(
            summary = "Lister toutes les sanctions avec pagination",
            description = "Récupère la liste complète de toutes les sanctions disciplinaires déclarées dans le système avec pagination et tri"
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
                      "message": "Liste paginée des sanctions récupérée avec succès",
                      "data": {
                        "content": [
                          {
                            "id": 1,
                            "employeId": 123,
                            "companyId": 5,
                            "dateSanction": "2024-03-15",
                            "typeSanction": "Avertissement écrit",
                            "motif": "Retard répété aux réunions",
                            "gravite": "MOYENNE",
                            "statut": "ACTIF",
                            "dateFinValidite": "2024-06-15",
                            "createdAt": "2024-03-15T14:30:00",
                            "addedById": 1
                          }
                        ],
                        "currentPage": 0,
                        "totalItems": 12,
                        "totalPages": 2,
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

            Page<SanctionDTO> sanctionsPage = sanctionService.findAll(pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", sanctionsPage.getContent());
            responseData.put("currentPage", sanctionsPage.getNumber());
            responseData.put("totalItems", sanctionsPage.getTotalElements());
            responseData.put("totalPages", sanctionsPage.getTotalPages());
            responseData.put("size", sanctionsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des sanctions récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des sanctions: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister les sanctions par entreprise",
            description = "Récupère toutes les sanctions disciplinaires déclarées pour une entreprise spécifique avec pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée des sanctions pour l'entreprise récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Entreprise introuvable",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Entreprise introuvable",
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
    @GetMapping("/par-entreprise/{companyId}")
    public ResponseEntity<ApiResponse<?>> getByCompany(
            @Parameter(description = "Identifiant de l'entreprise", required = true, example = "5")
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
            // Vérifier l'existence de l'entreprise
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise introuvable", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<SanctionDTO> sanctionsPage = sanctionService.findByCompanyId(companyId, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", sanctionsPage.getContent());
            responseData.put("currentPage", sanctionsPage.getNumber());
            responseData.put("totalItems", sanctionsPage.getTotalElements());
            responseData.put("totalPages", sanctionsPage.getTotalPages());
            responseData.put("size", sanctionsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des sanctions pour l'entreprise récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des sanctions de l'entreprise: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister les sanctions par employé",
            description = "Récupère l'historique des sanctions disciplinaires d'un employé spécifique avec pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée des sanctions de l'employé récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employé introuvable",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Employé introuvable",
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
    @GetMapping("/par-employe/{employeId}")
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
            // Vérifier l'existence de l'employé
            if (!employeRepository.existsById(employeId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Employé introuvable", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<SanctionDTO> sanctionsPage = sanctionService.findByEmployeId(employeId, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", sanctionsPage.getContent());
            responseData.put("currentPage", sanctionsPage.getNumber());
            responseData.put("totalItems", sanctionsPage.getTotalElements());
            responseData.put("totalPages", sanctionsPage.getTotalPages());
            responseData.put("size", sanctionsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des sanctions de l'employé récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des sanctions de l'employé: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer une sanction par son identifiant",
            description = "Retourne les détails complets d'une sanction disciplinaire spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Sanction trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Sanction trouvée",
                      "data": {
                        "id": 1,
                        "employeId": 123,
                        "companyId": 5,
                        "dateSanction": "2024-03-15",
                        "typeSanction": "Mise à pied",
                        "motif": "Absence non justifiée pendant 3 jours consécutifs",
                        "gravite": "ELEVEE",
                        "dureeJours": 3,
                        "statut": "ACTIF",
                        "dateFinValidite": "2024-09-15",
                        "observations": "L'employé a été averti oralement auparavant",
                        "createdAt": "2024-03-15T14:30:00",
                        "addedById": 1
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Sanction non trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Sanction non trouvée avec l'ID: 999",
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
            @Parameter(description = "Identifiant de la sanction", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Optional<SanctionDTO> sanctionOpt = sanctionService.findById(id);
            if (sanctionOpt.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Sanction trouvée", sanctionOpt.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Sanction non trouvée avec l'ID: " + id, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération de la sanction: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Créer une nouvelle sanction",
            description = """
                Enregistre une nouvelle sanction disciplinaire.
                
                Validations effectuées :
                - Champs obligatoires : employeId, companyId, dateSanction, typeSanction, motif
                - Date de sanction ne peut pas être dans le futur
                - Gravité doit correspondre aux valeurs autorisées (LEGERE, MOYENNE, ELEVEE)
                - Durée en jours doit être positive si renseignée
                - Date de fin de validité doit être postérieure à la date de sanction
                - Vérification de l'existence de l'employé et de l'entreprise
                
                Fonctionnalités incluses :
                - Audit automatique de la création
                - Notification aux responsables
                - Nettoyage des données pour éviter les injections
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Sanction créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Sanction créée avec succès",
                      "data": {
                        "id": 1,
                        "employeId": 123,
                        "companyId": 5,
                        "dateSanction": "2024-03-15",
                        "typeSanction": "Blâme",
                        "motif": "Non-respect des procédures de sécurité",
                        "gravite": "MOYENNE",
                        "dureeJours": null,
                        "statut": "ACTIF",
                        "dateFinValidite": "2024-06-15",
                        "createdAt": "2024-03-15T14:30:00",
                        "addedById": 1
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
                          "field": "dateSanction",
                          "message": "La date de sanction ne peut pas être dans le futur"
                        },
                        {
                          "field": "typeSanction",
                          "message": "Le type de sanction est obligatoire"
                        },
                        {
                          "field": "gravite",
                          "message": "La gravité doit être LEGERE, MOYENNE ou ELEVEE"
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
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(
            @Parameter(
                    description = "Données de la nouvelle sanction",
                    required = true,
                    schema = @Schema(implementation = SanctionDTO.class)
            )
            @RequestBody SanctionDTO sanctionDTO) {
        try {
            User currentUser = userService.getCurrentUser();

            List<ErrorResponse> errors = sanctionHelper.getInvalidFieldMessages(sanctionDTO);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            sanctionDTO.setCreatedAt(LocalDateTime.now());
            sanctionDTO.setAddedById(currentUser.getId());

            SanctionDTO saved = sanctionService.save(sanctionDTO);

            auditService.log(
                    "Création d'une sanction",
                    "sanctions",
                    currentUser.getId(),
                    "Ajout d'une sanction par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            notificationService.createNotification(
                    currentUser,
                    "Nouvelle sanction",
                    "Une nouvelle sanction a été enregistrée ⚠️"
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Sanction créée avec succès", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la création de la sanction: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Mettre à jour une sanction existante",
            description = """
                Modifie les informations d'une sanction existante.
                
                Validations effectuées :
                - Vérification de l'existence de la sanction
                - Validation des nouvelles données via helper
                - Cohérence des dates et de la gravité
                
                Fonctionnalités incluses :
                - Audit automatique de la modification
                - Notification de mise à jour
                - Nettoyage des champs null dans la réponse
                - Gestion transactionnelle
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Sanction mise à jour avec succès",
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
                    description = "Sanction introuvable",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Sanction introuvable avec l'ID: 999",
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
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(
            @Parameter(description = "Identifiant de la sanction à modifier", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de la sanction", required = true)
            @RequestBody SanctionDTO updatedDto) {

        try {
            Optional<SanctionDTO> existingOpt = sanctionService.findById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Sanction introuvable avec l'ID: " + id, null));
            }

            User currentUser = userService.getCurrentUser();
            List<ErrorResponse> errors = sanctionHelper.getInvalidUpdateFieldMessages(updatedDto);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            updatedDto.setId(id);
            updatedDto.setUpdatedById(currentUser.getId());
            SanctionDTO saved = sanctionService.update(updatedDto);

            // Journalisation
            auditService.log(
                    "Mise à jour d'une sanction",
                    "sanctions",
                    currentUser.getId(),
                    "Mise à jour d'une sanction par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            // Notification
            notificationService.createNotification(
                    currentUser,
                    "Modification d'une sanction avec succès",
                    "La demande de modification d'une sanction a bien été effectuée avec succès"
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Sanction mise à jour avec succès", JsonCleaner.removeNullFields(saved))
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la mise à jour de la sanction: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Supprimer une sanction",
            description = "Effectue une suppression logique (soft delete) d'une sanction. Les données restent en base mais sont marquées comme supprimées."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Sanction supprimée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Sanction supprimée avec succès",
                      "data": null
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Sanction introuvable",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Sanction introuvable avec l'ID: 999",
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
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(
            @Parameter(description = "Identifiant de la sanction à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Optional<SanctionDTO> existingOpt = sanctionService.findById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Sanction introuvable avec l'ID: " + id, null));
            }

            sanctionService.softDelete(id);
            User currentUser = userService.getCurrentUser();

            // Journalisation
            auditService.log(
                    "Suppression d'une sanction",
                    "sanctions",
                    currentUser.getId(),
                    "Suppression d'une sanction par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            // Notification
            notificationService.createNotification(
                    currentUser,
                    "Suppression d'une sanction avec succès",
                    "La demande de suppression d'une sanction a bien été effectuée avec succès"
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Sanction supprimée avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la suppression de la sanction: " + e.getMessage(), null));
        }
    }
}