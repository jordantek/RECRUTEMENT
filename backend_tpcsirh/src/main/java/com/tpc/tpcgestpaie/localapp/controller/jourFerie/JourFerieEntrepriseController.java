package com.tpc.tpcgestpaie.localapp.controller.jourFerie;

import com.tpc.tpcgestpaie.localapp.dto.jourFerie.JourFerieEntrepriseRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.jourFerie.JourFerieEntrepriseResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerie;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerieEntreprise;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.jourFerie.JourFerieEntrepriseService;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies/{companyId}/jours-feries")
@RequiredArgsConstructor
@Slf4j
public class JourFerieEntrepriseController {

    private final JourFerieEntrepriseService jourFerieEntrepriseService;
    private final UserService userService;
    private final CompanyService companyService;

    // ========================================
    // 🎯 JOURS FÉRIÉS EFFECTIFS
    // ========================================

    @GetMapping("/effectifs/{annee}")
    public ResponseEntity<?> getJoursFeriesEffectifs(
            @PathVariable Long companyId,
            @PathVariable int annee) {

        try {
            List<JourFerie> joursFeries =
                    jourFerieEntrepriseService.getJoursFeriesEffectifs(companyId, annee);

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Jours fériés récupérés avec succès",
                            joursFeries)
            );

        } catch (Exception e) {
            log.error("Erreur récupération jours fériés effectifs", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Erreur lors de la récupération des jours fériés",
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/effectifs/mois/{annee}/{mois}")
    public ResponseEntity<?> getJoursFeriesMois(
            @PathVariable Long companyId,
            @PathVariable int annee,
            @PathVariable int mois) {

        try {
            List<JourFerie> joursFeries =
                    jourFerieEntrepriseService.getJoursFeriesEffectifs(companyId, annee)
                            .stream()
                            .filter(j -> j.getDateFerie().getMonthValue() == mois)
                            .toList();

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Jours fériés du mois récupérés",
                            joursFeries)
            );

        } catch (Exception e) {
            log.error("Erreur récupération jours fériés par mois", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Erreur lors de la récupération des jours fériés du mois",
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkJourFerie(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            boolean isJourFerie =
                    jourFerieEntrepriseService.isJourFerie(companyId, date);

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            isJourFerie ? "C'est un jour férié" : "Ce n'est pas un jour férié",
                            isJourFerie)
            );

        } catch (Exception e) {
            log.error("Erreur vérification jour férié", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Erreur lors de la vérification du jour férié",
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * GET /api/companies/{companyId}/jours-feries/ajoutes/{annee}
     * Liste uniquement les jours fériés ajoutés
     */
    @GetMapping("/ajoutes/{annee}")
    public ResponseEntity<ApiResponse<List<JourFerieEntrepriseResponseDTO>>> getJoursFeriesAjoutes(
            @PathVariable Long companyId,
            @PathVariable int annee) {
        try {

            List<JourFerieEntreprise> joursAjoutes =
                    jourFerieEntrepriseService.getJoursFeriesAjoutes(companyId, annee);

            Company company = companyService.getById(companyId);

// Conversion en DTO
            List<JourFerieEntrepriseResponseDTO> joursAjoutesDto =
                    joursAjoutes.stream()
                            .map(jf -> convertirVersDTO(jf, company))
                            .toList();

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Jours fériés ajoutés récupérés avec succès",
                            joursAjoutesDto
                    )
            );



        } catch (Exception e) {
            log.error("Erreur lors de la récupération des jours fériés ajoutés", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Erreur lors de la récupération des jours fériés ajoutés", null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    /**
     * GET /api/companies/{companyId}/jours-feries/retires/{annee}
     * Liste uniquement les jours fériés retirés
     */
    @GetMapping("/retires/{annee}")
    public ResponseEntity<ApiResponse<List<JourFerieEntrepriseResponseDTO>>> getJoursFeriesRetires(
            @PathVariable Long companyId,
            @PathVariable int annee) {
        try {
            List<JourFerieEntreprise> joursRetires =
                    jourFerieEntrepriseService.getJoursFeriesRetires(companyId, annee);

            Company company = companyService.getById(companyId);

// Conversion en DTO
            List<JourFerieEntrepriseResponseDTO> retiresdto =
                    joursRetires.stream()
                            .map(jf -> convertirVersDTO(jf, company))
                            .toList();

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Jours fériés retirés récupérés avec succès",
                    retiresdto
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des jours fériés retirés", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Erreur lors de la récupération des jours fériés retirés", null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ========================================
    // ➕ AJOUT
    // ========================================

    @PostMapping("/ajouter")
    public ResponseEntity<?> ajouterJourFeriePersonnalise(
            @PathVariable Long companyId,
            @Valid @RequestBody JourFerieEntrepriseRequestDTO request) {

        try {
            if (request.getLibelle() == null || request.getLibelle().trim().isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponse<>(false, "Le libellé est obligatoire", null),
                        HttpStatus.BAD_REQUEST
                );
            }

            if (request.getDateFerie() == null) {
                return new ResponseEntity<>(
                        new ApiResponse<>(false, "La date est obligatoire", null),
                        HttpStatus.BAD_REQUEST
                );
            }
            User currentUser = userService.getCurrentUser();

            JourFerieEntreprise jourFerie =
                    jourFerieEntrepriseService.ajouterJourFeriePersonnalise(
                            companyId, request, currentUser.getId()
                    );

            Company company = companyService.getById(companyId);
            JourFerieEntrepriseResponseDTO dto =
                    convertirVersDTO(jourFerie, company);

            return new ResponseEntity<>(
                    new ApiResponse<>(true,
                            "Jour férié ajouté avec succès",
                            dto),
                    HttpStatus.CREATED
            );

        } catch (Exception e) {
            log.error("Erreur ajout jour férié personnalisé", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Erreur lors de l'ajout du jour férié",
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ========================================
    // ➖ RETRAIT
    // ========================================

    @PostMapping("/retirer")
    public ResponseEntity<?> retirerJourFerieNational(
            @PathVariable Long companyId,
            @RequestParam String slug,
            @RequestParam int annee
            ) {

        try {

            // Validation des paramètres
            if (slug == null || slug.trim().isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponse<>(false, "Le slug du jour férié est obligatoire", null),
                        HttpStatus.BAD_REQUEST
                );
            }

            User currentUser = userService.getCurrentUser();
            JourFerieEntreprise jourFerie =
                    jourFerieEntrepriseService
                            .retirerJourFerieNational(companyId, slug, annee, currentUser.getId());

            Company company = companyService.getById(companyId);
            JourFerieEntrepriseResponseDTO dto =
                    convertirVersDTO(jourFerie, company);
            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Jour férié retiré avec succès",
                            dto)
            );

        } catch (Exception e) {
            log.error("Erreur retrait jour férié", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Jour férié déjà retiré ou n'existe pas",
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ========================================
    // 📋 MODIFICATIONS
    // ========================================

    @GetMapping("/modifications/{annee}")
    public ResponseEntity<?> getModifications(
            @PathVariable Long companyId,
            @PathVariable int annee) {

        try {
            Map<String, List<JourFerieEntreprise>> modifications =
                    jourFerieEntrepriseService.getModifications(companyId, annee);

            Company company = companyService.getById(companyId);

// Conversion en DTO
            Map<String, List<JourFerieEntrepriseResponseDTO>> modificationsDto =
                    modifications.entrySet()
                            .stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue()
                                            .stream()
                                            .map(jf -> convertirVersDTO(jf, company))
                                            .toList()
                            ));

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Modifications récupérées avec succès",
                            modificationsDto
                    )
            );

        } catch (Exception e) {
            log.error("Erreur récupération modifications", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Erreur lors de la récupération des modifications",
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ========================================
    // 🗑️ SUPPRESSION
    // ========================================

    @DeleteMapping("/{jourFerieId}")
    public ResponseEntity<?> supprimerModification(
            @PathVariable Long companyId,
            @PathVariable Long jourFerieId) {

        try {
            jourFerieEntrepriseService.supprimerModification(jourFerieId);

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Modification supprimée avec succès",
                            null)
            );

        } catch (Exception e) {
            log.error("Erreur suppression modification", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Erreur lors de la suppression",
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping("/annee/{annee}")
    public ResponseEntity<?> supprimerToutesModifications(
            @PathVariable Long companyId,
            @PathVariable int annee) {

        try {
            jourFerieEntrepriseService.supprimerToutesModifications(companyId, annee);

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Toutes les modifications ont été supprimées",
                            null)
            );

        } catch (Exception e) {
            log.error("Erreur suppression toutes modifications", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Erreur lors de la suppression des modifications",
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ========================================
    // 📊 STATISTIQUES
    // ========================================

    @GetMapping("/statistiques/{annee}")
    public ResponseEntity<?> getStatistiques(
            @PathVariable Long companyId,
            @PathVariable int annee) {

        try {
            Map<String, Object> stats =
                    jourFerieEntrepriseService.getStatistiques(companyId, annee);

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Statistiques récupérées avec succès",
                            stats)
            );

        } catch (Exception e) {
            log.error("Erreur statistiques jours fériés", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Erreur lors de la récupération des statistiques",
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ========================================
    // 🔁 DTO
    // ========================================

    private JourFerieEntrepriseResponseDTO convertirVersDTO(
            JourFerieEntreprise entity,
            Company company) {

        return new JourFerieEntrepriseResponseDTO(
                entity.getId(),
                company.getId(),
                company.getName(),
                entity.getJourFerie() != null ? entity.getJourFerie().getId() : null,
                entity.getStatut(),
                entity.getSlug(),
                entity.getLibelle(),
                entity.getDateFerie(),
                entity.getPays(),
                entity.getEstFixe(),
                entity.getEstRecurrent(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                null,
                null,
                null
        );
    }
}
