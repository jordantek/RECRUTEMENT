package com.tpc.tpcgestpaie.localapp.controller.administration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tpc.tpcgestpaie.localapp.dto.MontantParContratDTO;
import com.tpc.tpcgestpaie.localapp.dto.RubriqueMontantDTO;
import com.tpc.tpcgestpaie.localapp.dto.administration.AugmentationSalarialeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.helper.AugmentationSalarialeHelper;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.*;
import com.tpc.tpcgestpaie.localapp.service.administration.AugmentationSalarialeService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(
        name = "Gestion administrative/Augmentations Salariales",
        description = "API de gestion des augmentations salariales : déclaration, suivi, consultation par employé/entreprise et gestion des rubriques"
)
@RestController
@RequestMapping("/api/paie/augmentations-salariales")
public class AugmentationSalarialeController {

    private final AugmentationSalarialeService augmentationService;
    private final AugmentationSalarialeHelper augmentationHelper;
    private final UserService userService;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private final RequestHelper requestHelper;
    private final StockageService stockageService;
    private final ContratEmployeService contratEmployeService;
    private final CompanyService companyService;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRubriqueService contratEmployeRubriqueService;
    private final ContratEmployeRepository contratEmployeRepository;

    public AugmentationSalarialeController(
            AugmentationSalarialeService augmentationService,
            AugmentationSalarialeHelper augmentationHelper,
            UserService userService,
            AuditLogService auditService,
            NotificationService notificationService,
            RequestHelper requestHelper, StockageService stockageService, ContratEmployeService contratEmployeService, CompanyService companyService, EmployeRepository employeRepository,
            ContratEmployeRubriqueService contratEmployeRubriqueService, ContratEmployeRepository contratEmployeRepository) {
        this.augmentationService = augmentationService;
        this.augmentationHelper = augmentationHelper;
        this.userService = userService;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.requestHelper = requestHelper;
        this.stockageService = stockageService;
        this.contratEmployeService = contratEmployeService;
        this.companyService = companyService;
        this.employeRepository = employeRepository;
        this.contratEmployeRubriqueService = contratEmployeRubriqueService;
        this.contratEmployeRepository = contratEmployeRepository;
    }

    // === ENDPOINTS PAGINÉS ===

