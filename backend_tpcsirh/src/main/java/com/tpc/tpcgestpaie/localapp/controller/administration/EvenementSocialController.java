package com.tpc.tpcgestpaie.localapp.controller.administration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tpc.tpcgestpaie.localapp.dto.administration.EvenementSocialDTO;
import com.tpc.tpcgestpaie.localapp.helper.EvenementSocialHelper;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.AuditLogService;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.administration.EvenementSocialService;
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
        name = "Gestion administrative/Événements Sociaux",
        description = "API de gestion des événements sociaux d'entreprise : organisation, suivi, consultation par entreprise/employé et gestion des activités sociales (séminaires, formations, célébrations)"
)
@RestController
@RequestMapping("/api/administration/evenements-socials")
public class EvenementSocialController {

    private final EvenementSocialService evenementSocialService;
    private final EvenementSocialHelper evenementSocialHelper;
    private final UserService userService;
    private final RequestHelper requestHelper;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private final CompanyRepository companyRepository;
    private final EmployeRepository employeRepository;

    public EvenementSocialController(
            EvenementSocialService evenementSocialService,
            EvenementSocialHelper evenementSocialHelper,
            UserService userService,
            RequestHelper requestHelper,
            AuditLogService auditService,
            NotificationService notificationService,
            CompanyRepository companyRepository,
            EmployeRepository employeRepository
    ) {
        this.evenementSocialService = evenementSocialService;
        this.evenementSocialHelper = evenementSocialHelper;
        this.userService = userService;
        this.requestHelper = requestHelper;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.companyRepository = companyRepository;
        this.employeRepository = employeRepository;
    }

    // === ENDPOINTS PAGINÉS ===

