package com.tpc.tpcgestpaie.localapp.controller.accessoire;

import com.tpc.tpcgestpaie.localapp.dto.accessoire.*;
import com.tpc.tpcgestpaie.localapp.model.Absence;
import com.tpc.tpcgestpaie.localapp.model.AllocationConge;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.IndemniteConge;
import com.tpc.tpcgestpaie.localapp.repository.AllocationCongeRepository;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.AllocationCongeService;
import com.tpc.tpcgestpaie.localapp.service.IndemniteCongeService;
import com.tpc.tpcgestpaie.localapp.service.accessoire.CongeService;
import com.tpc.tpcgestpaie.localapp.service.administration.AbsenceService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(
        name = "Gestion administrative/Indemnités de Congé",
        description = "API de gestion des indemnités et allocations de congé : calcul, enregistrement, consultation des allocations et indemnités de fin de contrat"
)
@RestController
@RequestMapping("/api/indemnite-conges")
public class CongeController {

    private final CongeService congeService;
    private final ContratEmployeRepository contratEmployeRepository;

    private final AbsenceService absenceService;
    private final AllocationCongeService allocationCongeService;
    private final IndemniteCongeService indemniteCongeService;
    private final AllocationCongeRepository allocationCongeRepository;
    private final EmployeRepository employeRepository;
    private final CompanyRepository companyRepository;

    public CongeController(CongeService congeService, ContratEmployeRepository contratEmployeRepository,
                           AbsenceService absenceService, AllocationCongeService allocationCongeService,
                           IndemniteCongeService indemniteCongeService, AllocationCongeRepository allocationCongeRepository,
                           EmployeRepository employeRepository, CompanyRepository companyRepository) {
        this.congeService = congeService;
        this.contratEmployeRepository = contratEmployeRepository;
        this.absenceService = absenceService;
        this.allocationCongeService = allocationCongeService;
        this.indemniteCongeService = indemniteCongeService;
        this.allocationCongeRepository = allocationCongeRepository;
        this.employeRepository = employeRepository;
        this.companyRepository = companyRepository;
    }