    @Operation(
            summary = "Lister toutes les augmentations salariales avec pagination",
            description = "Récupère la liste complète de toutes les augmentations salariales déclarées dans le système avec pagination et tri"
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
                      "message": "Liste paginée des augmentations récupérée avec succès",
                      "data": {
                        "content": [
                          {
                            "id": 1,
                            "employeId": 123,
                            "companyId": 5,
                            "contratEmployeId": 45,
                            "dateEffet": "2024-01-01",
                            "motifAugmentation": "Promotion",
                            "preuve": "/uploads/augmentations/preuve1.pdf",
                            "createdAt": "2024-01-01T10:30:00",
                            "added_by": {
                              "id": 1,
                              "username": "admin"
                            }
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

            Page<AugmentationSalarialeDTO> augmentationsPage = augmentationService.getAll(pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", augmentationsPage.getContent());
            responseData.put("currentPage", augmentationsPage.getNumber());
            responseData.put("totalItems", augmentationsPage.getTotalElements());
            responseData.put("totalPages", augmentationsPage.getTotalPages());
            responseData.put("size", augmentationsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des augmentations récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des augmentations: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister les augmentations salariales par entreprise",
            description = "Récupère toutes les augmentations salariales déclarées pour une entreprise spécifique avec pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des augmentations de l'entreprise récupérée avec succès",
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
    @Transactional
    @GetMapping("/par-entreprise/{entrepriseId}")
    public ResponseEntity<ApiResponse<?>> getByEntreprise(
            @Parameter(description = "Identifiant de l'entreprise", required = true, example = "5")
            @PathVariable Long entrepriseId,
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

            Page<AugmentationSalarialeDTO> augmentationsPage = augmentationService.getByEntrepriseId(entrepriseId, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", augmentationsPage.getContent());
            responseData.put("currentPage", augmentationsPage.getNumber());
            responseData.put("totalItems", augmentationsPage.getTotalElements());
            responseData.put("totalPages", augmentationsPage.getTotalPages());
            responseData.put("size", augmentationsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des augmentations par entreprise récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des augmentations: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister les augmentations salariales par contrat employé avec pagination",
            description = "Récupère l'historique complet des augmentations salariales d'un contrat employé spécifique avec pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des augmentations du contrat employé récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Contrat employé introuvable",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Erreurs de récupération des entités",
                      "data": [
                        {
                          "field": "contratEmployeId",
                          "message": "ContratEmploye non trouvé"
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
    @Transactional
    @GetMapping("/par-employee/{id}")
    public ResponseEntity<ApiResponse<?>> getByEmploye(
            @Parameter(description = "Identifiant du contrat employé", required = true, example = "45")
            @PathVariable Long id,
            @Parameter(description = "Numéro de page", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateEffet")
            @RequestParam(defaultValue = "dateEffet") String sortBy,
            @Parameter(description = "Direction du tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            List<ErrorResponse> errors = new ArrayList<>();
            Optional<ContratEmploye> contratOpt = contratEmployeRepository.findById(id);
            if (contratOpt.isEmpty()) {
                errors.add(new ErrorResponse("contratEmployeId", "ContratEmploye non trouvé"));
            }
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de récupération des entités", errors));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<AugmentationSalarialeDTO> augmentationsPage = augmentationService.getByContratEmployeId(id, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", augmentationsPage.getContent());
            responseData.put("currentPage", augmentationsPage.getNumber());
            responseData.put("totalItems", augmentationsPage.getTotalElements());
            responseData.put("totalPages", augmentationsPage.getTotalPages());
            responseData.put("size", augmentationsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des augmentations récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des augmentations: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister toutes les augmentations salariales par employé (non paginé)",
            description = "Récupère la liste complète des augmentations salariales d'un employé spécifique sans pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des augmentations de l'employé récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Contrat employé introuvable",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Erreurs de récupération des entités",
                      "data": [
                        {
                          "field": "contratEmployeId",
                          "message": "ContratEmploye non trouvé"
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
    @GetMapping("/par-employe/{id}")
    public ResponseEntity<ApiResponse<?>> getByEmployeId(
            @Parameter(description = "Identifiant du contrat employé", required = true, example = "45")
            @PathVariable Long id) {

        try {
            List<ErrorResponse> errors = new ArrayList<>();

            Optional<ContratEmploye> contratOpt = contratEmployeRepository.findById(id);
            if (contratOpt.isEmpty()) {
                errors.add(new ErrorResponse("contratEmployeId", "ContratEmploye non trouvé"));
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, "Erreurs de récupération des entités", errors)
                );
            }

            List<AugmentationSalarialeDTO> augmentations = augmentationService.getByContratEmployeId(id);

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Liste des augmentations récupérée avec succès",
                            augmentations
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des augmentations: " + e.getMessage(),
                            null)
                    );
        }
    }

    @Operation(
            summary = "Récupérer une augmentation salariale par son identifiant",
            description = "Retourne les détails complets d'une augmentation salariale spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Augmentation trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Augmentation non trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Augmentation non trouvée avec l'ID: 999",
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
            @Parameter(description = "Identifiant de l'augmentation", required = true, example = "1")
            @PathVariable Long id) {
        try {
            AugmentationSalarialeDTO dto = augmentationService.getById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Augmentation trouvée", dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Augmentation non trouvée avec l'ID: " + id, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération de l'augmentation: " + e.getMessage(), null));
        }
    }

    // === ENDPOINTS DE CRÉATION/MODIFICATION/SUPPRESSION ===

    @Operation(
            summary = "Créer une nouvelle augmentation salariale",
            description = """
                Enregistre un nouveau cas d'augmentation salariale avec fichier de preuve.
                
                Validations effectuées :
                - Champs obligatoires : dateEffet, motifAugmentation, employeId, companyId
                - Vérification des doublons (même employé, date et motif)
                - Existence de l'employé, entreprise et contrat actif
                - Fichier de preuve obligatoire (PDF, image, etc.)
                - Format JSON pour les données et multipart/form-data pour le fichier
                
                L'audit et les notifications sont automatiquement gérés.
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Augmentation créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Augmentation créée avec succès",
                      "data": {
                        "id": 1,
                        "employeId": 123,
                        "companyId": 5,
                        "contratEmployeId": 45,
                        "dateEffet": "2024-01-01",
                        "motifAugmentation": "Promotion exceptionnelle",
                        "preuve": "/uploads/augmentations/preuve_12345.pdf",
                        "createdAt": "2024-01-01T10:30:00",
                        "added_by": {
                          "id": 1,
                          "username": "admin"
                        }
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
                          "field": "dateEffet",
                          "message": "La date d'effet est obligatoire"
                        },
                        {
                          "field": "doublon_augmentation",
                          "message": "Une augmentation avec cette date et ce motif existe déjà pour cet employé."
                        },
                        {
                          "field": "preuve_inexistante",
                          "message": "La preuve n'existe pas"
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> create(
            @Parameter(
                    description = "Données de l'augmentation salariale au format JSON",
                    required = true,
                    schema = @Schema(implementation = AugmentationSalarialeDTO.class)
            )
            @RequestPart("data") String rawJson,
            @Parameter(
                    description = "Fichier de preuve (PDF, image, etc.)",
                    required = true
            )
            @RequestPart(value = "preuve", required = false) MultipartFile preuveFile
    ){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            AugmentationSalarialeDTO dto = objectMapper.readValue(rawJson, AugmentationSalarialeDTO.class);

            User currentUser = userService.getCurrentUser();
            if (augmentationHelper.isDuplicateAugmentation(dto.getEmployeId(), dto.getDateEffet(), dto.getMotifAugmentation())) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Une augmentation avec cette date et ce motif existe déjà pour cet employé.", null));
            }
            List<ErrorResponse> errors = augmentationHelper.getInvalidFieldMessages(AugmentationSalarialeDTO.toEntity(dto));

            //employé
            if (!employeRepository.existsById(dto.getEmployeId())) {
                errors.add(new ErrorResponse("employe_inexistant", "L'employé n'existe pas"));
            }

            // Gestion du fichier
            if (preuveFile != null && !preuveFile.isEmpty()) {
                String savedFilePath = stockageService.saveFile(preuveFile);
                dto.setPreuve(savedFilePath);
            } else {
                errors.add(new ErrorResponse("preuve_inexistante", "La preuve n'existe pas"));
            }

            Optional<ContractEmployeDTO> contratOpt = contratEmployeService.getActifByEmployeIdAndCompanyId(dto.getEmployeId(), dto.getCompanyId());
            if (contratOpt.isEmpty()) {
                errors.add(new ErrorResponse("contrat_inactif", "Pas de contrat actif pour cet employé ou entreprise différente"));
            } else {
                dto.setContratEmployeId(contratOpt.get().getId());
            }

            Optional<Company> companyOpt = companyService.findById(dto.getCompanyId());
            if (companyOpt.isEmpty()) {
                errors.add(new ErrorResponse("entreprise_inexistante", "Entreprise non trouvée"));
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            dto.setCreatedAt(LocalDateTime.now());
            dto.setAdded_by(currentUser);

            AugmentationSalarialeDTO saved = augmentationService.save(dto, currentUser);

            auditService.log(
                    "Création d'une augmentation salariale",
                    "augmentations_salariales",
                    currentUser.getId(),
                    "Création d'une augmentation salariale pour employé id : " + dto.getEmployeId(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            notificationService.createNotification(
                    currentUser,
                    "Nouvelle augmentation salariale",
                    "Une augmentation salariale a été ajoutée avec succès pour l'employé."
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Augmentation créée avec succès", saved));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la création de l'augmentation: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Mettre à jour une augmentation salariale",
            description = """
                Modifie les informations d'une augmentation salariale existante.
                
                Le fichier de preuve peut être mis à jour. Si aucun nouveau fichier n'est fourni,
                l'ancien fichier est conservé.
                
                L'audit et les notifications sont automatiquement gérés.
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Augmentation mise à jour avec succès",
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
                    description = "Augmentation non trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Augmentation non trouvée avec l'ID: 999",
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
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<?>> update(
            @Parameter(description = "Identifiant de l'augmentation à modifier", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de l'augmentation", required = true)
            @RequestPart("data") AugmentationSalarialeDTO updatedDto,
            @Parameter(description = "Nouveau fichier de preuve (optionnel)")
            @RequestPart(value = "preuve", required = false) MultipartFile preuveFile
    ) {
        try {
            AugmentationSalarialeDTO existing = augmentationService.getById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Augmentation non trouvée avec l'ID: " + id, null));
            }

            User currentUser = userService.getCurrentUser();

            if (preuveFile != null && !preuveFile.isEmpty()) {
                String savedFilePath = stockageService.saveFile(preuveFile);
                updatedDto.setPreuve(savedFilePath);
            }

            List<ErrorResponse> errors = augmentationHelper.getInvalidFieldMessages(AugmentationSalarialeDTO.toEntity(updatedDto));

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            // Mise à jour des champs
            existing.setDateEffet(updatedDto.getDateEffet());
            existing.setMotifAugmentation(updatedDto.getMotifAugmentation());
            existing.setPreuve(updatedDto.getPreuve());
            existing.setRubriques(updatedDto.getRubriques());
            existing.setUpdatedAt(LocalDateTime.now());
            existing.setAdded_by(currentUser);

            AugmentationSalarialeDTO saved = augmentationService.save(existing, currentUser);

            auditService.log(
                    "Modification d'une augmentation salariale",
                    "augmentations_salariales",
                    currentUser.getId(),
                    "Modification de l'augmentation salariale id : " + id,
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            notificationService.createNotification(
                    currentUser,
                    "Augmentation modifiée",
                    "L'augmentation salariale a été modifiée avec succès."
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Augmentation mise à jour avec succès", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la mise à jour de l'augmentation: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Supprimer une augmentation salariale",
            description = "Effectue une suppression d'une augmentation salariale. L'audit et les notifications sont automatiquement gérés."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Augmentation supprimée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Augmentation supprimée avec succès",
                      "data": null
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Augmentation non trouvée",
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
            @Parameter(description = "Identifiant de l'augmentation à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            AugmentationSalarialeDTO existing = augmentationService.getById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Augmentation non trouvée avec l'ID: " + id, null));
            }

            User currentUser = userService.getCurrentUser();

            augmentationService.delete(id);

            auditService.log(
                    "Suppression d'une augmentation salariale",
                    "augmentations_salariales",
                    currentUser.getId(),
                    "Suppression de l'augmentation salariale id : " + id,
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            notificationService.createNotification(
                    currentUser,
                    "Augmentation supprimée",
                    "L'augmentation salariale a été supprimée avec succès."
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Augmentation supprimée avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la suppression de l'augmentation: " + e.getMessage(), null));
        }
    }

    // === ENDPOINTS SPÉCIFIQUES (non paginés) ===

    @Operation(
            summary = "Récupérer les rubriques associées à un contrat",
            description = "Retourne la liste des rubriques et leurs montants pour un contrat employé spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des rubriques trouvées",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des rubriques trouvées",
                      "data": [
                        {
                          "rubriqueId": 1,
                          "rubriqueNom": "Salaire de base",
                          "montant": 500000,
                          "unite": "FCFA"
                        },
                        {
                          "rubriqueId": 2,
                          "rubriqueNom": "Prime de transport",
                          "montant": 50000,
                          "unite": "FCFA"
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
    @GetMapping("/rubriques/{contratId}")
    public ResponseEntity<ApiResponse<?>> getRubriquesByContratId(
            @Parameter(description = "Identifiant du contrat employé", required = true, example = "45")
            @PathVariable Long contratId) {
        try {
            List<RubriqueMontantDTO> rubriques = contratEmployeRubriqueService.getListRubriquesByContratId(contratId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des rubriques trouvées", rubriques));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la recherche des rubriques: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister les augmentations salariales par entreprise (non paginé)",
            description = "Retourne la liste des augmentations salariales avec les montants par contrat pour une entreprise spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des augmentations récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des augmentations récupérée avec succès",
                      "data": [
                        {
                          "contratId": 45,
                          "employeNom": "Dupont Jean",
                          "montantTotal": 750000,
                          "dateDerniereAugmentation": "2024-01-01",
                          "rubriques": [
                            {
                              "rubriqueNom": "Salaire de base",
                              "montant": 500000
                            },
                            {
                              "rubriqueNom": "Prime de transport",
                              "montant": 50000
                            }
                          ]
                        }
                      ]
                    }
                    """
                            )
                    )
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
                      "message": "Entreprise introuvable avec l'ID: 999",
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
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<?>> liste(
            @Parameter(description = "Identifiant de l'entreprise", required = true, example = "5")
            @PathVariable Long companyId) {
        try {
            Optional<Company> companyOpt = companyService.findById(companyId);
            if (companyOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise introuvable avec l'ID: " + companyId, null));
            }

            List<MontantParContratDTO> augmentations = contratEmployeRubriqueService.listeByCompanyId(companyId);

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Liste des augmentations récupérée avec succès",
                    augmentations
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Erreur lors de la récupération des augmentations: " + e.getMessage(),
                            null
                    ));
        }
    }
}