package com.tpc.tpcgestpaie.localapp.controller.administration;

import com.tpc.tpcgestpaie.localapp.dto.administration.CreditCongeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.helper.CreditCongeHelper;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.CreditCongeRepository;
import com.tpc.tpcgestpaie.localapp.service.administration.CreditCongeService;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.ContratEmployeService;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(
        name = "Gestion administrative/Crédits de Congé",
        description = "API de gestion des crédits de congé : attribution, suivi, consultation par employé/entreprise/période et gestion des soldes de congés"
)
@RestController
@RequestMapping("/api/administration/credit-conges")
public class CreditCongeController {

    private final CreditCongeService creditCongeService;
    private final CreditCongeHelper creditCongeHelper;
    private final UserService userService;
    private final CompanyService companyService;
    private final EmployeService employeService;
    private final ContratEmployeService contratEmployeService;
    private final CreditCongeRepository creditCongeRepository;
    private final ContratEmployeRepository contratEmployeRepository;

    public CreditCongeController(CreditCongeService creditCongeService, CreditCongeHelper creditCongeHelper, UserService userService, CompanyService companyService, EmployeService employeService, ContratEmployeService contratEmployeService, CreditCongeRepository creditCongeRepository, ContratEmployeRepository contratEmployeRepository) {
        this.creditCongeService = creditCongeService;
        this.creditCongeHelper = creditCongeHelper;
        this.userService = userService;
        this.companyService = companyService;
        this.employeService = employeService;
        this.contratEmployeService = contratEmployeService;
        this.creditCongeRepository = creditCongeRepository;
        this.contratEmployeRepository = contratEmployeRepository;
    }

