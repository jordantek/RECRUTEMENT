package com.tpc.tpcgestpaie.localapp.controller.jourtravail;

import com.tpc.tpcgestpaie.localapp.dto.jourtravail.JoursTravaillesDTO;
import com.tpc.tpcgestpaie.localapp.service.jourtravail.JoursTravaillesService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jours-travailles")

public class JoursTravaillesController {

    private final JoursTravaillesService joursTravaillesService;

    public JoursTravaillesController(JoursTravaillesService joursTravaillesService) {
        this.joursTravaillesService = joursTravaillesService;
    }

    // 🔹 Récupérer la configuration pour une entreprise
    @GetMapping("/entreprise/{companyId}")
    public ResponseEntity<ApiResponse<JoursTravaillesDTO>> getByCompany(
            @PathVariable Long companyId) {
        try {
            JoursTravaillesDTO dto = joursTravaillesService.getByCompanyId(companyId);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Configuration récupérée avec succès", dto)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Erreur: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // 🔹 Sauvegarder ou mettre à jour
    @PostMapping
    public ResponseEntity<ApiResponse<JoursTravaillesDTO>> saveOrUpdate(
            @RequestBody JoursTravaillesDTO dto) {
        try {
            JoursTravaillesDTO saved = joursTravaillesService.saveOrUpdate(dto);
            return new ResponseEntity<>(
                    new ApiResponse<>(true, "Configuration enregistrée avec succès", saved),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Erreur: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // 🔹 Appliquer une configuration standard
    @PostMapping("/configurer/{companyId}/{type}")
    public ResponseEntity<ApiResponse<JoursTravaillesDTO>> setConfigurationStandard(
            @PathVariable Long companyId,
            @PathVariable String type) {
        try {
            JoursTravaillesDTO dto = joursTravaillesService.setConfigurationStandard(companyId, type);
            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            String.format("Configuration '%s' appliquée avec succès", type),
                            dto)
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Erreur: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // 🔹 Vérifier si un jour est travaillé
    @GetMapping("/verifier/{companyId}/{jour}")
    public ResponseEntity<ApiResponse<Boolean>> estJourTravaille(
            @PathVariable Long companyId,
            @PathVariable String jour) {
        try {
            boolean estTravaille = joursTravaillesService.estJourTravaille(companyId, jour);
            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            String.format("Le %s est %s travaillé", jour, estTravaille ? "" : "non"),
                            estTravaille)
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Erreur: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // 🔹 Calculer les jours travaillés entre deux dates
    @GetMapping("/calculer/{companyId}")
    public ResponseEntity<ApiResponse<Integer>> calculerJoursTravailles(
            @PathVariable Long companyId,
            @RequestParam String dateDebut,
            @RequestParam String dateFin) {
        try {
            int jours = joursTravaillesService.calculerJoursTravailles(companyId, dateDebut, dateFin);
            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            String.format("%d jours travaillés entre %s et %s", jours, dateDebut, dateFin),
                            jours)
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Erreur: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
//
//    @GetMapping("/entreprise/{companyId}")
//    public ResponseEntity<ApiResponse<JoursTravaillesDTO>> getByCompany(
//            @PathVariable Long companyId) {
//        try {
//            JoursTravaillesDTO dto = joursTravaillesService.getByCompanyId(companyId);
//            return ResponseEntity.ok(
//                    new ApiResponse<>(true, "Configuration récupérée avec succès", dto)
//            );
//        } catch (Exception e) {
//            return new ResponseEntity<>(
//                    new ApiResponse<>(false, "Erreur: " + e.getMessage(), null),
//                    HttpStatus.INTERNAL_SERVER_ERROR
//            );
//        }
//    }
}