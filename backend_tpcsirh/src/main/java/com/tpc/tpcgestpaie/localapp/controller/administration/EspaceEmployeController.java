package com.tpc.tpcgestpaie.localapp.controller.administration;

import com.tpc.tpcgestpaie.localapp.dto.EmployeFamilleDTO;
import com.tpc.tpcgestpaie.localapp.dto.EmployeUserInfoDTO;
import com.tpc.tpcgestpaie.localapp.dto.administration.AccidentTravailDTO;
import com.tpc.tpcgestpaie.localapp.dto.administration.AugmentationSalarialeDTO;
import com.tpc.tpcgestpaie.localapp.dto.administration.FormationDTO;
import com.tpc.tpcgestpaie.localapp.dto.administration.SanctionDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.AcompteDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.AvanceDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.MensualiteDTO;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.ContratEmployeService;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.administration.AccidentTravailService;
import com.tpc.tpcgestpaie.localapp.service.administration.AugmentationSalarialeService;
import com.tpc.tpcgestpaie.localapp.service.administration.FormationService;
import com.tpc.tpcgestpaie.localapp.service.administration.SanctionService;
import com.tpc.tpcgestpaie.localapp.service.employe.EmployeFamilleService;
import com.tpc.tpcgestpaie.localapp.service.paie.AcompteService;
import com.tpc.tpcgestpaie.localapp.service.paie.AvanceService;
import com.tpc.tpcgestpaie.localapp.service.paie.MensualiteService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@Tag(
        name = "Gestion administrative/Espace Employé",
        description = "API dédiée à l'espace personnel des employés : consultation des informations personnelles, accidents, formations, paie, contrats, augmentations, sanctions et données familiales"
)
@RestController
@RequestMapping("/api/espace-employe")
public class EspaceEmployeController {

    private final EmployeService employeService;
    private final EmployeRepository employeRepository;
    private final AccidentTravailService accidentService;
    private final FormationService formationService;
    private final AcompteService acompteService;
    private final MensualiteService mensualiteService;
    private final AvanceService avanceService;
    private final UserService userService;
    private final ContratEmployeService contratEmployeService;
    private final ContratEmployeRepository contratEmployeRepository;
    private final AugmentationSalarialeService augmentationService;
    private final EmployeFamilleService employeFamilleService;
    private final SanctionService sanctionService;

    public EspaceEmployeController(
            EmployeService employeService,
            EmployeRepository employeRepository,
            AccidentTravailService accidentService,
            FormationService formationService,
            AcompteService acompteService,
            MensualiteService mensualiteService,
            AvanceService avanceService,
            UserService userService,
            ContratEmployeService contratEmployeService,
            ContratEmployeRepository contratEmployeRepository,
            AugmentationSalarialeService augmentationService,
            EmployeFamilleService employeFamilleService,
            SanctionService sanctionService) {
        this.employeService = employeService;
        this.employeRepository = employeRepository;
        this.accidentService = accidentService;
        this.formationService = formationService;
        this.acompteService = acompteService;
        this.mensualiteService = mensualiteService;
        this.avanceService = avanceService;
        this.userService = userService;
        this.contratEmployeService = contratEmployeService;
        this.contratEmployeRepository = contratEmployeRepository;
        this.augmentationService = augmentationService;
        this.employeFamilleService = employeFamilleService;
        this.sanctionService = sanctionService;
    }