    @Operation(
            summary = "Lister tous les crédits de congé",
            description = "Récupère la liste complète de tous les crédits de congé attribués dans le système"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des crédits congés récupérée",
                      "data": [
                        {
                          "id": 1,
                          "contratEmployeId": 123,
                          "employeId": 456,
                          "companyId": 5,
                          "dateReference": "2024-01-01",
                          "joursAcquis": 30,
                          "joursPris": 5,
                          "joursRestants": 25,
                          "statut": "ACTIF",
                          "createdAt": "2024-01-01T10:30:00",
                          "addedById": 1
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
    @GetMapping
    public ResponseEntity<ApiResponse<List<CreditCongeDTO>>> getAll() {
        List<CreditCongeDTO> list = creditCongeService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des crédits congés récupérée", list));
    }

    @Operation(
            summary = "Récupérer un crédit de congé par son identifiant",
            description = "Retourne les détails complets d'un crédit de congé spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Crédit congé trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Crédit congé non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Crédit congé non trouvé",
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
    public ResponseEntity<ApiResponse<CreditCongeDTO>> getById(
            @Parameter(description = "Identifiant du crédit de congé", required = true, example = "1")
            @PathVariable Long id) {
        Optional<CreditCongeDTO> opt = creditCongeService.getById(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Crédit congé trouvé", opt.get()));
        }
        return new ResponseEntity<>(new ApiResponse<>(false, "Crédit congé non trouvé", null), HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "Créer un nouveau crédit de congé",
            description = """
                Attribue un nouveau crédit de congé à un employé.
                
                Validations effectuées :
                - Champs obligatoires : contratEmployeId, dateReference
                - Vérification des doublons (un seul crédit ACTIF par contrat et date de référence)
                - Existence du contrat employé, employé et entreprise
                - Validation métier via helper
                - Clôture automatique des crédits précédents ACTIF pour le même employé
                
                Comportement spécifique :
                - Le statut par défaut est "ACTIF"
                - Les crédits précédents ACTIF pour le même employé sont automatiquement clôturés
                - La date de référence détermine la période de validité
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Crédit congé créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Crédit congé créé avec succès",
                      "data": {
                        "id": 1,
                        "contratEmployeId": 123,
                        "employeId": 456,
                        "companyId": 5,
                        "dateReference": "2024-01-01",
                        "joursAcquis": 30,
                        "joursPris": 0,
                        "joursRestants": 30,
                        "statut": "ACTIF",
                        "createdAt": "2024-01-01T10:30:00",
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
                                            name = "Champs manquants",
                                            value = """
                        {
                          "success": false,
                          "message": "Champs requis manquants",
                          "data": [
                            {
                              "field": "contratEmployeId",
                              "message": "Le champ 'contratEmployeId' est requis."
                            },
                            {
                              "field": "dateReference",
                              "message": "Le champ 'dateReference' est requis."
                            }
                          ]
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "Doublon",
                                            value = """
                        {
                          "success": false,
                          "message": "Doublon détecté",
                          "data": [
                            {
                              "field": "dateReference",
                              "message": "Un crédit congé actif existe déjà pour ce contrat et cette date."
                            }
                          ]
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "Validation métier",
                                            value = """
                        {
                          "success": false,
                          "message": "Erreurs de validation métier",
                          "data": [
                            {
                              "field": "joursAcquis",
                              "message": "Le nombre de jours acquis doit être positif"
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
    public ResponseEntity<?> create(
            @Parameter(
                    description = "Données du crédit de congé à créer",
                    required = true,
                    schema = @Schema(implementation = CreditCongeDTO.class)
            )
            @RequestBody CreditCongeDTO dto) {
        try {
            User currentUser = userService.getCurrentUser();
            List<ErrorResponse> errors = new ArrayList<>();

            // 1. Vérification manuelle des champs requis
            if (dto.getContratEmployeId() == null) {
                errors.add(new ErrorResponse("contratEmployeId", "Le champ 'contratEmployeId' est requis."));
            }

            if (dto.getDateReference() == null) {
                errors.add(new ErrorResponse("dateReference", "Le champ 'dateReference' est requis."));
            }

            // Si des erreurs de champs requis sont présentes, on retourne immédiatement
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Champs requis manquants", errors));
            }

            // Vérification doublon
            Optional<CreditConge> existing = creditCongeRepository.findByContratEmployeIdAndDateReferenceAndStatut(
                    dto.getContratEmployeId(),
                    dto.getDateReference(),
                    "ACTIF"
            );
            if (existing.isPresent()) {
                errors.add(new ErrorResponse("dateReference", "Un crédit congé actif existe déjà pour ce contrat et cette date."));
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Doublon détecté", errors));
            }

            // 2. Récupération des entités
            ContratEmploye contratEmploye = contratEmployeRepository.findById(dto.getContratEmployeId())
                    .orElseThrow(() -> new RuntimeException("ContratEmploye non trouvé"));

            Employe employe = contratEmploye.getEmploye();
            Company company = contratEmploye.getCompany();

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de récupération des entités", errors));
            }

            // 3. Validation métier via helper
            CreditConge entity = dto.toEntity(contratEmploye, employe, company, currentUser);
            List<ErrorResponse> validationErrors = creditCongeHelper.getInvalidFieldMessages(entity);

            if (!validationErrors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation métier", validationErrors));
            }

            // 4. Ajout des métadonnées
            dto.setStatut("ACTIF");
            dto.setCreatedAt(LocalDateTime.now());
            dto.setAddedById(currentUser.getId());

            // 5. Clôture des crédits précédents ACTIF pour le même employé
            CreditConge creditConge1 = creditCongeService.getCreditCongeActifByIdEmploye(employe.getId());

            if (creditConge1 != null) {
                creditConge1.setStatut("CLOTURE");
                creditConge1.setUpdated_at(LocalDateTime.now());
                creditCongeRepository.save(creditConge1);
            }

            // 6. Sauvegarde
            CreditCongeDTO saved = creditCongeService.save(dto, contratEmploye, employe, company, currentUser);
            return new ResponseEntity<>(new ApiResponse<>(true, "Crédit congé créé avec succès", saved), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création du crédit congé", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Mettre à jour un crédit de congé",
            description = """
                Modifie les informations d'un crédit de congé existant.
                
                Validations effectuées :
                - Existence du crédit à modifier
                - Existence du contrat employé, employé et entreprise
                - Validation métier via helper
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Crédit congé mis à jour avec succès",
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
                          "field": "joursAcquis",
                          "message": "Le nombre de jours acquis ne peut pas être négatif"
                        }
                      ]
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Crédit congé non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Crédit congé non trouvé",
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
    public ResponseEntity<?> update(
            @Parameter(description = "Identifiant du crédit de congé à modifier", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données du crédit de congé", required = true)
            @RequestBody CreditCongeDTO dto) {
        try {
            Optional<CreditCongeDTO> existingOpt = creditCongeService.getById(id);
            if (existingOpt.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Crédit congé non trouvé", null), HttpStatus.NOT_FOUND);
            }

            User currentUser = userService.getCurrentUser();

            Optional<ContractEmployeDTO> contratOpt = contratEmployeService.getById(dto.getContratEmployeId());
            Optional<Employe> employeOpt = employeService.findById(dto.getEmployeId());
            Optional<Company> companyOpt = Optional.ofNullable(companyService.getById(dto.getCompanyId()));

            if (contratOpt.isEmpty())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Contrat employé introuvable", null));
            if (employeOpt.isEmpty())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Employé introuvable", null));
            if (companyOpt.isEmpty())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));

            List<ErrorResponse> errors = creditCongeHelper.getInvalidFieldMessages(dto.toEntity(contratOpt.get().toEntity(), employeOpt.get(), companyOpt.get(), currentUser));
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            CreditCongeDTO toUpdate = existingOpt.get();

            // Mise à jour des champs
            toUpdate.setContratEmployeId(dto.getContratEmployeId());
            toUpdate.setEmployeId(dto.getEmployeId());
            toUpdate.setCompanyId(dto.getCompanyId());
            toUpdate.setDateReference(dto.getDateReference());
            toUpdate.setUpdatedAt(LocalDateTime.now());
            toUpdate.setAddedById(currentUser.getId());

            CreditCongeDTO saved = creditCongeService.save(toUpdate, contratOpt.get().toEntity(), employeOpt.get(), companyOpt.get(), currentUser);

            return ResponseEntity.ok(new ApiResponse<>(true, "Crédit congé mis à jour avec succès", saved));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour du crédit congé", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Supprimer un crédit de congé",
            description = "Supprime définitivement un crédit de congé du système"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Crédit congé supprimé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Crédit congé supprimé avec succès",
                      "data": null
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Crédit congé non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Crédit congé non trouvé",
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
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Identifiant du crédit de congé à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        if (!creditCongeService.exists(id)) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Crédit congé non trouvé", null), HttpStatus.NOT_FOUND);
        }
        creditCongeService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Crédit congé supprimé avec succès", null));
    }

    @Operation(
            summary = "Lister les crédits de congé par entreprise",
            description = "Récupère tous les crédits de congé attribués pour une entreprise spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des crédits congés de l'entreprise récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
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
    public ResponseEntity<ApiResponse<List<CreditCongeDTO>>> getByCompany(
            @Parameter(description = "Identifiant de l'entreprise", required = true, example = "5")
            @PathVariable Long companyId) {
        if (!companyService.existsById(companyId)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));
        }
        List<CreditCongeDTO> list = creditCongeService.findByCompanyId(companyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des crédits congés de l'entreprise", list));
    }

    @Operation(
            summary = "Lister les crédits de congé d'un employé dans une entreprise spécifique",
            description = "Récupère les crédits de congé attribués à un employé particulier au sein d'une entreprise donnée"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Crédits congés de l'employé dans l'entreprise récupérés avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Crédits congés de l'employé dans l'entreprise",
                      "data": [
                        {
                          "id": 1,
                          "contratEmployeId": 123,
                          "employeId": 456,
                          "companyId": 5,
                          "dateReference": "2024-01-01",
                          "joursAcquis": 30,
                          "joursPris": 5,
                          "joursRestants": 25,
                          "statut": "ACTIF"
                        }
                      ]
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Entreprise ou employé introuvable",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Entreprise introuvable",
                                            value = """
                        {
                          "success": false,
                          "message": "Entreprise introuvable",
                          "data": null
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "Employé introuvable",
                                            value = """
                        {
                          "success": false,
                          "message": "Employé introuvable",
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
    @GetMapping("/entreprise/{companyId}/employe/{employeeId}")
    public ResponseEntity<ApiResponse<List<CreditCongeDTO>>> getByCompanyAndEmployee(
            @Parameter(description = "Identifiant de l'entreprise", required = true, example = "5")
            @PathVariable Long companyId,
            @Parameter(description = "Identifiant de l'employé", required = true, example = "456")
            @PathVariable Long employeeId
    ) {
        // Vérification entreprise
        if (!companyService.existsById(companyId)) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Entreprise introuvable", null)
            );
        }

        // Vérification employé
        if (!employeService.existsById(employeeId)) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Employé introuvable", null)
            );
        }

        // Récupération des crédits congés de l'entreprise
        List<CreditCongeDTO> list = creditCongeService.findByCompanyId(companyId);

        // Filtrage pour ne garder que l'employé demandé
        List<CreditCongeDTO> filtered = list.stream()
                .filter(c -> c.getEmployeId() != null && c.getEmployeId().equals(employeeId))
                .toList();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Crédits congés de l'employé dans l'entreprise", filtered)
        );
    }

    @Operation(
            summary = "Lister les crédits de congé par employé",
            description = "Récupère l'historique complet des crédits de congé d'un employé spécifique (toutes entreprises confondues)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des crédits congés de l'employé récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
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
    public ResponseEntity<ApiResponse<List<CreditCongeDTO>>> getByEmploye(
            @Parameter(description = "Identifiant de l'employé", required = true, example = "456")
            @PathVariable Long employeId) {
        if (!employeService.existsById(employeId)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Employé introuvable", null));
        }
        List<CreditCongeDTO> list = creditCongeService.findByEmployeId(employeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des crédits congés de l'employé", list));
    }

    @Operation(
            summary = "Lister les crédits de congé par période",
            description = "Récupère tous les crédits de congé dont la date de référence se situe entre deux dates spécifiques"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des crédits congés pour la période récupérée avec succès",
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
                          "message": "Dates de début et fin obligatoires",
                          "data": null
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "Dates incohérentes",
                                            value = """
                        {
                          "success": false,
                          "message": "La date de début doit être antérieure à la date de fin",
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
    public ResponseEntity<?> getByDateReferenceBetween(
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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null || endDate == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Dates de début et fin obligatoires", null));
        }
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "La date de début doit être antérieure à la date de fin", null));
        }

        List<CreditCongeDTO> list = creditCongeService.findByDateReferenceBetween(startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des crédits congés pour la période", list));
    }
}