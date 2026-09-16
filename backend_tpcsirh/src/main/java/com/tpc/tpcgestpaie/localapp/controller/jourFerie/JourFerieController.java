package com.tpc.tpcgestpaie.localapp.controller.jourFerie;


import com.tpc.tpcgestpaie.localapp.dto.jourFerie.JourFerieCreateDTO;
import com.tpc.tpcgestpaie.localapp.dto.jourFerie.JourFerieEntrepriseResponseDTO;
import com.tpc.tpcgestpaie.localapp.dto.jourFerie.JourFerieResponseDTO;
import com.tpc.tpcgestpaie.localapp.dto.jourFerie.JourFerieUpdateDTO;
import com.tpc.tpcgestpaie.localapp.repository.jourFerie.JourFerieRepository;
import com.tpc.tpcgestpaie.localapp.service.jourFerie.JourFerieService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST pour la gestion des jours fériés
 */

@RestController
@RequestMapping("/api/jours-feries")
@RequiredArgsConstructor
@Slf4j
public class JourFerieController {

    private final JourFerieService jourFerieService;
    private final JourFerieRepository jourFerieRepository;

    // ========================================
    // ➕ CRÉATION
    // ========================================

    @PostMapping
    public ResponseEntity<?> creerJourFerie(
            @RequestBody JourFerieCreateDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        log.info("Création d'un jour férié : {}", dto.getLibelle());

        try {
            JourFerieResponseDTO result =
                    jourFerieService.creerJourFerie(dto, userId != null ? userId : 1L);

            return new ResponseEntity<>(
                    new ApiResponse<>(true,
                            "Jour férié créé avec succès",
                            result),
                    HttpStatus.CREATED
            );

        } catch (IllegalArgumentException e) {
            log.warn("Erreur de validation : {}", e.getMessage());
            return new ResponseEntity<>(
                    new ApiResponse<>(false, e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );

        } catch (Exception e) {
            log.error("Erreur création jour férié", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Erreur lors de la création du jour férié",
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ========================================
    // 🔍 RÉCUPÉRATION PAR ID
    // ========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getJourFerieById(@PathVariable Long id) {

        log.info("Récupération du jour férié ID={}", id);

        try {
            JourFerieResponseDTO result =
                    jourFerieService.getJourFerieById(id);

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Jour férié trouvé",
                            result)
            );

        } catch (RuntimeException e) {
            log.warn("Jour férié non trouvé : {}", e.getMessage());
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Jour férié non trouvé",
                            null),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // ========================================
    // 📅 PAR ANNÉE
    // ========================================

    @GetMapping("/annee/{annee}")
    public ResponseEntity<?> getJoursFeriesParAnnee(@PathVariable Integer annee) {

        log.info("Récupération des jours fériés pour l'année {}", annee);

        try {
            List<JourFerieResponseDTO> result =
                    jourFerieService.getJoursFeriesParAnnee(annee);

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Liste des jours fériés récupérée",
                            result)
            );

        } catch (Exception e) {
            log.error("Erreur récupération jours fériés", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Erreur lors de la récupération des jours fériés",
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ========================================
    // ✏️ MISE À JOUR
    // ========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> mettreAJourJourFerie(
            @PathVariable Long id,
            @RequestBody JourFerieUpdateDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        log.info("Mise à jour du jour férié ID={}", id);

        try {
            JourFerieResponseDTO result =
                    jourFerieService.mettreAJourJourFerie(
                            id, dto, userId != null ? userId : 1L
                    );

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Jour férié mis à jour avec succès",
                            result)
            );

        } catch (RuntimeException e) {
            log.warn("Erreur mise à jour : {}", e.getMessage());
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            e.getMessage(),
                            null),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // ========================================
    // 🗑️ SUPPRESSION
    // ========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerJourFerie(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        log.info("Suppression du jour férié ID={}", id);

        try {
            jourFerieService.supprimerJourFerie(
                    id, userId != null ? userId : 1L
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            "Jour férié supprimé avec succès",
                            null)
            );

        } catch (RuntimeException e) {
            log.warn("Erreur suppression : {}", e.getMessage());
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            e.getMessage(),
                            null),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // ========================================
    // ⚙️ GÉNÉRATION AUTOMATIQUE
    // ========================================

    @PostMapping("/generer/{annee}")
    public ResponseEntity<?> genererJoursFeries(
            @PathVariable Integer annee,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        log.info("Génération automatique des jours fériés pour l'année {}", annee);

        try {
            List<JourFerieResponseDTO> result =
                    jourFerieService.genererJoursFeriesAnnee(
                            annee, userId != null ? userId : 1L
                    );

            return new ResponseEntity<>(
                    new ApiResponse<>(true,
                            result.size() + " jours fériés générés pour l'année " + annee,
                            result),
                    HttpStatus.CREATED
            );

        } catch (Exception e) {
            log.error("Erreur génération jours fériés", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(false,
                            "Erreur lors de la génération des jours fériés",
                            null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
