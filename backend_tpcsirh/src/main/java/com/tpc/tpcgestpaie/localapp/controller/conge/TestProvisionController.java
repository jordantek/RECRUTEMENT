package com.tpc.tpcgestpaie.localapp.controller.conge;

import com.tpc.tpcgestpaie.localapp.model.conge.ProvisionConge;
import com.tpc.tpcgestpaie.localapp.service.conge.ProvisionManuelleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test/conges")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "TEST - Gestion Manuelle Provisions", description = "API de test pour créer manuellement des provisions")
public class TestProvisionController {

    private final ProvisionManuelleService provisionManuelleService;

    // ============================================
    // CRÉATION MANUELLE
    // ============================================

    @Operation(summary = "Créer une provision manuellement")
    @PostMapping("/provision/creer")
    public ResponseEntity<ProvisionConge> creerProvision(
            @RequestBody CreationProvisionRequest request) {

        ProvisionConge provision = provisionManuelleService.creerProvisionManuelle(
                request.getEmployeId(),
                request.getMoisReference(), // "2025-01"
                request.getSalaireBrut(),
                request.getJoursTravailles(),
                request.getJoursAcquis()
        );

        return ResponseEntity.ok(provision);
    }

    @Operation(summary = "Créer plusieurs provisions de test (scénario auto)")
    @PostMapping("/provision/creer-test/{employeId}")
    public ResponseEntity<Map<String, Object>> creerProvisionsTest(
            @PathVariable Long employeId,
            @RequestParam(defaultValue = "6") int nombreMois) {

        provisionManuelleService.creerProvisionsTest(employeId, nombreMois);

        Map<String, Object> result = new HashMap<>();
        result.put("message", nombreMois + " mois de provisions créés");
        result.put("employeId", employeId);
        result.put("moisGenere", nombreMois);

        return ResponseEntity.ok(result);
    }

    // ============================================
    // CONSULTATION
    // ============================================

    @Operation(summary = "Lister toutes les provisions d'un employé")
    @GetMapping("/provision/employe/{employeId}")
    public ResponseEntity<List<ProvisionConge>> listerProvisions(
            @PathVariable Long employeId) {

        List<ProvisionConge> provisions = provisionManuelleService.listerProvisions(employeId);
        return ResponseEntity.ok(provisions);
    }

    // ============================================
    // SUPPRESSION (Test uniquement)
    // ============================================

    @Operation(summary = "Supprimer une provision (si non consommée)")
    @DeleteMapping("/provision/{provisionId}")
    public ResponseEntity<Map<String, String>> supprimerProvision(
            @PathVariable Long provisionId) {

        provisionManuelleService.supprimerProvision(provisionId);

        Map<String, String> result = new HashMap<>();
        result.put("message", "Provision supprimée");
        result.put("provisionId", provisionId.toString());

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "RÉINITIALISER toutes les provisions d'un employé (DANGER)")
    @DeleteMapping("/provision/employe/{employeId}/reinitialiser")
    public ResponseEntity<Map<String, String>> reinitialiser(
            @PathVariable Long employeId) {

        provisionManuelleService.reinitialiserProvisions(employeId);

        Map<String, String> result = new HashMap<>();
        result.put("message", "Toutes les provisions supprimées");
        result.put("employeId", employeId.toString());
        result.put("warning", "Opération irréversible");

        return ResponseEntity.ok(result);
    }

    // ============================================
    // DTOs Internes
    // ============================================

    @Data
    public static class CreationProvisionRequest {
        private Long employeId;
        private String moisReference; // "2025-01"
        private BigDecimal salaireBrut;
        private BigDecimal joursTravailles;
        private BigDecimal joursAcquis;
    }
}