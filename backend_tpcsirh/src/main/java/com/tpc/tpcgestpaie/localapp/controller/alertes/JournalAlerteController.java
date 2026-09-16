package com.tpc.tpcgestpaie.localapp.controller.alertes;

import com.tpc.tpcgestpaie.localapp.dto.alertes.JournalAlerteDTO;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.Alertes.AlerteJournalService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(
        name = "Gestion administrative/Alerte Journal",
        description = """
        API de gestion des événements et alertes quotidiennes du système.
        Système centralisé de notifications et d'événements à venir pour les utilisateurs.
        
        Types d'événements supportés :
        - Rendez-vous et réunions
        - Échéances administratives
        - Rappels de tâches
        - Notifications système
        - Événements d'entreprise
        
        Caractéristiques principales :
        - Vue consolidée des événements
        - Filtrage par période (jour, semaine, prochains jours)
        - Personnalisation selon les rôles utilisateur
        - Statistiques et métriques
        """
)
@RestController
@RequestMapping("/api/alertes-journal")
@RequiredArgsConstructor
public class JournalAlerteController {

    private final AlerteJournalService alerteJournalService;
    private final UserService userService;

    @Operation(
            summary = "Récupérer les événements prochains",
            description = """
                Retourne la liste des événements prévus dans les jours à venir.
                
                Types d'événements inclus :
                - Rendez-vous professionnels
                - Réunions d'équipe
                - Échéances de projets
                - Rappels administratifs
                - Formations planifiées
                
                Fonctionnalités :
                - Filtrage par nombre de jours personnalisable
                - Tri chronologique automatique
                - Filtrage selon les permissions utilisateur
                - Informations contextuelles complètes
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des événements prochains récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des événements prochains récupérée avec succès",
                      "data": {
                        "evenements": [
                          {
                            "id": 1,
                            "titre": "Réunion de coordination RH",
                            "description": "Réunion hebdomadaire de coordination des équipes RH",
                            "typeEvenement": "REUNION",
                            "dateDebut": "2024-03-18T10:00:00",
                            "dateFin": "2024-03-18T11:30:00",
                            "lieu": "Salle de conférence A",
                            "organisateur": "Responsable RH",
                            "participants": ["rh1", "rh2", "rh3"],
                            "priorite": "MOYENNE",
                            "statut": "CONFIRME",
                            "urlDocumentation": "/docs/reunions/rh-2024-03-18",
                            "rappelActif": true,
                            "heureRappel": "09:45"
                          },
                          {
                            "id": 2,
                            "titre": "Échéance rapport mensuel",
                            "description": "Date limite de soumission du rapport d'activité mensuel",
                            "typeEvenement": "ECHEANCE",
                            "dateEcheance": "2024-03-20T17:00:00",
                            "projet": "Rapport Mensuel RH",
                            "responsable": "Chef de service RH",
                            "priorite": "HAUTE",
                            "statut": "EN_COURS",
                            "joursRestants": 2,
                            "urlDocument": "/templates/rapport-mensuel.docx"
                          },
                          {
                            "id": 3,
                            "titre": "Formation RGPD",
                            "description": "Session de formation sur la protection des données personnelles",
                            "typeEvenement": "FORMATION",
                            "dateDebut": "2024-03-22T09:00:00",
                            "dateFin": "2024-03-22T12:30:00",
                            "formateur": "Expert RGPD",
                            "lieu": "Salle de formation B",
                            "inscrits": 15,
                            "placesRestantes": 5,
                            "urlInscription": "/formations/rgpd/inscription"
                          }
                        ],
                        "total": 3,
                        "joursRecherche": 7
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
    @GetMapping("/prochains")
    public ResponseEntity<ApiResponse<?>> getEvenementsProchains(
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

            List<JournalAlerteDTO> alertes = alerteJournalService.getEvenementsProchains(currentUser, jours);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("evenements", alertes);
            responseData.put("total", alertes.size());
            responseData.put("joursRecherche", jours);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des événements prochains récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des événements: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les événements d'aujourd'hui",
            description = """
                Retourne la liste des événements prévus pour la journée en cours.
                
                Focus quotidien :
                - Agenda du jour
                - Réunions immédiates
                - Tâches prioritaires
                - Rappels urgents
                
                Fonctionnalités :
                - Vue concentrée sur l'immédiat
                - Filtrage par créneau horaire
                - Indicateurs de participation
                - Lien avec le calendrier
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des événements d'aujourd'hui récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des événements d'aujourd'hui récupérée avec succès",
                      "data": {
                        "evenements": [
                          {
                            "id": 4,
                            "titre": "Daily stand-up",
                            "description": "Réunion quotidienne de suivi des activités",
                            "typeEvenement": "REUNION",
                            "dateDebut": "2024-03-15T09:00:00",
                            "dateFin": "2024-03-15T09:15:00",
                            "lieu": "Salle de réunion quotidienne",
                            "format": "HYBRIDE",
                            "participantsConfirmes": 8,
                            "priorite": "HAUTE",
                            "ordreDuJour": [
                              "Tour de table",
                              "Points bloquants",
                              "Planification journée"
                            ]
                          },
                          {
                            "id": 5,
                            "titre": "Revue de candidatures",
                            "description": "Analyse des candidatures reçues cette semaine",
                            "typeEvenement": "TACHE",
                            "dateDebut": "2024-03-15T14:00:00",
                            "dateFin": "2024-03-15T16:00:00",
                            "responsable": "Recruteur Senior",
                            "nbCandidatures": 24,
                            "priorite": "MOYENNE",
                            "statut": "EN_ATTENTE"
                          }
                        ],
                        "total": 2,
                        "date": "2024-03-15",
                        "reunions": 1,
                        "taches": 1,
                        "urgent": 1
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
    @GetMapping("/aujourdhui")
    public ResponseEntity<ApiResponse<?>> getEvenementsAujourdhui() {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<JournalAlerteDTO> alertes = alerteJournalService.getEvenementsAujourdhui(currentUser);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("evenements", alertes);
            responseData.put("total", alertes.size());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des événements d'aujourd'hui récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des événements: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les événements de la semaine",
            description = """
                Retourne la liste des événements prévus pour la semaine en cours.
                
                Vue hebdomadaire :
                - Planification sur 7 jours
                - Événements récurrents
                - Tâches à moyen terme
                - Préparation des réunions importantes
                
                Fonctionnalités :
                - Regroupement par jour
                - Vue calendaire
                - Indicateurs de charge
                - Prévision des ressources
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des événements de la semaine récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des événements de la semaine récupérée avec succès",
                      "data": {
                        "evenements": [
                          {
                            "id": 6,
                            "titre": "Comité de direction",
                            "description": "Réunion mensuelle du comité de direction",
                            "typeEvenement": "REUNION",
                            "dateDebut": "2024-03-18T14:00:00",
                            "dateFin": "2024-03-18T16:30:00",
                            "lieu": "Salle du conseil",
                            "organisateur": "Directeur Général",
                            "participantsObligatoires": true,
                            "priorite": "TRES_HAUTE",
                            "documents": [
                              {
                                "nom": "Ordre du jour",
                                "url": "/docs/comite/ordre-du-jour-mars.pdf"
                              },
                              {
                                "nom": "Compte-rendu précédent",
                                "url": "/docs/comite/cr-fevrier.pdf"
                              }
                            ]
                          },
                          {
                            "id": 7,
                            "titre": "Atelier innovation",
                            "description": "Atelier collaboratif sur l'innovation RH",
                            "typeEvenement": "ATELIER",
                            "dateDebut": "2024-03-19T09:00:00",
                            "dateFin": "2024-03-19T17:00:00",
                            "animateur": "Consultant Innovation",
                            "lieu": "Espace créatif",
                            "inscrits": 12,
                            "capaciteMax": 20,
                            "themes": [
                              "Digitalisation RH",
                              "Expérience collaborateur",
                              "Outils innovants"
                            ]
                          }
                        ],
                        "total": 2,
                        "semaine": "Semaine 12 (18-24 mars 2024)",
                        "parJour": {
                          "lundi": 1,
                          "mardi": 1,
                          "mercredi": 0,
                          "jeudi": 0,
                          "vendredi": 0
                        },
                        "chargeSemaine": "MOYENNE"
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
    @GetMapping("/cette-semaine")
    public ResponseEntity<ApiResponse<?>> getEvenementsCetteSemaine() {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<JournalAlerteDTO> alertes = alerteJournalService.getEvenementsCetteSemaine(currentUser);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("evenements", alertes);
            responseData.put("total", alertes.size());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des événements de la semaine récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des événements: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Déclencher une vérification manuelle des alertes",
            description = """
                Déclenche immédiatement une vérification manuelle de toutes les alertes du journal.
                
                Fonctionnalités :
                - Actualisation en temps réel des événements
                - Génération de nouvelles alertes si nécessaire
                - Nettoyage des événements obsolètes
                - Mise à jour des statuts
                
                Utilisation typique :
                - Après ajout d'événements
                - Synchronisation avec calendriers externes
                - Tests et débogage
                - Maintenance du système
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Vérification des alertes journal lancée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Vérification des alertes journal lancée avec succès",
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
    @PostMapping("/verifier-maintenant")
    public ResponseEntity<ApiResponse<?>> verifierAlertesMaintenant() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            alerteJournalService.verifierAlertesJournal(currentUser);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Vérification des alertes journal lancée avec succès", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la vérification des alertes: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les statistiques des événements",
            description = """
                Retourne des statistiques détaillées sur les événements et alertes.
                
                Métriques incluses :
                - Distribution par type d'événement
                - Évolution dans le temps
                - Taux de participation
                - Charge de travail prévisionnelle
                - Tendance des annulations
                
                Utilisation typique :
                - Analyse de l'activité
                - Optimisation des plannings
                - Prévision des ressources
                - Rapports de performance
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Statistiques des événements récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Statistiques des événements récupérées avec succès",
                      "data": {
                        "periodeAnalyse": "7 jours",
                        "totalEvenements": 15,
                        "distributionType": {
                          "REUNION": 8,
                          "TACHE": 4,
                          "FORMATION": 2,
                          "ATELIER": 1
                        },
                        "tendance": "+12%",
                        "tauxParticipationMoyen": 78.5,
                        "chargeTravail": {
                          "lundi": 4.5,
                          "mardi": 6.0,
                          "mercredi": 3.5,
                          "jeudi": 5.0,
                          "vendredi": 2.0
                        },
                        "annulations7Jours": 2,
                          "tauxAnnulation": 13.3,
                        "prochains7Jours": {
                          "evenements": 7,
                          "reunions": 4,
                          "formations": 2,
                          "urgent": 1
                        },
                        "recommandations": [
                          "Équilibrer les réunions entre début et fin de semaine",
                          "Prévoir plus de créneaux pour les formations",
                          "Réduire les annulations par une meilleure confirmation"
                        ]
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
    public ResponseEntity<ApiResponse<?>> getStatistiques(
            @Parameter(
                    description = "Nombre de jours pour l'analyse (défaut: 7)",
                    example = "7"
            )
            @RequestParam(defaultValue = "7") int jours) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            AlerteJournalService.JournalStatistiquesDTO statistiques =
                    alerteJournalService.getStatistiques(currentUser, jours);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Statistiques des événements récupérées avec succès", statistiques));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des statistiques: " + e.getMessage(), null));
        }
    }
}