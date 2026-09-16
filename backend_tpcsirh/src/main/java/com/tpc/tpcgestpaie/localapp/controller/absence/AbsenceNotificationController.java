package com.tpc.tpcgestpaie.localapp.controller.absence;

import com.tpc.tpcgestpaie.localapp.model.absence.NotificationDemandeAbsence;
import com.tpc.tpcgestpaie.localapp.service.absence.NotificationDemandeAbsenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(
        name = "Gestion administrative/Notifications d'Absence",
        description = "API de gestion des notifications liées aux demandes d'absence"
)
@RestController
@RequestMapping("/api/notifications")
public class AbsenceNotificationController {

    private final NotificationDemandeAbsenceService notificationService;

    public AbsenceNotificationController(NotificationDemandeAbsenceService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
            summary = "Récupérer les notifications non lues",
            description = "Retourne la liste des notifications non lues pour un employé donné"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des notifications récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotificationDemandeAbsence.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Paramètre employeId invalide",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès non autorisé",
                    content = @Content
            )
    })
    @GetMapping("/non-lues")
//    @PreAuthorize("hasRole('EMPLOYE') or hasRole('MANAGER') or hasRole('DIRECTOR')")
    public ResponseEntity<List<NotificationDemandeAbsence>> getNotificationsNonLues(
            @Parameter(
                    description = "Identifiant unique de l'employé",
                    required = true,
                    example = "1"
            )
            @RequestParam Long employeId) {

        List<NotificationDemandeAbsence> notifications =
                notificationService.getNotificationsNonLues(employeId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(
            summary = "Obtenir le compteur de notifications",
            description = "Retourne le nombre total de notifications non lues pour un employé"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Compteur récupéré avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemple de réponse",
                                    value = "{\"nonLues\": 5}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Paramètre employeId invalide",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié",
                    content = @Content
            )
    })
    @GetMapping("/compteur")
//    @PreAuthorize("hasRole('EMPLOYE') or hasRole('MANAGER') or hasRole('DIRECTOR')")
    public ResponseEntity<Map<String, Long>> getCompteurNotifications(
            @Parameter(
                    description = "Identifiant unique de l'employé",
                    required = true,
                    example = "1"
            )
            @RequestParam Long employeId) {

        long count = notificationService.compterNotificationsNonLues(employeId);
        return ResponseEntity.ok(Map.of("nonLues", count));
    }

    @Operation(
            summary = "Marquer une notification comme lue",
            description = "Change le statut d'une notification pour la marquer comme lue"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification marquée comme lue avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = "{\"message\": \"Notification marquée comme lue\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erreur lors du traitement",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Erreur",
                                    value = "{\"error\": \"Notification introuvable\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notification non trouvée",
                    content = @Content
            )
    })
    @PostMapping("/{notificationId}/marquer-lue")
//    @PreAuthorize("hasRole('EMPLOYE') or hasRole('MANAGER') or hasRole('DIRECTOR')")
    public ResponseEntity<Map<String, String>> marquerCommeLue(
            @Parameter(
                    description = "Identifiant unique de la notification",
                    required = true,
                    example = "123"
            )
            @PathVariable Long notificationId) {

        try {
            notificationService.marquerCommeLue(notificationId);
            return ResponseEntity.ok(Map.of("message", "Notification marquée comme lue"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Récupérer l'historique des notifications",
            description = "Retourne l'historique complet des notifications (lues et non lues) pour un employé"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Historique récupéré avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotificationDemandeAbsence.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Paramètre employeId invalide",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès non autorisé",
                    content = @Content
            )
    })
    @GetMapping("/historique")
//    @PreAuthorize("hasRole('EMPLOYE') or hasRole('MANAGER') or hasRole('DIRECTOR')")
    public ResponseEntity<List<NotificationDemandeAbsence>> getHistorique(
            @Parameter(
                    description = "Identifiant unique de l'employé",
                    required = true,
                    example = "1"
            )
            @RequestParam Long employeId) {

        List<NotificationDemandeAbsence> historique =
                notificationService.getHistoriqueNotifications(employeId);
        return ResponseEntity.ok(historique);
    }
}