package com.tpc.tpcgestpaie.localapp.controller.administration;

import com.tpc.tpcgestpaie.localapp.dto.administration.FormationDTO;
import com.tpc.tpcgestpaie.localapp.helper.FormationHelper;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.FormationRepository;
import com.tpc.tpcgestpaie.localapp.service.AuditLogService;
import com.tpc.tpcgestpaie.localapp.service.administration.FormationService;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(
        name = "Gestion administrative/Formations",
        description = "API de gestion des formations professionnelles : planification, inscription, suivi des formations internes/externes et gestion des compétences"
)
@RestController
@RequestMapping("/api/administration/formations")
public class FormationController {

    private final FormationService formationService;
    private final FormationHelper formationHelper;
    private final UserService userService;
    private final RequestHelper requestHelper;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private final CompanyRepository companyRepository;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final FormationRepository formationRepository;

    public FormationController(
            FormationService formationService,
            FormationHelper formationHelper,
            UserService userService,
            RequestHelper requestHelper,
            AuditLogService auditService,
            NotificationService notificationService,
            CompanyRepository companyRepository,
            EmployeRepository employeRepository,
            ContratEmployeRepository contratEmployeRepository,
            FormationRepository formationRepository) {
        this.formationService = formationService;
        this.formationHelper = formationHelper;
        this.userService = userService;
        this.requestHelper = requestHelper;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.companyRepository = companyRepository;
        this.employeRepository = employeRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.formationRepository = formationRepository;
    }

    // === ENDPOINTS PAGINÉS ===