    @Operation(
            summary = "Lister tous les événements sociaux avec pagination",
            description = "Récupère la liste complète de tous les événements sociaux organisés dans le système avec pagination et tri. Inclut les événements passés, en cours et à venir."
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
                      "message": "Liste paginée des événements sociaux récupérée avec succès",
                      "data": {
                        "content": [
                          {
                            "id": 1,
                            "titre": "Séminaire annuel",
                            "typeEvenement": "FORMATION",
                            "description": "Séminaire de formation sur les nouvelles technologies",
                            "dateDebut": "2024-03-15T09:00:00",
                            "dateFin": "2024-03-16T17:00:00",
                            "lieu": "Centre de conférence Paris",
                            "companyId": 5,
                            "employeId": 123,
                            "budgetAlloue": 500000,
                            "statut": "PLANIFIE",
                            "createdAt": "2024-01-15T10:30:00",
                            "addedById": 1
                          }
                        ],
                        "currentPage": 0,
                        "totalItems": 15,
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

            Page<EvenementSocialDTO> evenementsPage = evenementSocialService.findAll(pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", evenementsPage.getContent());
            responseData.put("currentPage", evenementsPage.getNumber());
            responseData.put("totalItems", evenementsPage.getTotalElements());
            responseData.put("totalPages", evenementsPage.getTotalPages());
            responseData.put("size", evenementsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des événements sociaux récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des événements sociaux: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister les événements sociaux par entreprise",
            description = "Récupère tous les événements sociaux organisés pour une entreprise spécifique avec pagination. Permet de suivre les activités sociales internes."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée des événements sociaux de l'entreprise récupérée avec succès",
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
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise introuvable", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<EvenementSocialDTO> evenementsPage = evenementSocialService.findByCompanyId(companyId, pageable);

            // Nettoyage des champs null
            List<ObjectNode> cleanedContent = evenementsPage.getContent().stream()
                    .map(JsonCleaner::removeNullFields)
                    .toList();

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", cleanedContent);
            responseData.put("currentPage", evenementsPage.getNumber());
            responseData.put("totalItems", evenementsPage.getTotalElements());
            responseData.put("totalPages", evenementsPage.getTotalPages());
            responseData.put("size", evenementsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des événements sociaux pour l'entreprise récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des événements sociaux: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister les événements sociaux par employé",
            description = "Récupère tous les événements sociaux auxquels un employé participe ou est impliqué avec pagination. Permet de visualiser l'engagement social d'un employé."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée des événements sociaux de l'employé récupérée avec succès",
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
            if (!employeRepository.existsById(employeId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Employé introuvable", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<EvenementSocialDTO> evenementsPage = evenementSocialService.findByEmployeId(employeId, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", evenementsPage.getContent());
            responseData.put("currentPage", evenementsPage.getNumber());
            responseData.put("totalItems", evenementsPage.getTotalElements());
            responseData.put("totalPages", evenementsPage.getTotalPages());
            responseData.put("size", evenementsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des événements sociaux de l'employé récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des événements sociaux: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer un événement social par son identifiant",
            description = "Retourne les détails complets d'un événement social spécifique. Inclut toutes les informations sur l'organisation, les participants et le budget."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Événement social trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Événement social trouvé",
                      "data": {
                        "id": 1,
                        "titre": "Séminaire de fin d'année",
                        "typeEvenement": "CELEBRATION",
                        "description": "Célébration des réussites de l'année 2024",
                        "dateDebut": "2024-12-15T19:00:00",
                        "dateFin": "2024-12-15T23:00:00",
                        "lieu": "Hôtel Royal",
                        "budgetAlloue": 1000000,
                        "statut": "PLANIFIE",
                        "companyId": 5,
                        "employeId": 123,
                        "nombreParticipants": 50,
                        "createdAt": "2024-11-01T14:30:00",
                        "addedById": 1
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Événement social non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Événement social non trouvé avec l'ID: 999",
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
            @Parameter(description = "Identifiant de l'événement social", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Optional<EvenementSocialDTO> evenementOpt = evenementSocialService.findById(id);
            if (evenementOpt.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Événement social trouvé", evenementOpt.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Événement social non trouvé avec l'ID: " + id, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération de l'événement social: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Créer un nouvel événement social",
            description = """
                Planifie un nouvel événement social d'entreprise.
                
                Validations effectuées :
                - Champs obligatoires : titre, typeEvenement, dateDebut, companyId
                - Date de début doit être antérieure ou égale à la date de fin
                - Budget alloué doit être positif si renseigné
                - Vérification de l'existence de l'entreprise et de l'employé responsable
                
                Fonctionnalités incluses :
                - Audit automatique de la création
                - Notification aux utilisateurs concernés
                - Gestion des métadonnées (créateur, dates)
                
                Types d'événements supportés : FORMATION, CELEBRATION, SEMINAIRE, TEAM_BUILDING, AUTRE
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Événement social créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Événement social créé avec succès",
                      "data": {
                        "id": 1,
                        "titre": "Formation leadership",
                        "typeEvenement": "FORMATION",
                        "description": "Formation sur les compétences en leadership",
                        "dateDebut": "2024-04-10T09:00:00",
                        "dateFin": "2024-04-11T17:00:00",
                        "lieu": "Salle de conférence A",
                        "budgetAlloue": 250000,
                        "statut": "PLANIFIE",
                        "companyId": 5,
                        "employeId": 123,
                        "createdAt": "2024-03-20T10:30:00",
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
                          "field": "titre",
                          "message": "Le titre est obligatoire"
                        },
                        {
                          "field": "dateDebut",
                          "message": "La date de début est obligatoire"
                        },
                        {
                          "field": "budgetAlloue",
                          "message": "Le budget alloué doit être positif"
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
                    description = "Données du nouvel événement social",
                    required = true,
                    schema = @Schema(implementation = EvenementSocialDTO.class)
            )
            @RequestBody EvenementSocialDTO evenementSocialDTO) {
        try {
            User currentUser = userService.getCurrentUser();

            List<ErrorResponse> errors = evenementSocialHelper.getInvalidFieldMessages(evenementSocialDTO);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            evenementSocialDTO.setCreatedAt(LocalDateTime.now());
            evenementSocialDTO.setAddedById(currentUser.getId());

            EvenementSocialDTO saved = evenementSocialService.save(evenementSocialDTO);

            auditService.log(
                    "Création d'un événement social",
                    "evenements_socials",
                    currentUser.getId(),
                    "Ajout d'un événement social par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            notificationService.createNotification(
                    currentUser,
                    "Nouvel événement social",
                    "Un nouvel événement social a été enregistré 🥳"
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Événement social créé avec succès", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la création de l'événement social: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Mettre à jour un événement social existant",
            description = """
                Modifie les informations d'un événement social existant.
                
                Validations effectuées :
                - Vérification de l'existence de l'événement
                - Validation des champs mis à jour
                - Cohérence des dates
                
                Fonctionnalités incluses :
                - Audit automatique de la modification
                - Notification de mise à jour
                - Nettoyage des champs null dans la réponse
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Événement social mis à jour avec succès",
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
                    description = "Événement social non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Événement social non trouvé avec l'ID: 999",
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
            @Parameter(description = "Identifiant de l'événement social à modifier", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de l'événement social", required = true)
            @RequestBody EvenementSocialDTO updatedDto) {
        try {
            Optional<EvenementSocialDTO> existingOpt = evenementSocialService.findById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Événement social non trouvé avec l'ID: " + id, null));
            }

            User currentUser = userService.getCurrentUser();

            List<ErrorResponse> errors = evenementSocialHelper.getInvalidUpdateFieldMessages(updatedDto);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            updatedDto.setId(id);
            updatedDto.setUpdatedById(currentUser.getId());

            EvenementSocialDTO saved = evenementSocialService.update(updatedDto);

            // Journalisation
            auditService.log(
                    "Mise à jour d'un événement social",
                    "evenements_socials",
                    currentUser.getId(),
                    "Mise à jour d'un événement social par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            // Notification
            notificationService.createNotification(
                    currentUser,
                    "Événement social modifié",
                    "Un événement social a été modifié ✏️"
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Événement social mis à jour avec succès", JsonCleaner.removeNullFields(saved))
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la mise à jour de l'événement social: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Supprimer un événement social",
            description = "Effectue une suppression logique (soft delete) d'un événement social. Les données restent en base mais sont marquées comme supprimées."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Événement social supprimé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Événement social supprimé avec succès",
                      "data": null
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Événement social non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Événement social non trouvé avec l'ID: 999",
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
            @Parameter(description = "Identifiant de l'événement social à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Optional<EvenementSocialDTO> evenementOpt = evenementSocialService.findById(id);
            if (evenementOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Événement social non trouvé avec l'ID: " + id, null));
            }

            evenementSocialService.softDelete(id);

            User currentUser = userService.getCurrentUser();

            // Journalisation
            auditService.log(
                    "Suppression d'un événement social",
                    "evenements_socials",
                    currentUser.getId(),
                    "Suppression d'un événement social par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            // Notification
            notificationService.createNotification(
                    currentUser,
                    "Suppression d'un événement social",
                    "Un événement social a été supprimé 🗑️"
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Événement social supprimé avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la suppression de l'événement social: " + e.getMessage(), null));
        }
    }
}