    @Operation(
            summary = "Calculer l'allocation ou l'indemnité de congé",
            description = "Effectue le calcul de l'allocation de congé (pour contrat en cours) ou de l'indemnité de congé (pour contrat terminé) d'un employé pour un mois donné. Vérifie les règles métier et les absences déductibles."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Calcul effectué avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès du calcul",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Indemnité calculée avec succès",
                                      "data": {
                                        "typeOperation": "ALLOCATION",
                                        "montantCalcule": 150000,
                                        "joursConge": 2.5,
                                        "tauxJournalier": 60000,
                                        "details": {...}
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Erreur de validation ou règle métier non respectée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Contrat en cours",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Règle métier non respectée",
                                              "data": [{
                                                "field": "typeOperation",
                                                "message": "Le contrat de cet employé est en cours, vous ne pouvez pas calculer son indemnité de congé."
                                              }]
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Aucun congé enregistré",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Règle métier non respectée",
                                              "data": [{
                                                "field": "absences",
                                                "message": "Aucun congé enregistré pour cet employé pour le mois de 2024-03"
                                              }]
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Format de mois invalide",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Erreurs de validation",
                                              "data": [{
                                                "field": "mois",
                                                "message": "Format du mois invalide. Format attendu : yyyy-MM"
                                              }]
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content
            )
    })
    @PostMapping("/calcul")
    public ResponseEntity<?> calculerConge(
            @Parameter(
                    description = "Données pour le calcul de congé",
                    required = true,
                    schema = @Schema(implementation = IndemniteCongeDTO.class)
            )
            @RequestBody IndemniteCongeDTO request) {
        try {
            List<ErrorResponse> errors = new ArrayList<>();

            // Vérification de l'identifiant du contrat
            if (request.getIdContratEmploye() == null || !contratEmployeRepository.existsById(request.getIdContratEmploye())) {
                errors.add(new ErrorResponse("idContratEmploye", "Contrat employé introuvable"));
            }

            // Vérification du type d'opération
            if (request.getTypeOperation() == null || request.getTypeOperation().isBlank()) {
                errors.add(new ErrorResponse("typeOperation", "Type d'opération obligatoire"));
            }

            // Vérification du mois
            YearMonth moisTraitement = null;
            if (request.getMois() == null || request.getMois().isBlank()) {
                errors.add(new ErrorResponse("mois", "Le mois de traitement est obligatoire (format attendu : yyyy-MM)"));
            } else {
                try {
                    moisTraitement = YearMonth.parse(request.getMois()); // Format attendu : "yyyy-MM"
                } catch (DateTimeParseException e) {
                    errors.add(new ErrorResponse("mois", "Format du mois invalide. Format attendu : yyyy-MM"));
                }
            }

            // Si erreurs déjà détectées, on les retourne
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            // Récupération du contrat pour validation métier
            Optional<ContratEmploye> contratOpt = contratEmployeRepository.findById(request.getIdContratEmploye());
            if (contratOpt.isEmpty()) {
                errors.add(new ErrorResponse("idContratEmploye", "Contrat employé introuvable")); // double sécurité
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            ContratEmploye contrat = contratOpt.get();

            // Vérification du statut du contrat si opération = INDEMNITE_CONGE
            if ("INDEMNITE_CONGE".equalsIgnoreCase(request.getTypeOperation()) && "CONTRAT EN COURS".equalsIgnoreCase(contrat.getStatus_contrat())) {
                errors.add(new ErrorResponse("typeOperation", "Le contrat de cet employé est en cours, vous ne pouvez pas calculer son indemnité de congé."));
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Règle métier non respectée", errors));
            }

            // Vérification s'il existe au moins une absence à déduire pour ALLOCATION
            if ("ALLOCATION".equalsIgnoreCase(request.getTypeOperation())) {
                LocalDate debutMois = moisTraitement.atDay(1);
                LocalDate finMois = moisTraitement.atEndOfMonth();

                List<Absence> absences = absenceService.getAbsencesDeductibles(contrat.getEmploye().getId(), debutMois, finMois);
                boolean hasCongeDeductible = absences.stream()
                        .anyMatch(abs -> GlobalEnums.ConditionAcceptationConge.A_DEDUIRE_DES_CONGES.name().equals(
                                Optional.ofNullable(abs.getConditionAcceptation()).orElse("")
                        ));
                if (!hasCongeDeductible) {
                    errors.add(new ErrorResponse("absences", "Aucun congé enregistré pour cet employé pour le mois de " + moisTraitement));
                    return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Règle métier non respectée", errors));
                }
            }


            // Appel du service avec le mois traité
            ResultatCongeDTO resultat = congeService.calculerAllocationConge(
                    request.getIdContratEmploye(),
                    request.getTypeOperation(),
                    request.getMois()
            );
            return new ResponseEntity<>(new ApiResponse<>(true, "Indemnité calculée avec succès", resultat), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur interne du serveur", null));
        }
    }

    @Operation(
            summary = "Enregistrer une allocation de congé",
            description = "Enregistre une allocation de congé calculée pour un employé dont le contrat est en cours. Vérifie les doublons avant l'enregistrement."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Allocation enregistrée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Allocation enregistrée avec succès",
                                      "data": 123
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Conflit - Allocation déjà existante",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Doublon",
                                    value = """
                                    {
                                      "success": false,
                                      "message": "Une allocation existe déjà pour cet employé et ce mois",
                                      "data": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur lors de l'enregistrement",
                    content = @Content
            )
    })
    @PostMapping("/allocation/save")
    public ResponseEntity<?> enregistrerAllocation(
            @Parameter(
                    description = "Données d'allocation à enregistrer",
                    required = true,
                    schema = @Schema(implementation = AllocationCongeSaveDTO.class)
            )
            @RequestBody AllocationCongeSaveDTO saveDto) {
        try {
            AllocationConge allocation = allocationCongeService.enregistrerAllocationConge(
                    saveDto.getResultatCongeDTO(),
                    saveDto.getIdContratEmploye(),
                    saveDto.getCompanyId(),
                    saveDto.getMoisCalculSalaire()
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "Allocation enregistrée avec succès", allocation.getId()));
        } catch (IllegalStateException e) {
            // Cas doublon
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de l'enregistrement de l'allocation", null));
        }
    }

    @Operation(
            summary = "Lister les allocations de congé",
            description = "Récupère la liste détaillée des allocations de congé pour une entreprise donnée, avec possibilité de filtrer par contrat employé spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Le contrat ne correspond pas à l'entreprise",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": false,
                                      "message": "Le contrat ne correspond pas à l'entreprise fournie",
                                      "data": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Entreprise ou contrat introuvable",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/allocation/list")
    public ResponseEntity<?> listerAllocations(
            @Parameter(
                    description = "Identifiant de l'entreprise",
                    required = true,
                    example = "1"
            )
            @RequestParam Long companyId,
            @Parameter(
                    description = "Identifiant du contrat employé (optionnel pour filtrage)",
                    example = "5"
            )
            @RequestParam(required = false) Long contratEmployeId
    ) {
        try {
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise introuvable", null));
            }

            if (contratEmployeId != null) {
                Optional<ContratEmploye> contratOpt = contratEmployeRepository.findById(contratEmployeId);
                if (contratOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Contrat employé introuvable", null));
                }
                ContratEmploye contrat = contratOpt.get();
                if (!contrat.getCompany().getId().equals(companyId)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse<>(false, "Le contrat ne correspond pas à l'entreprise fournie", null));
                }
            }

            List<AllocationCongeDetailDTO> list = allocationCongeService.getAllocationsDetailsByCompanyAndContrat(companyId, contratEmployeId);

            return ResponseEntity.ok(new ApiResponse<>(true, "Liste détaillée des allocations", list));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des allocations", null));
        }
    }

    @Operation(
            summary = "Récupérer le détail d'une allocation",
            description = "Retourne les informations détaillées d'une allocation de congé spécifique par son identifiant"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Détail récupéré avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Détail de l'allocation",
                                      "data": {
                                        "id": 123,
                                        "employeNom": "Doe",
                                        "employePrenom": "John",
                                        "montant": 150000,
                                        "mois": "2024-03",
                                        "joursConge": 2.5,
                                        "details": {...}
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Allocation non trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": false,
                                      "message": "Allocation non trouvée",
                                      "data": null
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/allocation/detail/{id}")
    public ResponseEntity<?> getAllocationDetail(
            @Parameter(
                    description = "Identifiant de l'allocation",
                    required = true,
                    example = "123"
            )
            @PathVariable Long id) {
        try {
            AllocationCongeDetailDTO detail = allocationCongeService.getAllocationDetailById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Détail de l'allocation", detail));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Allocation non trouvée", null));
        }
    }

    @Operation(
            summary = "Enregistrer une indemnité de congé",
            description = "Enregistre une indemnité de congé pour un employé dont le contrat est terminé (indemnité de fin de contrat). Vérifie les doublons avant l'enregistrement."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Indemnité enregistrée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Indemnité enregistrée avec succès",
                                      "data": 456
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Conflit - Indemnité déjà existante",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Doublon",
                                    value = """
                                    {
                                      "success": false,
                                      "message": "Une indemnité existe déjà pour cet employé",
                                      "data": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur lors de l'enregistrement",
                    content = @Content
            )
    })
    @PostMapping("/indemnite/save")
    public ResponseEntity<?> enregistrerIndemnite(
            @Parameter(
                    description = "Données d'indemnité à enregistrer",
                    required = true,
                    schema = @Schema(implementation = AllocationCongeSaveDTO.class)
            )
            @RequestBody AllocationCongeSaveDTO saveDto) {
        try {
            IndemniteConge indemniteConge = indemniteCongeService.enregistrerIndemniteConge(
                    saveDto.getResultatCongeDTO(),
                    saveDto.getIdContratEmploye(),
                    saveDto.getCompanyId(),
                    saveDto.getMoisCalculSalaire()
            );
            return ResponseEntity.ok(new ApiResponse<>(true, "Indemnité enregistrée avec succès", indemniteConge.getId()));
        } catch (IllegalStateException e) {
            // Cas doublon
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de l'enregistrement de l'indemnité", null));
        }
    }

    @Operation(
            summary = "Lister les indemnités de congé par entreprise",
            description = "Récupère la liste détaillée de toutes les indemnités de congé (fin de contrat) pour une entreprise donnée"
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
                                      "message": "Liste des indemnités récupérée",
                                      "data": [
                                        {
                                          "id": 456,
                                          "employeNom": "Smith",
                                          "employePrenom": "Jane",
                                          "montantTotal": 500000,
                                          "dateFinContrat": "2024-02-28",
                                          "details": {...}
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
    @GetMapping("/indemnite/list")
    public ResponseEntity<?> listerIndemnitesParCompany(
            @Parameter(
                    description = "Identifiant de l'entreprise",
                    required = true,
                    example = "1"
            )
            @RequestParam Long companyId
    ) {
        try {
            if (!companyRepository.existsById(companyId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise introuvable", null));
            }

            List<AllocationCongeDetailDTO> list = indemniteCongeService.getIndemniteDetailsByCompany(companyId);

            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des indemnités récupérée", list));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des indemnités", null));
        }
    }

}