    @Operation(
            summary = "Lister les sanctions d'un employé avec pagination",
            description = "Récupère l'historique des sanctions disciplinaires d'un employé spécifique avec pagination et tri"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée des sanctions récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Liste paginée des sanctions de l'employé récupérée avec succès",
                      "data": {
                        "content": [
                          {
                            "id": 1,
                            "employeId": 123,
                            "dateSanction": "2024-01-15",
                            "typeSanction": "Avertissement écrit",
                            "motif": "Retard répété",
                            "statut": "ACTIF",
                            "createdAt": "2024-01-15T14:30:00"
                          }
                        ],
                        "currentPage": 0,
                        "totalItems": 5,
                        "totalPages": 1,
                        "size": 10
                      }
                    }
                    """
                            )
                    )
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
    @GetMapping("/sanctions/par-employe/{employeId}")
    public ResponseEntity<ApiResponse<?>> getByEmploye(
            @Parameter(description = "Identifiant de l'employé", required = true, example = "123")
            @PathVariable Long employeId,
            @Parameter(description = "Numéro de page (commence à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri", example = "desc", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            // Vérifie l'existence de l'employé
            if (!employeRepository.existsById(employeId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Employé introuvable", null));
            }

            // Créer le Pageable
            Pageable pageable = PageRequest.of(page, size);

            // Appeler la méthode avec les deux arguments
            Page<SanctionDTO> sanctionsPage = sanctionService.findByEmployeId(employeId, pageable);

            // Structure de réponse paginée
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
                            "Erreur lors de la récupération des sanctions: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les informations d'un employé par son identifiant utilisateur",
            description = "Retourne les informations personnelles et professionnelles d'un employé en fonction de son identifiant utilisateur (userId). Utile pour l'espace personnel connecté."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Informations de l'employé récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Informations de l'employé récupérées avec succès",
                      "data": {
                        "employeId": 123,
                        "userId": 456,
                        "matricule": "EMP001",
                        "nom": "Dupont",
                        "prenom": "Jean",
                        "email": "jean.dupont@entreprise.com",
                        "telephone": "+33 1 23 45 67 89",
                        "poste": "Développeur Senior",
                        "dateEmbauche": "2020-01-15",
                        "statut": "ACTIF",
                        "companyId": 5,
                        "companyNom": "Tech Solutions SARL"
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Utilisateur ou employé non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Utilisateur ou employé non trouvé pour l'ID: 999",
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
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ApiResponse<?>> getEmployeByUserId(
            @Parameter(description = "Identifiant de l'utilisateur", required = true, example = "456")
            @PathVariable Long userId) {
        try {
            EmployeUserInfoDTO dto = userService.getEmployeInfoByUserId(userId);

            if (dto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Utilisateur ou employé non trouvé pour l'ID: " + userId, null));
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Informations de l'employé récupérées avec succès", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération de l'employé: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les accidents de travail d'un employé",
            description = "Retourne l'historique complet des accidents de travail déclarés pour un employé spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Accidents de l'employé récupérés avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
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
    @GetMapping("/accidents/par-employe/{employeId}")
    public ResponseEntity<ApiResponse<?>> getAccidentsByEmploye(
            @Parameter(description = "Identifiant de l'employé", required = true, example = "123")
            @PathVariable Long employeId) {
        try {
            if (!employeService.existsById(employeId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Employé introuvable", null));
            }
            List<AccidentTravailDTO> list = accidentService.findByEmployeId(employeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Accidents de l'employé récupérés avec succès", list));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des accidents: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les formations d'un employé avec pagination",
            description = "Retourne la liste des formations suivies par un employé avec pagination et tri. Inclut les formations en cours, terminées et planifiées."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste paginée des formations récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Liste paginée des formations pour l'employé récupérée avec succès",
                      "data": {
                        "content": [
                          {
                            "id": 1,
                            "nomFormation": "Gestion de projet Agile",
                            "typeFormation": "INTERNE",
                            "dateDebut": "2024-02-01",
                            "dateFin": "2024-02-03",
                            "dureeHeures": 24,
                            "organisme": "Service Formation Interne",
                            "statut": "TERMINEE",
                            "certificat": "/uploads/certificats/cert_123.pdf"
                          }
                        ],
                        "currentPage": 0,
                        "totalItems": 8,
                        "totalPages": 1,
                        "size": 10
                      }
                    }
                    """
                            )
                    )
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
    @GetMapping("/formations/par-employe/{employeId}")
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
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Employé non trouvé", null));
            }

            // Créer le Pageable avec les paramètres de pagination
            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            // Appeler la méthode paginée
            Page<FormationDTO> formationsPage = formationService.getAllByEmploye(employeId, pageable);

            // Structure de réponse paginée
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", formationsPage.getContent());
            responseData.put("currentPage", formationsPage.getNumber());
            responseData.put("totalItems", formationsPage.getTotalElements());
            responseData.put("totalPages", formationsPage.getTotalPages());
            responseData.put("size", formationsPage.getSize());

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Liste paginée des formations pour l'employé récupérée avec succès",
                            responseData)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des formations: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les acomptes d'un employé",
            description = "Retourne la liste des acomptes perçus par un employé (avances sur salaire, prélèvements exceptionnels)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des acomptes de l'employé récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des acomptes de l'employé récupérée avec succès",
                      "data": [
                        {
                          "id": 1,
                          "montant": 50000,
                          "dateAcompte": "2024-01-15",
                          "motif": "Avance sur salaire",
                          "statut": "VALIDE",
                          "modePaiement": "VIREMENT"
                        }
                      ]
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employé introuvable",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/acomptes/par-employe/{employeId}")
    public ResponseEntity<ApiResponse<?>> getAcomptesByEmploye(
            @Parameter(description = "Identifiant de l'employé", required = true, example = "123")
            @PathVariable Long employeId) {
        try {
            if (!employeService.existsById(employeId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Employé introuvable", null));
            }

            List<AcompteDTO> list = acompteService.findByEmployeId(employeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des acomptes de l'employé récupérée avec succès", list));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des acomptes: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les mensualités d'un employé",
            description = "Retourne la liste des mensualités (paiements réguliers, prêts, échéances) liées à un employé"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des mensualités de l'employé récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des mensualités de l'employé récupérée avec succès",
                      "data": [
                        {
                          "id": 1,
                          "montant": 75000,
                          "dateEcheance": "2024-01-31",
                          "typeMensualite": "PRET_PERSONNEL",
                          "statut": "PAYE",
                          "reference": "PRET-2024-001"
                        }
                      ]
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employé introuvable",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/mensualites/par-employe/{employeId}")
    public ResponseEntity<ApiResponse<?>> getMensualitesByEmploye(
            @Parameter(description = "Identifiant de l'employé", required = true, example = "123")
            @PathVariable Long employeId) {
        try {
            if (!employeService.existsById(employeId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Employé introuvable", null));
            }

            List<MensualiteDTO> mensualites = mensualiteService.getMensualitesParEmploye(employeId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des mensualités de l'employé récupérée avec succès", mensualites)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des mensualités: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les avances d'un employé",
            description = "Retourne la liste des avances financières accordées à un employé (avances sur salaire, prêts de trésorerie)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des avances de l'employé récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des avances de l'employé récupérée avec succès",
                      "data": [
                        {
                          "id": 1,
                          "montant": 100000,
                          "dateAvance": "2024-01-10",
                          "motif": "Urgence familiale",
                          "statut": "REMBOURSE",
                          "modeRemboursement": "PRELEVEMENT_SALAIRE"
                        }
                      ]
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employé introuvable",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/avances/par-employe/{employeId}")
    public ResponseEntity<ApiResponse<?>> getAvancesByEmploye(
            @Parameter(description = "Identifiant de l'employé", required = true, example = "123")
            @PathVariable Long employeId) {
        try {
            if (!employeService.existsById(employeId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Employé introuvable", null));
            }

            List<AvanceDTO> list = avanceService.findByEmployeId(employeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des avances de l'employé récupérée avec succès", list));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des avances: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les contrats d'un employé",
            description = "Retourne l'historique des contrats de travail d'un employé (CDD, CDI, contrats actuels et passés)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des contrats récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des contrats récupérée avec succès",
                      "data": [
                        {
                          "id": 45,
                          "typeContrat": "CDI",
                          "dateDebut": "2020-01-15",
                          "dateFin": null,
                          "salaireBase": 500000,
                          "poste": "Développeur Senior",
                          "statut": "ACTIF",
                          "companyId": 5
                        }
                      ]
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "Aucun contrat trouvé pour cet employé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Aucun contrat trouvé pour cet employé",
                      "data": []
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
    @GetMapping("/contrats/by-employe/{employeId}")
    public ResponseEntity<ApiResponse<?>> getContratsByEmploye(
            @Parameter(description = "Identifiant de l'employé", required = true, example = "123")
            @PathVariable Long employeId) {
        try {
            List<ContractEmployeDTO> contrats = contratEmployeService.getContratsByEmployeId(employeId);
            if (contrats.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Aucun contrat trouvé pour cet employé", contrats));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des contrats récupérée avec succès", contrats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des contrats: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les détails complets d'un employé",
            description = "Retourne toutes les informations détaillées d'un employé (coordonnées, poste, statut, etc.)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Employé trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
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
                      "message": "Employé non trouvé avec l'ID: 999",
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
    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponse<?>> getById(
            @Parameter(description = "Identifiant de l'employé", required = true, example = "123")
            @PathVariable Long id) {
        try {
            Optional<Employe> employe = employeService.findById(id);
            if (employe.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Employé trouvé", employe.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Employé non trouvé avec l'ID: " + id, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération de l'employé: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les augmentations salariales d'un employé",
            description = "Retourne l'historique des augmentations salariales d'un employé spécifique (promotions, revalorisations, ajustements)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des augmentations récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
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
    @GetMapping("/augmentations/par-employe/{id}")
    public ResponseEntity<ApiResponse<?>> getAugmentationsByContrat(
            @Parameter(description = "Identifiant du contrat employé", required = true, example = "45")
            @PathVariable Long id) {
        try {
            List<ErrorResponse> errors = new ArrayList<>();
            Optional<ContratEmploye> contratOpt = contratEmployeRepository.findById(id);
            if (contratOpt.isEmpty()) {
                errors.add(new ErrorResponse("contratEmployeId", "ContratEmploye non trouvé"));
            }
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de récupération des entités", errors));
            }
            List<AugmentationSalarialeDTO> list = augmentationService.getByContratEmployeId(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des augmentations récupérée avec succès", list));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des augmentations: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les informations de la famille d'un employé",
            description = "Retourne les informations sur les membres de la famille déclarés par l'employé (conjoint, enfants, personnes à charge)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Informations de la famille récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Informations de la famille récupérées avec succès",
                      "data": {
                        "employeId": 123,
                        "conjoint": {
                          "nom": "Dupont",
                          "prenom": "Marie",
                          "dateNaissance": "1985-05-15",
                          "profession": "Infirmière"
                        },
                        "enfants": [
                          {
                            "nom": "Dupont",
                            "prenom": "Lucas",
                            "dateNaissance": "2015-08-20",
                            "scolarise": true
                          }
                        ]
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
    @GetMapping("/{employeId}/famille")
    public ResponseEntity<ApiResponse<?>> getEmployeFamille(
            @Parameter(description = "Identifiant de l'employé", required = true, example = "123")
            @PathVariable Long employeId) {
        try {
            EmployeFamilleDTO dto = employeFamilleService.getById(employeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Informations de la famille récupérées avec succès", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération de la famille: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Vérifier l'existence d'un employé",
            description = "Vérifie si un employé existe dans le système par son identifiant. Retourne un booléen."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Vérification effectuée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Vérification de l'existence de l'employé",
                      "data": true
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
    @GetMapping("/employe/exists/{employeId}")
    public ResponseEntity<ApiResponse<?>> checkEmployeExists(
            @Parameter(description = "Identifiant de l'employé à vérifier", required = true, example = "123")
            @PathVariable Long employeId) {
        try {
            boolean exists = employeService.existsById(employeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Vérification de l'existence de l'employé", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la vérification: " + e.getMessage(), null));
        }
    }
}