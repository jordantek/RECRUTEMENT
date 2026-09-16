package com.tpc.tpcgestpaie.localapp.controller.alertes;

import com.tpc.tpcgestpaie.localapp.service.Alertes.AlerteAbsenceService;
import com.tpc.tpcgestpaie.localapp.service.Alertes.AlerteService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alertes-systeme")
@RequiredArgsConstructor
public class AlerteController {

    private final UserService userService;
    private final AlerteAbsenceService absenceService; // si utilisé dans d'autres endpoints
    private final AlerteService alerteService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllAlertes() {
        try {

            // Vérification utilisateur connecté (optionnel mais propre)
           // User currentUser = userService.getCurrentUser();

            // Récupération des alertes (paramètre 6 → peut représenter la limite)

            var alertes = alerteService.getAllAlertes(15);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Alertes récupérées avec succès",
                            alertes
                    )
            );

        } catch (Exception e) {

            System.err.println(e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Erreur lors de la récupération des alertes",
                            null
                    ));
        }
    }
}
