package com.tpc.tpcgestpaie.localapp.controller.alertes;

import com.tpc.tpcgestpaie.localapp.config.AbsenceConfig;
import com.tpc.tpcgestpaie.localapp.dto.alertes.AbsenceAlerteDTO;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.Alertes.AlerteAbsenceService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(
        name = "Gestion administrative/Alertes d'Absence",
        description = """
        API de gestion des alertes d'absence : suivi des départs, retours, absences en cours et prochaines.
        Fournit des fonctionnalités de monitoring des absences des employés avec système d'alerte intelligent.
        
        Caractéristiques principales :
        - Suivi en temps réel des absences
        - Alertes personnalisées par utilisateur
        - Statistiques et rapports détaillés
        - Configuration flexible des seuils d'alerte
        """
)
@RestController
@RequestMapping("/api/alertes-absences")
public class AbsenceAlerteController {

    private final AlerteAbsenceService alerteAbsenceService;
    private final AbsenceConfig absenceConfig;
    private final UserService userService;

    public AbsenceAlerteController(AlerteAbsenceService alerteAbsenceService, AbsenceConfig absenceConfig, UserService userService) {
        this.alerteAbsenceService = alerteAbsenceService;
        this.absenceConfig = absenceConfig;
        this.userService = userService;
    }

