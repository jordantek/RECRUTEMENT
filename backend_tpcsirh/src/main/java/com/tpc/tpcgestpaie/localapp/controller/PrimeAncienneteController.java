package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.dto.PrimeAncienneteResponseDTO;
import com.tpc.tpcgestpaie.localapp.service.PrimeAncienneteService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prime-anciennete")
public class PrimeAncienneteController {

    private final PrimeAncienneteService primeAncienneteService;

    public PrimeAncienneteController(PrimeAncienneteService primeAncienneteService) {
        this.primeAncienneteService = primeAncienneteService;
    }

    // 🔹 Calculer la prime d'ancienneté pour un employé
    @GetMapping("/calculer/{companyId}/{employeId}")
    public ResponseEntity<ApiResponse<PrimeAncienneteResponseDTO>> calculerPrimeAnciennete(
            @PathVariable Long companyId,
            @PathVariable Long employeId) {

        try {
            PrimeAncienneteResponseDTO response = primeAncienneteService
                    .calculerPrimeAnciennete(companyId, employeId);

            // Si un message d'erreur est présent dans le DTO
            if (response.getMessage() != null && !response.isEnabled()) {
                return ResponseEntity.ok(
                        new ApiResponse<>(false, response.getMessage(), response)
                );
            }

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Prime d'ancienneté calculée avec succès", response)
            );

        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Erreur lors du calcul de la prime d'ancienneté: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // 🔹 Vérifier si la prime d'ancienneté est activée pour une entreprise
    @GetMapping("/verifier/{companyId}")
    public ResponseEntity<ApiResponse<Boolean>> verifierPrimeActive(
            @PathVariable Long companyId) {

        try {
            // Calculer avec un employeId null pour juste vérifier l'activation
            PrimeAncienneteResponseDTO response = primeAncienneteService
                    .calculerPrimeAnciennete(companyId, null);

            String message = response.isEnabled()
                    ? "La prime d'ancienneté est activée pour cette entreprise"
                    : response.getMessage();

            return ResponseEntity.ok(
                    new ApiResponse<>(response.isEnabled(), message, response.isEnabled())
            );

        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Erreur lors de la vérification: " + e.getMessage(), false),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // 🔹 Obtenir le détail du calcul (version alternative)
    @GetMapping("/detail/{companyId}/{employeId}")
    public ResponseEntity<ApiResponse<PrimeAncienneteResponseDTO>> getDetailPrimeAnciennete(
            @PathVariable Long companyId,
            @PathVariable Long employeId) {

        try {
            PrimeAncienneteResponseDTO response = primeAncienneteService
                    .calculerPrimeAnciennete(companyId, employeId);

            if (!response.isEnabled()) {
                return ResponseEntity.ok(
                        new ApiResponse<>(false, response.getMessage(), response)
                );
            }

            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            String.format("Prime d'ancienneté: %.2f pour %d ans d'ancienneté",
                                    response.getPrimeAnciennete(),
                                    response.getAncienneteAnnees()),
                            response)
            );

        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Erreur lors de l'obtention du détail: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}