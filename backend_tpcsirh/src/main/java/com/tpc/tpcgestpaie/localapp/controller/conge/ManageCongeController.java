package com.tpc.tpcgestpaie.localapp.controller.conge;

import com.tpc.tpcgestpaie.localapp.dto.conge.annulation.AnnulationRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.annulation.ResultatAnnulationDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.consommation.ResultatConsommationDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.historique.HistoriqueDemandeDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.historique.HistoriqueMoisProvisionDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.solde.HistoriqueEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.solde.RapportPeriodiqueDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.solde.SoldeCongeDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.solde.SoldesEntrepriseDTO;
import com.tpc.tpcgestpaie.localapp.exception.conge.SoldeInsuffisantException;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.conge.*;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gestion/conges")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Gestion des Congés", description = "API complète de gestion des provisions, consommations et soldes de congés avec validation et traçabilité")
public class ManageCongeController {

    private final ConsommationCongeService consommationService;
    private final SoldeCongeService soldeService;
    private final HistoriqueCongeService historiqueService;
    private final UserService userService;
    private final SoldesEntrepriseService soldesEntrepriseService;

    // ============================================
    // 1. CONSOMMATION (Confirmation de départ)
    // ============================================

    @Operation(
            summary = "Confirmer le départ et consommer les provisions",
            description = "Consomme les provisions de congés selon la méthode FIFO lors de la confirmation de départ. " +
                    "Peut répartir la consommation sur plusieurs mois si nécessaire."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Consommation réussie",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"success\": true, \"message\": \"Départ confirmé, consommation enregistrée\", " +
                                            "\"data\": {\"montantTotal\": 4.0, \"nombreMoisTouches\": 2}}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Paramètres invalides",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Non autorisé",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Demande ou employé non trouvé",
                    content = @Content
            )
    })
    @PostMapping("/demande/{demandeId}/confirmer")
    public ResponseEntity<ApiResponse<?>> confirmerDepart(
            @Parameter(description = "ID de la demande de congé", required = true, example = "1001")
            @PathVariable Long demandeId,

            @Parameter(description = "Nombre de jours à déduire", required = true, example = "4")
            @RequestParam Integer joursADeduire) {

        log.info("POST /demande/{}/confirmer - jours={}", demandeId, joursADeduire);

        try {
            // Récupération de l'utilisateur connecté
            var currentUser = userService.getCurrentUser();
            if (currentUser.getEmploye() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Aucun employé associé à l'utilisateur", null));
            }

            Long employeId = currentUser.getEmploye().getId();

            ResultatConsommationDTO resultat = consommationService.consommerProvisions(
                    employeId,
                    new BigDecimal(joursADeduire),
                    demandeId
            );

            Map<String, Object> data = new HashMap<>();
            data.put("montantTotal", resultat.getMontantTotal());
            data.put("nombreMoisTouches", resultat.getNombreMoisTouches());
            data.put("detailsParMois", resultat.getDetailsParMois());

            String message = String.format("Départ confirmé. Consommation de %s jours répartie sur %s mois.",
                    resultat.getMontantTotal(), resultat.getNombreMoisTouches());

            log.info("Consommation réussie: {} jours sur {} mois",
                    resultat.getMontantTotal(), resultat.getNombreMoisTouches());

            return ResponseEntity.ok(new ApiResponse<>(true, message, data));

        } catch (SoldeInsuffisantException e) {
            log.warn("Solde insuffisant pour demande {}: {}", demandeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (RuntimeException e) {
            log.error("Erreur consommation demande {}: {}", demandeId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ============================================
    // 2. ANNULATION
    // ============================================

    @Operation(
            summary = "Annuler une consommation de provisions",
            description = "Restaure les provisions consommées pour une demande annulée. " +
                    "Opération inverse de la consommation avec traçabilité complète."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Annulation réussie",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Requête invalide",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Non autorisé",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Demande non trouvée",
                    content = @Content
            )
    })
    @PostMapping("/demande/{demandeId}/annuler-provisions")
    public ResponseEntity<ApiResponse<?>> annulerConsommation(
            @Parameter(description = "ID de la demande", required = true, example = "1001")
            @PathVariable Long demandeId,

            @Valid @RequestBody AnnulationRequestDTO request) {

        log.info("POST /demande/{}/annuler-provisions", demandeId);

        try {
            // Récupération de l'utilisateur connecté
            var currentUser = userService.getCurrentUser();
            if (currentUser.getId() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Utilisateur non identifié", null));
            }

            ResultatAnnulationDTO resultat = consommationService.annulerConsommation(
                    demandeId,
                    request.getMotif(),
                    currentUser.getId()
            );

            Map<String, Object> data = new HashMap<>();
            data.put("joursTotalRestitues", resultat.getJoursTotalRestaures());
            data.put("nombreMoisTouches", resultat.getNombreMoisTouches());
            data.put("detailsParMois", resultat.getDetailsParMois());

            String message = String.format("Annulation réussie. %s jours restitués sur %s mois.",
                    resultat.getJoursTotalRestaures(), resultat.getNombreMoisTouches());

            log.info("Annulation réussie: {} jours restaurés", resultat.getJoursTotalRestaures());

            return ResponseEntity.ok(new ApiResponse<>(true, message, data));

        } catch (RuntimeException e) {
            log.error("Erreur annulation demande {}: {}", demandeId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ============================================
    // 3. SOLDES
    // ============================================

    @Operation(
            summary = "Obtenir le solde actuel",
            description = "Retourne le solde de congés à la date du jour avec détail par mois de provision."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Solde retourné avec succès",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Non autorisé",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Employé non trouvé",
                    content = @Content
            )
    })
    @GetMapping("/mon-solde")
    public ResponseEntity<ApiResponse<?>> getMonSoldeActuel() {
        try {
            var currentUser = userService.getCurrentUser();
            if (currentUser.getEmploye() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Aucun employé associé", null));
            }

            Long employeId = currentUser.getEmploye().getId();
            log.info("GET /mon-solde - employe={}", employeId);

            SoldeCongeDTO solde = soldeService.getSoldeActuel(employeId);

            return ResponseEntity.ok(new ApiResponse<>(true, "Solde récupéré avec succès", solde));

        } catch (Exception e) {
            log.error("Erreur récupération solde: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur technique: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Obtenir le solde d'un employé (pour RH)",
            description = "Permet aux RH de consulter le solde d'un employé spécifique."
    )
    @GetMapping("/employe/{employeId}/solde")
    public ResponseEntity<ApiResponse<?>> getSoldeEmploye(
            @Parameter(description = "ID de l'employé", required = true, example = "42")
            @PathVariable Long employeId) {

        try {
            log.info("GET /employe/{}/solde", employeId);
            SoldeCongeDTO solde = soldeService.getSoldeActuel(employeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Solde récupéré avec succès", solde));

        } catch (Exception e) {
            log.error("Erreur récupération solde employé {}: {}", employeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Employé non trouvé: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Obtenir le solde à une date donnée",
            description = "Permet de consulter rétrospectivement le solde disponible à une date spécifique."
    )
    @GetMapping("/employe/{employeId}/solde-a-date")
    public ResponseEntity<ApiResponse<?>> getSoldeADate(
            @Parameter(description = "ID de l'employé", required = true, example = "42")
            @PathVariable Long employeId,

            @Parameter(description = "Date de calcul (ISO-8601)", required = true, example = "2025-02-28")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            log.info("GET /employe/{}/solde-a-date - date={}", employeId, date);

            if (date == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "La date est requise", null));
            }

            SoldeCongeDTO solde = soldeService.getSoldeAdate(employeId, date);
            return ResponseEntity.ok(new ApiResponse<>(true, "Solde à date récupéré", solde));

        } catch (Exception e) {
            log.error("Erreur récupération solde à date: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // ============================================
    // 4. HISTORIQUE
    // ============================================

    @Operation(
            summary = "Historique complet d'une demande",
            description = "Retourne le détail d'une demande avec la répartition des consommations sur les mois de provision."
    )
    @GetMapping("/demande/{demandeId}/historique")
    public ResponseEntity<ApiResponse<?>> getHistoriqueDemande(
            @Parameter(description = "ID de la demande", required = true, example = "1001")
            @PathVariable Long demandeId) {

        try {

            HistoriqueDemandeDTO historique = historiqueService.getHistoriqueDemande(demandeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Historique récupéré", historique));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Demande non trouvée: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Historique d'un mois de provision (audit)",
            description = "Vue d'audit d'un mois de provision spécifique."
    )
    @GetMapping("/provision/{provisionId}/historique")
    public ResponseEntity<ApiResponse<?>> getHistoriqueMois(
            @Parameter(description = "ID de la provision (mois)", required = true, example = "101")
            @PathVariable Long provisionId) {

        try {
            log.info("GET /provision/{}/historique", provisionId);
            HistoriqueMoisProvisionDTO historique = historiqueService.getHistoriqueMoisProvision(provisionId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Historique du mois récupéré", historique));

        } catch (Exception e) {
            log.error("Erreur récupération historique provision {}: {}", provisionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Provision non trouvée: " + e.getMessage(), null));
        }
    }

    // ============================================
    // 5. HISTORIQUE EMPLOYÉ
    // ============================================

    @Operation(
            summary = "Historique complet d'un employé",
            description = "Retourne l'évolution mensuelle des congés, les statistiques globales et les dernières demandes."
    )
    @GetMapping("/employe/{employeId}/historique-complet")
    public ResponseEntity<ApiResponse<?>> getHistoriqueComplet(
            @Parameter(description = "ID de l'employé", required = true, example = "42")
            @PathVariable Long employeId) {

        try {
            log.info("GET /employe/{}/historique-complet", employeId);
            HistoriqueEmployeDTO historique = soldeService.getHistoriqueComplet(employeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Historique complet récupéré", historique));

        } catch (Exception e) {
            log.error("Erreur récupération historique employé {}: {}", employeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Employé non trouvé: " + e.getMessage(), null));
        }
    }

    @GetMapping("/mon-historique")
    public ResponseEntity<ApiResponse<?>> getMonHistoriqueComplet() {
        try {
            var currentUser = userService.getCurrentUser();
            if (currentUser.getEmploye() == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Aucun employé associé", null));
            }

            Long employeId = currentUser.getEmploye().getId();
            log.info("GET /mon-historique - employe={}", employeId);

            HistoriqueEmployeDTO historique = soldeService.getHistoriqueComplet(employeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Historique récupéré", historique));

        } catch (Exception e) {
            log.error("Erreur récupération historique: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur technique", null));
        }
    }

    // ============================================
    // 6. RAPPORTS
    // ============================================

    @Operation(
            summary = "Rapport périodique pour RH",
            description = "Génère un rapport des congés pris sur une période donnée avec coûts associés."
    )
    @GetMapping("/employe/{employeId}/rapport-periode")
    public ResponseEntity<ApiResponse<?>> getRapportPeriode(
            @Parameter(description = "ID de l'employé", required = true, example = "42")
            @PathVariable Long employeId,

            @Parameter(description = "Date début (ISO-8601)", required = true, example = "2025-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,

            @Parameter(description = "Date fin (ISO-8601)", required = true, example = "2025-03-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        try {
            log.info("GET /employe/{}/rapport-periode - {} à {}", employeId, debut, fin);

            if (debut == null || fin == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Les dates de début et fin sont requises", null));
            }

            if (debut.isAfter(fin)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "La date début doit être avant la date fin", null));
            }

            RapportPeriodiqueDTO rapport = soldeService.getRapportPeriode(employeId, debut, fin);
            return ResponseEntity.ok(new ApiResponse<>(true, "Rapport généré avec succès", rapport));

        } catch (IllegalArgumentException e) {
            log.warn("Paramètres invalides: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erreur génération rapport: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur technique: " + e.getMessage(), null));
        }
    }

    // ============================================
    // 7. GESTIONNAIRE D'EXCEPTIONS LOCAL
    // ============================================

    @ExceptionHandler(SoldeInsuffisantException.class)
    public ResponseEntity<ApiResponse<?>> handleSoldeInsuffisant(SoldeInsuffisantException ex) {
        log.warn("Solde insuffisant: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Argument invalide: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        log.error("Erreur runtime: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Erreur technique: " + ex.getMessage(), null));
    }

    @GetMapping("/entreprise/{companyId}/soldes")
    @Operation(summary = "Liste les soldes de tous les employés d'une entreprise")
    public ResponseEntity<ApiResponse<?>> getSoldesParEntreprise(
            @Parameter(description = "ID de l'entreprise", required = true, example = "1")
            @PathVariable Long companyId) {

        try {
            log.info("GET /entreprise/{}/soldes", companyId);
            SoldesEntrepriseDTO soldes = soldesEntrepriseService.getSoldesParEntreprise(companyId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Soldes entreprise récupérés", soldes));

        } catch (Exception e) {
            log.error("Erreur récupération soldes entreprise {}: {}", companyId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Entreprise non trouvée: " + e.getMessage(), null));
        }
    }
}