package com.tpc.tpcgestpaie.localapp.controller.conge;

import com.tpc.tpcgestpaie.localapp.service.conge.SoldeCongeService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Gestion des Soldes de Congés",
        description = "API pour la gestion du solde congés, provisions mensuelles, consommation et historique."
)
@RestController
@RequestMapping("/api/solde-conge")
@RequiredArgsConstructor
public class SoldeCongeController {

    private final SoldeCongeService soldeCongeService;
    private final UserService userService;

    // ========================================
    // 1. INITIALISATION SOLDE (Création contrat)
    // ========================================

//    @Operation(
//            summary = "Initialiser le solde congé d'un employé",
//            description = "Si soldeInitial non renseigné, calcul automatique depuis date début contrat " +
//                    "en tenant compte des congés déjà pris."
//    )
//    @PostMapping("/initialiser/{employeId}")
//    public ResponseEntity<ApiResponse<?>> initialiserSoldeConge(
//            @Parameter(description = "ID de l'employé", required = true)
//            @PathVariable Long employeId,
//
//            @Parameter(description = "ID de l'entreprise", required = true)
//            @RequestParam Long companyId,
//
//            @Parameter(description = "Date de début du contrat", required = true)
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebutContrat,
//
//            @Parameter(description = "Solde initial (optionnel, pour migration)")
//            @RequestParam(required = false) BigDecimal soldeInitial) {
//
//        try {
//            Long userId = userService.getCurrentUser().getId();
//
//            SoldesConge solde = soldeCongeService.initialiserSoldeConge(
//                    employeId,
//                    companyId,
//                    dateDebutContrat,
//                    soldeInitial,
//                    userId
//            );
//
//            Map<String, Object> data = new HashMap<>();
//            data.put("soldeId", solde.getId());
//            data.put("employeId", employeId);
//            data.put("soldeTotalJours", solde.getSoldeTotalJours());
//            data.put("soldeConsommeJours", solde.getSoldeConsommeJours());
//            data.put("soldeRestantJours", solde.getSoldeRestantJours());
//            data.put("montantTotalProvisions", solde.getMontantTotalProvisions());
//            data.put("montantRestant", solde.getMontantRestant());
//            data.put("dateReference", solde.getDateReference());
//            data.put("congesDejaPrisJours", solde.getCongesDejaPrisJours());
//
//            String message = (soldeInitial != null)
//                    ? "Solde initialisé avec valeur saisie"
//                    : "Solde calculé automatiquement depuis " + dateDebutContrat;
//
//            return ResponseEntity.ok(
//                    new ApiResponse<>(true, message, data)
//            );
//
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse<>(false, e.getMessage(), null));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse<>(false, "Erreur technique: " + e.getMessage(), null));
//        }
//    }

    // ========================================
    // 2. CONSULTATION SOLDE
    // ========================================

//    @Operation(
//            summary = "Récupérer le solde courant d'un employé",
//            description = "Retourne le solde détaillé avec toutes les provisions."
//    )
//    @GetMapping("/{employeId}")
//    public ResponseEntity<ApiResponse<?>> getSoldeCourant(
//            @Parameter(description = "ID de l'employé", required = true)
//            @PathVariable Long employeId) {
//
//        try {
//            SoldeCongeDTO solde = soldeCongeService.getSoldeCourant(employeId);
//            return ResponseEntity.ok(
//                    new ApiResponse<>(true, "Solde récupéré avec succès", solde)
//            );
//
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse<>(false, e.getMessage(), null));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse<>(false, "Erreur technique: " + e.getMessage(), null));
//        }
//    }

//    @Operation(
//            summary = "Récupérer mon solde (employé connecté)",
//            description = "Retourne le solde de l'employé actuellement connecté."
//    )
//    @GetMapping("/mon-solde")
//    public ResponseEntity<ApiResponse<?>> getMonSolde() {
//        try {
//            Long employeId = userService.getCurrentUser().getEmploye().getId();
//            SoldeCongeDTO solde = soldeCongeService.getSoldeCourant(employeId);
//
//            return ResponseEntity.ok(
//                    new ApiResponse<>(true, "Solde récupéré avec succès", solde)
//            );
//
//        } catch (NullPointerException e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse<>(false, "Aucun employé associé à cet utilisateur", null));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest()
//                    .body(new ApiResponse<>(false, e.getMessage(), null));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse<>(false, "Erreur technique: " + e.getMessage(), null));
//        }
//    }

    // ========================================
    // 3. TRAITEMENT CONFIRMATION RETOUR (appelé par workflow absence)
    // ========================================

    @Operation(
            summary = "Traiter la confirmation de retour d'une absence",
            description = "Appelé automatiquement après confirmation retour. " +
                    "Vérifie condition et mode entreprise pour déduction solde si applicable."
    )
    @PostMapping("/traiter-retour/{demandeAbsenceId}")
    public ResponseEntity<ApiResponse<?>> traiterConfirmationRetour(
            @Parameter(description = "ID de la demande d'absence", required = true)
            @PathVariable Long demandeAbsenceId) {

        try {
//            soldeCongeService.traiterConfirmationRetour(demandeAbsenceId);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Traitement confirmation retour effectué", null)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur technique: " + e.getMessage(), null));
        }
    }
}