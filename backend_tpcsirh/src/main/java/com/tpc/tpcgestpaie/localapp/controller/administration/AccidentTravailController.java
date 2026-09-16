package com.tpc.tpcgestpaie.localapp.controller.administration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tpc.tpcgestpaie.localapp.dto.administration.AccidentTravailDTO;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.administration.AccidentTravailRepository;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.ContratEmployeService;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.administration.AccidentTravailService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(
        name = "Gestion administrative/Accidents de Travail",
        description = "API de gestion des accidents de travail : déclaration, suivi, consultation par employé/entreprise/période et statistiques"
)
@RestController
@RequestMapping("/api/administration/accidents-travail")
public class AccidentTravailController {

    private final AccidentTravailService accidentService;
    private final EmployeService employeService;
    private final CompanyService companyService;
    private final ContratEmployeService contratEmployeService;
    private final UserService userService;
    private final AccidentTravailRepository accidentTravailRepository;

    public AccidentTravailController(
            AccidentTravailService accidentService,
            EmployeService employeService,
            CompanyService companyService,
            ContratEmployeService contratEmployeService,
            UserService userService,
            AccidentTravailRepository accidentTravailRepository) {
        this.accidentService = accidentService;
        this.employeService = employeService;
        this.companyService = companyService;
        this.contratEmployeService = contratEmployeService;
        this.userService = userService;
        this.accidentTravailRepository = accidentTravailRepository;
    }

    // === ENDPOINTS PAGINÉS ===