    @Operation(
            summary = "Lister toutes les formations avec pagination",
            description = "Récupère la liste complète de toutes les formations planifiées et passées dans le système avec pagination et tri"
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
                      "message": "Liste paginée des formations récupérée avec succès",
                      "data": {
                        "content": [
                          {
                            "id": 1,
                            "nomFormation": "Gestion de projet Agile",
                            "typeFormation": "INTERNE",
                            "description": "Formation sur les méthodologies Agile",
                            "dateDebut": "2024-03-15T09:00:00",
                            "dateFin": "2024-03-16T17:00:00",
                            "dureeHeures": 16,
                            "organisme": "Service Formation Interne",
                            "lieu": "Salle de conférence A",
                            "cout": 500000,
                            "statut": "PLANIFIE",
                            "companyId": 5,
                            "employeIds": [123, 456],
                            "certificat": "/uploads/certificats/agile_cert.pdf",
                            "createdAt": "2024-01-15T10:30:00",
                            "addedById": 1
                          }
                        ],
                        "currentPage": 0,
                        "totalItems": 25,
                        "totalPages": 3,
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

            Page<FormationDTO> formationsPage = formationService.getAll(pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", formationsPage.getContent());
            responseData.put("currentPage", formationsPage.getNumber());
            responseData.put("totalItems", formationsPage.getTotalElements());
            responseData.put("totalPages", formationsPage.getTotalPages());
            responseData.put("size", formationsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des formations récupérée avec succès", responseData));
        } catch (Exception e) {
            return errorResponse("Erreur lors de la récupération des formations: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Lister les formations par entreprise",
            description = "Récupère toutes les formations organisées pour une entreprise spécifique avec pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée des formations pour l'entreprise récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Entreprise non trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Entreprise non trouvée",
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
    public ResponseEntity<ApiResponse<?>> getFormationsByCompany(
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
            if (!companyRepository.existsById(companyId)) {
                return notFoundResponse("Entreprise non trouvée");
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<FormationDTO> formationsPage = formationService.getAllByCompany(companyId, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", formationsPage.getContent());
            responseData.put("currentPage", formationsPage.getNumber());
            responseData.put("totalItems", formationsPage.getTotalElements());
            responseData.put("totalPages", formationsPage.getTotalPages());
            responseData.put("size", formationsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des formations pour l'entreprise récupérée avec succès", responseData));

        } catch (Exception e) {
            return errorResponse("Erreur lors de la récupération des formations: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Lister les formations par employé",
            description = "Récupère l'historique des formations suivies par un employé spécifique avec pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée des formations pour l'employé récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employé non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Employé non trouvé",
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
    public ResponseEntity<ApiResponse<?>> getFormationsByEmploye(
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
            if (!employeRepository.existsById(employeId)) {
                return notFoundResponse("Employé non trouvé");
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<FormationDTO> formationsPage = formationService.getAllByEmploye(employeId, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", formationsPage.getContent());
            responseData.put("currentPage", formationsPage.getNumber());
            responseData.put("totalItems", formationsPage.getTotalElements());
            responseData.put("totalPages", formationsPage.getTotalPages());
            responseData.put("size", formationsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des formations pour l'employé récupérée avec succès", responseData));
        } catch (Exception e) {
            return errorResponse("Erreur lors de la récupération des formations: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Récupérer une formation par son identifiant",
            description = "Retourne les détails complets d'une formation spécifique, incluant la liste des employés inscrits"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Formation trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Formation trouvée",
                      "data": {
                        "id": 1,
                        "nomFormation": "Cybersécurité avancée",
                        "typeFormation": "EXTERNE",
                        "description": "Formation sur les bonnes pratiques de sécurité",
                        "dateDebut": "2024-04-10T09:00:00",
                        "dateFin": "2024-04-12T17:00:00",
                        "dureeHeures": 24,
                        "organisme": "SecureTech Academy",
                        "lieu": "Centre de formation Lyon",
                        "cout": 750000,
                        "statut": "PLANIFIE",
                        "companyId": 5,
                        "employeIds": [123, 124, 125],
                        "certificat": null,
                        "evaluation": null,
                        "createdAt": "2024-02-15T14:30:00",
                        "addedById": 2
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Formation non trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Formation non trouvée avec l'ID: 999",
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
            @Parameter(description = "Identifiant de la formation", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Optional<FormationDTO> formation = formationService.getById(id);
            if (formation.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Formation trouvée", formation.get()));
            }
            return notFoundResponse("Formation non trouvée avec l'ID: " + id);
        } catch (Exception e) {
            return errorResponse("Erreur lors de la récupération de la formation: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Créer une nouvelle formation",
            description = """
                Planifie une nouvelle formation professionnelle.
                
                Validations effectuées :
                - Champs obligatoires : nomFormation, typeFormation, dateDebut, companyId, employeIds
                - Date de début doit être antérieure ou égale à la date de fin
                - Durée en heures doit être positive
                - Coût doit être positif si renseigné
                - Vérification des contrats des employés avec l'entreprise
                - Validation via helper pour les règles métier spécifiques
                
                Fonctionnalités incluses :
                - Audit automatique de la création
                - Notification aux utilisateurs concernés
                - Vérification des employés éligibles (contrat actif avec l'entreprise)
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Formation créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Formation créée avec succès",
                      "data": {
                        "id": 1,
                        "nomFormation": "Management d'équipe",
                        "typeFormation": "INTERNE",
                        "description": "Formation sur les techniques de management",
                        "dateDebut": "2024-05-20T09:00:00",
                        "dateFin": "2024-05-21T17:00:00",
                        "dureeHeures": 16,
                        "organisme": "Service RH",
                        "lieu": "Salle de réunion B",
                        "cout": 300000,
                        "statut": "PLANIFIE",
                        "companyId": 5,
                        "employeIds": [123, 456, 789],
                        "certificat": null,
                        "createdAt": "2024-04-01T10:30:00",
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
                            examples = {
                                    @ExampleObject(
                                            name = "Validation générale",
                                            value = """
                        {
                          "success": false,
                          "message": "Erreurs de validation",
                          "data": [
                            {
                              "field": "nomFormation",
                              "message": "Le nom de la formation est obligatoire"
                            },
                            {
                              "field": "dateDebut",
                              "message": "La date de début est obligatoire"
                            }
                          ]
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "Validation contrats",
                                            value = """
                        {
                          "success": false,
                          "message": "Erreurs de validation",
                          "data": [
                            {
                              "field": "contrat_inexistant",
                              "message": "L'employé ID 123 n'a pas de contrat avec l'entreprise"
                            }
                          ]
                        }
                        """
                                    )
                            }
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
                    description = "Données de la nouvelle formation",
                    required = true,
                    schema = @Schema(implementation = FormationDTO.class)
            )
            @RequestBody FormationDTO formationDto) {
        try {
            User currentUser = userService.getCurrentUser();

            // Vérification validation
            var errors = formationHelper.getInvalidFieldMessages(formationDto);

            // Vérification contrats employés
            List<ErrorResponse> contratErrors = checkEmployesContracts(formationDto);
            errors.addAll(contratErrors);

            if (!errors.isEmpty()) {
                return validationErrorResponse(errors);
            }

            formationDto.setCreatedAt(LocalDateTime.now());
            formationDto.setAddedById(currentUser.getId());

            FormationDTO saved = formationService.save(formationDto);

            logAndNotify(currentUser, "Ajout d'une formation", "Une formation a été ajoutée avec succès 📚");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Formation créée avec succès", saved));
        } catch (Exception e) {
            return errorResponse("Erreur lors de la création de la formation: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Mettre à jour une formation existante",
            description = """
                Modifie les informations d'une formation existante.
                
                Validations effectuées :
                - Au moins un employé doit être associé à la formation
                - Vérification de l'existence de la formation
                - Validation des nouvelles données
                
                Fonctionnalités incluses :
                - Audit automatique de la modification
                - Notification de mise à jour
                - Gestion transactionnelle des modifications
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Formation mise à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
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
                          "field": "employes",
                          "message": "Au moins un employé doit être associé"
                        }
                      ]
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Formation non trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Formation non trouvée avec l'ID: 999",
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
    @Transactional
    public ResponseEntity<ApiResponse<?>> update(
            @Parameter(description = "Identifiant de la formation à modifier", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de la formation", required = true)
            @RequestBody FormationDTO updatedDto) {
        try {
            // Validation de base
            if (updatedDto.getEmployeIds() == null || updatedDto.getEmployeIds().isEmpty()) {
                return validationErrorResponse(List.of(new ErrorResponse("employes", "Au moins un employé doit être associé")));
            }

            User currentUser = userService.getCurrentUser();

            FormationDTO saved = formationService.update(id, updatedDto, currentUser);

            logAndNotify(currentUser, "Modification d'une formation", "La formation a été modifiée avec succès ✏️");

            return ResponseEntity.ok(new ApiResponse<>(true, "Formation mise à jour avec succès", saved));
        } catch (EntityNotFoundException e) {
            return notFoundResponse(e.getMessage());
        } catch (IllegalArgumentException e) {
            return validationErrorResponse(List.of(new ErrorResponse("global", e.getMessage())));
        } catch (Exception e) {
            return errorResponse("Erreur lors de la mise à jour de la formation: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Supprimer une formation",
            description = "Effectue une suppression logique (soft delete) d'une formation. Les données restent en base mais sont marquées comme supprimées."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Formation supprimée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Formation supprimée avec succès",
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
            @Parameter(description = "Identifiant de la formation à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            formationService.softDelete(id);
            User currentUser = userService.getCurrentUser();

            auditService.log(
                    "Suppression d'une formation",
                    "formations",
                    currentUser.getId(),
                    "Suppression d'une formation par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            notificationService.createNotification(
                    currentUser,
                    "Suppression d'une formation avec succès",
                    "La demande de suppression d'une formation a bien été effectuée avec succès"
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Formation supprimée avec succès", null));
        } catch (Exception e) {
            return errorResponse("Erreur lors de la suppression de la formation: " + e.getMessage());
        }
    }

    // ==== MÉTHODES PRIVÉES ====

    private ResponseEntity<ApiResponse<?>> errorResponse(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, message, null));
    }

    private ResponseEntity<ApiResponse<?>> notFoundResponse(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, message, null));
    }

    private ResponseEntity<ApiResponse<?>> validationErrorResponse(List<ErrorResponse> errors) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Erreurs de validation", errors));
    }

    private void logAndNotify(User user, String logAction, String notificationMessage) {
        auditService.log(
                logAction,
                "formations",
                user.getId(),
                logAction + " par " + user.getFullName(),
                user,
                requestHelper.getClientIp(),
                requestHelper.getUserAgent()
        );

        notificationService.createNotification(user, logAction, notificationMessage);
    }

    private List<ErrorResponse> checkEmployesContracts(FormationDTO dto) {
        List<ErrorResponse> errors = new ArrayList<>();

        if (dto.getCompanyId() == null) {
            errors.add(new ErrorResponse("company", "L'entreprise est obligatoire pour vérifier les contrats"));
            return errors;
        }

        if (dto.getEmployeIds() != null) {
            for (Long employeId : dto.getEmployeIds()) {
                if (!employeRepository.existsById(employeId)) {
                    errors.add(new ErrorResponse("employe_inexistant", "Un employé ID " + employeId + " n'existe pas"));
                } else if (!contratEmployeRepository.existsByEmployeIdAndCompanyId(employeId, dto.getCompanyId())) {
                    errors.add(new ErrorResponse("contrat_inexistant", "L'employé ID " + employeId + " n'a pas de contrat avec l'entreprise"));
                }
            }
        }

        return errors;
    }
}