    @Operation(
            summary = "Récupérer les départs prochains en congé",
            description = """
                Retourne la liste des employés dont le départ en congé est prévu dans les jours à venir.
                
                Fonctionnalités :
                - Filtre par nombre de jours personnalisable
                - Alerte préventive pour les RH et managers
                - Intégration avec le calendrier des absences
                - Personnalisation selon les permissions utilisateur
                
                Utilisation typique :
                - Planification des remplacements
                - Ajustement des plannings
                - Préparation des congés
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des départs prochains récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des départs prochains récupérée avec succès",
                      "data": {
                        "alertes": [
                          {
                            "employeId": 123,
                            "employeNom": "Dupont Jean",
                            "dateDebut": "2024-03-20",
                            "dateFin": "2024-03-27",
                            "typeAbsence": "CONGE_PAYE",
                            "joursRestants": 2,
                            "statut": "A_VENIR",
                            "service": "Informatique"
                          },
                          {
                            "employeId": 124,
                            "employeNom": "Martin Sophie",
                            "dateDebut": "2024-03-22",
                            "dateFin": "2024-04-05",
                            "typeAbsence": "CONGE_MATERNITE",
                            "joursRestants": 4,
                            "statut": "A_VENIR",
                            "service": "Ressources Humaines"
                          }
                        ],
                        "total": 2,
                        "joursRecherche": 7,
                        "type": "DEPARTS"
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Utilisateur non authentifié",
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
    @GetMapping("/departs-prochains")
    public ResponseEntity<ApiResponse<?>> getDepartsProchains(
            @Parameter(
                    description = "Nombre de jours pour la recherche (défaut: 7)",
                    example = "7"
            )
            @RequestParam(defaultValue = "7") int jours) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<AbsenceAlerteDTO> alertes = alerteAbsenceService.getDepartsProchains(currentUser, jours);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("alertes", alertes);
            responseData.put("total", alertes.size());
            responseData.put("joursRecherche", jours);
            responseData.put("type", "DEPARTS");

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des départs prochains récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des départs: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les retours prochains de congé",
            description = """
                Retourne la liste des employés dont le retour de congé est prévu dans les jours à venir.
                
                Fonctionnalités :
                - Prépare le retour des collaborateurs
                - Facilite la réintégration
                - Alertes pour les responsables
                - Suivi du planning de reprise
                
                Utilisation typique :
                - Préparation des réunions de reprise
                - Mise à jour des plannings
                - Coordination des équipes
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des retours prochains récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des retours prochains récupérée avec succès",
                      "data": {
                        "alertes": [
                          {
                            "employeId": 125,
                            "employeNom": "Leroy Thomas",
                            "dateRetour": "2024-03-19",
                            "typeAbsence": "CONGE_PAYE",
                            "joursRestants": 1,
                            "statut": "RETOUR_PROCHAIN",
                            "service": "Marketing"
                          }
                        ],
                        "total": 1,
                        "joursRecherche": 7,
                        "type": "RETOURS"
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/retours-prochains")
    public ResponseEntity<ApiResponse<?>> getRetoursProchains(
            @Parameter(
                    description = "Nombre de jours pour la recherche (défaut: 7)",
                    example = "7"
            )
            @RequestParam(defaultValue = "7") int jours) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<AbsenceAlerteDTO> alertes = alerteAbsenceService.getRetoursProchains(currentUser, jours);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("alertes", alertes);
            responseData.put("total", alertes.size());
            responseData.put("joursRecherche", jours);
            responseData.put("type", "RETOURS");

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des retours prochains récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des retours: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les absences actuellement en cours",
            description = """
                Retourne la liste des employés actuellement en congé.
                
                Fonctionnalités :
                - Vue en temps réel des absences
                - Filtrage par service ou équipe
                - Information sur la durée restante
                - État des dossiers de congé
                
                Utilisation typique :
                - Gestion quotidienne des effectifs
                - Suivi des congés maladie
                - Tableau de bord RH
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des absences en cours récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des absences en cours récupérée avec succès",
                      "data": {
                        "alertes": [
                          {
                            "employeId": 126,
                            "employeNom": "Petit Claire",
                            "dateDebut": "2024-03-10",
                            "dateFin": "2024-03-24",
                            "typeAbsence": "MALADIE",
                            "joursEcoules": 5,
                            "joursRestants": 9,
                            "statut": "EN_COURS",
                            "service": "Finance"
                          }
                        ],
                        "total": 1,
                        "type": "EN_COURS"
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/en-cours")
    public ResponseEntity<ApiResponse<?>> getAbsencesEnCours() {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<AbsenceAlerteDTO> alertes = alerteAbsenceService.getAbsencesEnCours(currentUser);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("alertes", alertes);
            responseData.put("total", alertes.size());
            responseData.put("type", "EN_COURS");

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des absences en cours récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des absences en cours: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer toutes les absences prochaines",
            description = """
                Retourne une vue consolidée de toutes les absences à venir.
                
                Fonctionnalités :
                - Vue unifiée départs + retours
                - Filtrage par période personnalisable
                - Tri par date et priorité
                - Indicateurs de criticité
                
                Utilisation typique :
                - Planification globale des ressources
                - Analyse des pics d'absence
                - Prévision des besoins en personnel
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste de toutes les absences prochaines récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste de toutes les absences prochaines récupérée avec succès",
                      "data": {
                        "alertes": [
                          {
                            "employeId": 127,
                            "employeNom": "Moreau Luc",
                            "dateDebut": "2024-03-25",
                            "dateFin": "2024-04-08",
                            "typeAbsence": "CONGE_FORMATION",
                            "joursRestants": 5,
                            "statut": "A_VENIR",
                            "priorite": "MOYENNE",
                            "service": "Production"
                          }
                        ],
                        "total": 1,
                        "joursRecherche": 7,
                        "type": "TOUTES"
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/prochaines")
    public ResponseEntity<ApiResponse<?>> getAbsencesProchaines(
            @Parameter(
                    description = "Nombre de jours pour la recherche (défaut: 7)",
                    example = "7"
            )
            @RequestParam(defaultValue = "7") int jours) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<AbsenceAlerteDTO> alertes = alerteAbsenceService.getAbsencesProchaines(currentUser, jours);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("alertes", alertes);
            responseData.put("total", alertes.size());
            responseData.put("joursRecherche", jours);
            responseData.put("type", "TOUTES");

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste de toutes les absences prochaines récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des absences: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Déclencher une vérification manuelle des alertes",
            description = """
                Déclenche immédiatement une vérification manuelle de toutes les alertes d'absence.
                
                Fonctionnalités :
                - Forçage de la vérification en temps réel
                - Mise à jour instantanée des statuts
                - Génération de notifications
                - Nettoyage des alertes obsolètes
                
                Utilisation typique :
                - Tests et débogage
                - Mise à jour après import de données
                - Synchronisation avec systèmes externes
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Vérification des alertes absence lancée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Vérification des alertes absence lancée avec succès",
                      "data": null
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/verifier-maintenant")
    public ResponseEntity<ApiResponse<?>> verifierAlertesMaintenant() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            alerteAbsenceService.verifierAlertesAbsences(currentUser);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Vérification des alertes absence lancée avec succès", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la vérification des alertes: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les statistiques des absences",
            description = """
                Retourne des statistiques consolidées sur les absences.
                
                Métriques incluses :
                - Nombre de départs prochains
                - Nombre de retours prochains
                - Nombre d'absences en cours
                - Configuration actuelle du système
                
                Utilisation typique :
                - Tableau de bord RH
                - Rapports de gestion
                - Analyse des tendances
                - Optimisation des seuils d'alerte
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Statistiques des absences récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Statistiques des absences récupérées avec succès",
                      "data": {
                        "departsProchains": 3,
                        "retoursProchains": 2,
                        "absencesEnCours": 5,
                        "config": {
                          "seuilAlerteJours": 7,
                          "notificationsActives": true,
                          "typesAbsencesSurveilles": [
                            "CONGE_PAYE",
                            "MALADIE",
                            "CONGE_MATERNITE"
                          ],
                          "emailsNotifications": [
                            "rh@entreprise.com",
                            "managers@entreprise.com"
                          ]
                        }
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/statistiques")
    public ResponseEntity<ApiResponse<?>> getStatistiques() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<AbsenceAlerteDTO> departs = alerteAbsenceService.getDepartsProchains(currentUser, 7);
            List<AbsenceAlerteDTO> retours = alerteAbsenceService.getRetoursProchains(currentUser, 7);
            List<AbsenceAlerteDTO> enCours = alerteAbsenceService.getAbsencesEnCours(currentUser);

            Map<String, Object> statistiques = new HashMap<>();
            statistiques.put("departsProchains", departs.size());
            statistiques.put("retoursProchains", retours.size());
            statistiques.put("absencesEnCours", enCours.size());
            statistiques.put("config", absenceConfig);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Statistiques des absences récupérées avec succès", statistiques));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des statistiques: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer la configuration des alertes",
            description = """
                Retourne la configuration actuelle du système d'alertes d'absence.
                
                Paramètres de configuration :
                - Seuils d'alerte en jours
                - Types d'absence surveillés
                - Paramètres de notification
                - Options de personnalisation
                
                Utilisation typique :
                - Administration du système
                - Audit des paramètres
                - Configuration des alertes
                - Personnalisation par entreprise
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Configuration des alertes absence récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Configuration des alertes absence récupérée avec succès",
                      "data": {
                        "seuilAlerteJours": 7,
                        "actif": true,
                        "notificationEmail": true,
                        "notificationInApp": true,
                        "typesAbsencesSurveilles": [
                          "CONGE_PAYE",
                          "MALADIE",
                          "CONGE_MATERNITE",
                          "CONGE_FORMATION"
                        ],
                        "exclusionsServices": [],
                        "joursOuverture": [1, 2, 3, 4, 5],
                        "heureVerification": "08:00",
                        "version": "2.1.0"
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur",
                    content = @Content
            )
    })
    @GetMapping("/config")
    public ResponseEntity<ApiResponse<?>> getConfig() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Configuration des alertes absence récupérée avec succès", absenceConfig));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération de la configuration: " + e.getMessage(), null));
        }
    }
}