    @Operation(
            summary = "Lister tous les accidents de travail avec pagination",
            description = "Récupère la liste complète de tous les accidents de travail déclarés dans le système avec pagination et tri"
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
                                      "message": "Liste paginée des accidents de travail récupérée avec succès",
                                      "data": {
                                        "content": [
                                          {
                                            "id": 1,
                                            "employeId": 123,
                                            "companyId": 5,
                                            "contratEmployeId": 45,
                                            "dateAccident": "2024-03-15",
                                            "dateDeclaration": "2024-03-16",
                                            "lieu": "Atelier de production",
                                            "circonstances": "Chute d'un échafaudage",
                                            "naturelesion": "Fracture du bras",
                                            "depense": 150000,
                                            "observations": "Prise en charge immédiate"
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

            Page<AccidentTravailDTO> accidentsPage = accidentService.getAll(pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", accidentsPage.getContent());
            responseData.put("currentPage", accidentsPage.getNumber());
            responseData.put("totalItems", accidentsPage.getTotalElements());
            responseData.put("totalPages", accidentsPage.getTotalPages());
            responseData.put("size", accidentsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des accidents de travail récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des accidents: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister les accidents de travail par entreprise",
            description = "Récupère tous les accidents de travail déclarés pour une entreprise spécifique avec pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des accidents de l'entreprise récupérée avec succès",
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
            if (!companyService.existsById(companyId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise introuvable", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<AccidentTravailDTO> accidentsPage = accidentService.findByCompanyId(companyId, pageable);

            // Nettoyage des nulls
            List<ObjectNode> cleanedContent = accidentsPage.getContent().stream()
                    .map(JsonCleaner::removeNullFields)
                    .toList();

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", cleanedContent);
            responseData.put("currentPage", accidentsPage.getNumber());
            responseData.put("totalItems", accidentsPage.getTotalElements());
            responseData.put("totalPages", accidentsPage.getTotalPages());
            responseData.put("size", accidentsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des accidents de l'entreprise récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des accidents: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister les accidents de travail par employé",
            description = "Récupère l'historique complet des accidents de travail d'un employé spécifique avec pagination"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des accidents de l'employé récupérée avec succès",
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
            if (!employeService.existsById(employeId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Employé introuvable", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<AccidentTravailDTO> accidentsPage = accidentService.findByEmployeId(employeId, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", accidentsPage.getContent());
            responseData.put("currentPage", accidentsPage.getNumber());
            responseData.put("totalItems", accidentsPage.getTotalElements());
            responseData.put("totalPages", accidentsPage.getTotalPages());
            responseData.put("size", accidentsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des accidents de l'employé récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des accidents: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Lister les accidents de travail par période",
            description = "Récupère tous les accidents survenus entre deux dates avec pagination (utile pour les rapports et statistiques)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des accidents pour la période récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dates invalides ou manquantes",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Dates manquantes",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Les dates de début et de fin sont obligatoires",
                                              "data": null
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Dates incohérentes",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "La date de début ne peut pas être après la date de fin",
                                              "data": null
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
    @GetMapping("/par-periode")
    public ResponseEntity<ApiResponse<?>> getByDateBetween(
            @Parameter(
                    description = "Date de début de la période (format ISO: YYYY-MM-DD)",
                    required = true,
                    example = "2024-01-01"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(
                    description = "Date de fin de la période (format ISO: YYYY-MM-DD)",
                    required = true,
                    example = "2024-12-31"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Numéro de page", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "dateAccident")
            @RequestParam(defaultValue = "dateAccident") String sortBy,
            @Parameter(description = "Direction du tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            if (startDate == null || endDate == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Les dates de début et de fin sont obligatoires", null));
            }
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "La date de début ne peut pas être après la date de fin", null));
            }

            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<AccidentTravailDTO> accidentsPage = accidentService.findByDateAccidentBetween(startDate, endDate, pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", accidentsPage.getContent());
            responseData.put("currentPage", accidentsPage.getNumber());
            responseData.put("totalItems", accidentsPage.getTotalElements());
            responseData.put("totalPages", accidentsPage.getTotalPages());
            responseData.put("size", accidentsPage.getSize());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste paginée des accidents pour la période récupérée avec succès", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des accidents: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer un accident de travail par son identifiant",
            description = "Retourne les détails complets d'un accident de travail spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Accident trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Accident non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": false,
                                      "message": "Accident non trouvé avec l'ID: 999",
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
    public ResponseEntity<ApiResponse<AccidentTravailDTO>> getById(
            @Parameter(description = "Identifiant de l'accident", required = true, example = "1")
            @PathVariable Long id) {
        try {
            return accidentService.getById(id)
                    .map(dto -> ResponseEntity.ok(new ApiResponse<>(true, "Accident trouvé", dto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Accident non trouvé avec l'ID: " + id, null)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération de l'accident: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Déclarer un nouvel accident de travail",
            description = """
                    Enregistre un nouveau cas d'accident de travail.
                    
                    Validations effectuées :
                    - Champs obligatoires : dateAccident, dateDeclaration, depense, employeId, companyId, contratEmployeId
                    - Date d'accident ne peut pas être dans le futur
                    - Date de déclaration ne peut pas être antérieure à la date d'accident
                    - Vérification des doublons (même date accident + déclaration)
                    - Existence de l'employé, entreprise et contrat
                    
                    Champs optionnels : lieu, circonstances, naturelesion, observations
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Accident déclaré avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Accident de travail enregistré avec succès",
                                      "data": {
                                        "id": 1,
                                        "employeId": 123,
                                        "companyId": 5,
                                        "contratEmployeId": 45,
                                        "dateAccident": "2024-03-15",
                                        "dateDeclaration": "2024-03-16",
                                        "lieu": "Atelier",
                                        "circonstances": "Chute",
                                        "naturelesion": "Fracture",
                                        "depense": 150000,
                                        "createdAt": "2024-03-16T10:30:00"
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
                                      "message": "Champs invalides",
                                      "data": [
                                        {
                                          "field": "dateAccident",
                                          "message": "La date de l'accident ne peut pas être dans le futur"
                                        },
                                        {
                                          "field": "dateDeclaration",
                                          "message": "La date de déclaration ne peut pas être antérieure à la date de l'accident"
                                        },
                                        {
                                          "field": "doublon_date",
                                          "message": "Un accident avec la même date d'accident et de déclaration existe déjà."
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
                    description = "Données de l'accident de travail à déclarer",
                    required = true,
                    schema = @Schema(implementation = AccidentTravailDTO.class)
            )
            @RequestBody AccidentTravailDTO dto) {
        try {
            List<ErrorResponse> errors = validate(dto);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Champs invalides", errors));
            }

            User currentUser = userService.getCurrentUser();
            dto.setCreatedAt(LocalDateTime.now());
            dto.setAddedById(currentUser.getId());

            AccidentTravailDTO saved = accidentService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Accident de travail enregistré avec succès", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la création de l'accident: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Mettre à jour un accident de travail",
            description = "Modifie les informations d'un accident de travail existant. Tous les champs obligatoires doivent être fournis."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Accident mis à jour avec succès",
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
                    description = "Accident non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": false,
                                      "message": "Accident introuvable avec l'ID: 999",
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
            @Parameter(description = "Identifiant de l'accident à modifier", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de l'accident", required = true)
            @RequestBody AccidentTravailDTO dto) {
        try {
            if (!accidentService.exists(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Accident introuvable avec l'ID: " + id, null));
            }

            List<ErrorResponse> errors = updateValidate(dto);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Champs invalides", errors));
            }

            dto.setUpdatedAt(LocalDateTime.now());
            dto.setId(id);

            AccidentTravailDTO saved = accidentService.update(dto);

            return ResponseEntity.ok(new ApiResponse<>(true, "Accident mis à jour avec succès", saved));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la mise à jour de l'accident: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Supprimer un accident de travail",
            description = "Effectue une suppression logique (soft delete) d'un accident de travail. Les données restent en base mais sont marquées comme supprimées."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Accident supprimé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Accident supprimé avec succès",
                                      "data": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Accident non trouvé",
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
            @Parameter(description = "Identifiant de l'accident à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            if (!accidentService.exists(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Accident introuvable avec l'ID: " + id, null));
            }

            accidentService.softDelete(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Accident supprimé avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la suppression de l'accident: " + e.getMessage(), null));
        }
    }

    // === MÉTHODES DE VALIDATION ===

    /**
     * Valide les champs pour la création d'un accident de travail
     */
    private List<ErrorResponse> validate(AccidentTravailDTO dto) {
        List<ErrorResponse> errors = new ArrayList<>();

        if (dto.getDateAccident() == null) {
            errors.add(new ErrorResponse("dateAccident", "Le champ dateAccident est requis."));
        }
        if (dto.getDateDeclaration() == null) {
            errors.add(new ErrorResponse("dateDeclaration", "Le champ dateDeclaration est requis."));
        }
        if (dto.getDepense() == null) {
            errors.add(new ErrorResponse("depense", "Le champ depense est requis."));
        }
        if (dto.getEmployeId() == null || !employeService.existsById(dto.getEmployeId())) {
            errors.add(new ErrorResponse("employeId", "Employé introuvable ou non spécifié."));
        }
        if (dto.getCompanyId() == null || !companyService.existsById(dto.getCompanyId())) {
            errors.add(new ErrorResponse("companyId", "Entreprise introuvable ou non spécifiée."));
        }
        if (dto.getContratEmployeId() == null || !contratEmployeService.existsById(dto.getContratEmployeId())) {
            errors.add(new ErrorResponse("contratEmployeId", "Contrat employé introuvable ou non spécifié."));
        }

        // Validations de cohérence des dates
        if (dto.getDateAccident() != null) {
            LocalDate aujourdhui = LocalDate.now();

            // dateAccident doit être antérieure ou égale à aujourd'hui
            if (dto.getDateAccident().isAfter(aujourdhui)) {
                errors.add(new ErrorResponse("dateAccident", "La date de l'accident ne peut pas être dans le futur"));
            }
        }

        if (dto.getDateAccident() != null && dto.getDateDeclaration() != null) {
            // dateDeclaration ne doit pas être antérieure à dateAccident
            if (dto.getDateDeclaration().isBefore(dto.getDateAccident())) {
                errors.add(new ErrorResponse("dateDeclaration", "La date de déclaration ne peut pas être antérieure à la date de l'accident"));
            }

            // Vérification du doublon (gardée de votre code original)
            boolean existe = accidentTravailRepository
                    .existsByDateAccidentAndDateDeclaration(dto.getDateAccident(), dto.getDateDeclaration());

            if (existe) {
                errors.add(new ErrorResponse(
                        "doublon_date",
                        "Un accident avec la même date d'accident et de déclaration existe déjà."
                ));
            }
        }

        return errors;
    }

    /**
     * Valide les champs pour la mise à jour d'un accident de travail
     */
    private List<ErrorResponse> updateValidate(AccidentTravailDTO dto) {
        List<ErrorResponse> errors = new ArrayList<>();

        if (dto.getDateAccident() == null) {
            errors.add(new ErrorResponse("dateAccident", "Le champ dateAccident est requis."));
        }
        if (dto.getDateDeclaration() == null) {
            errors.add(new ErrorResponse("dateDeclaration", "Le champ dateDeclaration est requis."));
        }
        if (dto.getDepense() == null) {
            errors.add(new ErrorResponse("depense", "Le champ depense est requis."));
        }
        if (dto.getEmployeId() == null || !employeService.existsById(dto.getEmployeId())) {
            errors.add(new ErrorResponse("employeId", "Employé introuvable ou non spécifié."));
        }
        if (dto.getCompanyId() == null || !companyService.existsById(dto.getCompanyId())) {
            errors.add(new ErrorResponse("companyId", "Entreprise introuvable ou non spécifiée."));
        }
        if (dto.getContratEmployeId() == null || !contratEmployeService.existsById(dto.getContratEmployeId())) {
            errors.add(new ErrorResponse("contratEmployeId", "Contrat employé introuvable ou non spécifié."));
        }

        return errors;
    }


}