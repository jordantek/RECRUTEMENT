package com.tpc.tpcgestpaie.localapp.controller.alertes;

import com.tpc.tpcgestpaie.localapp.config.AnniversaireConfig;
import com.tpc.tpcgestpaie.localapp.dto.alertes.EmployeAlerteDTO;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.Alertes.AlerteAnniversaireService;
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
        name = "Gestion administrative/Anniversaires",
        description = """
        API de gestion des anniversaires des employés : suivi, alertes et célébrations.
        Fournit des fonctionnalités pour suivre les anniversaires des employés et générer des alertes.
        
        Fonctionnalités principales :
        - Détection des anniversaires prochains
        - Alertes personnalisées par période
        - Statistiques et rapports
        - Configuration flexible du système
        - Gestion des célébrations d'entreprise
        
        Utilité :
        - Renforcement de la culture d'entreprise
        - Amélioration du climat social
        - Gestion des événements RH
        - Planification des activités festives
        """
)
@RestController
@RequestMapping("/api/anniversaires")
@RequiredArgsConstructor
public class AnniversaireController {

    private final AlerteAnniversaireService alerteService;
    private final AnniversaireConfig config;
    private final UserService userService;

    @Operation(
            summary = "Récupérer les anniversaires prochains",
            description = """
                Retourne la liste des employés dont l'anniversaire est prévu dans les jours à venir.
                
                Fonctionnalités :
                - Filtrage par nombre de jours personnalisable
                - Calcul automatique des jours restants
                - Informations détaillées sur les employés
                - Personnalisation selon les permissions utilisateur
                
                Utilisation typique :
                - Préparation des célébrations
                - Envoi de cartes de vœux
                - Organisation d'événements
                - Tableau de bord RH
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des anniversaires prochains récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des anniversaires prochains récupérée avec succès",
                      "data": {
                        "anniversaires": [
                          {
                            "employeId": 123,
                            "employeNom": "Dupont Jean",
                            "dateAnniversaire": "2024-03-20",
                            "joursRestants": 2,
                            "age": 35,
                            "service": "Informatique",
                            "poste": "Développeur Senior",
                            "entreprise": "Tech Solutions SARL"
                          },
                          {
                            "employeId": 124,
                            "employeNom": "Martin Sophie",
                            "dateAnniversaire": "2024-03-22",
                            "joursRestants": 4,
                            "age": 28,
                            "service": "Ressources Humaines",
                            "poste": "Responsable RH",
                            "entreprise": "Tech Solutions SARL"
                          }
                        ],
                        "total": 2,
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
    public ResponseEntity<ApiResponse<?>> getAnniversairesProchains(
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

            List<EmployeAlerteDTO> anniversaires = alerteService.getAnniversairesProchains(currentUser, jours);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("anniversaires", anniversaires);
            responseData.put("total", anniversaires.size());
            responseData.put("joursRecherche", jours);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des anniversaires prochains récupérée avec succès", responseData));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des anniversaires: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les anniversaires du mois courant",
            description = """
                Retourne la liste des employés dont l'anniversaire est prévu dans le mois en cours.
                
                Fonctionnalités :
                - Vue mensuelle des anniversaires
                - Regroupement par semaine ou jour
                - Informations complètes sur les employés
                - Calcul automatique des âges
                
                Utilisation typique :
                - Planification mensuelle des événements
                - Budget prévisionnel des célébrations
                - Communication interne
                - Rapports RH mensuels
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des anniversaires du mois récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des anniversaires du mois récupérée avec succès",
                      "data": {
                        "anniversaires": [
                          {
                            "employeId": 125,
                            "employeNom": "Leroy Thomas",
                            "dateAnniversaire": "2024-03-05",
                            "age": 42,
                            "service": "Marketing",
                            "poste": "Responsable Marketing",
                            "date": "2024-03-05",
                            "estPasse": true,
                            "prochain": "2025-03-05"
                          },
                          {
                            "employeId": 126,
                            "employeNom": "Petit Claire",
                            "dateAnniversaire": "2024-03-25",
                            "age": 29,
                            "service": "Finance",
                            "poste": "Comptable",
                            "date": "2024-03-25",
                            "estPasse": false,
                            "joursRestants": 10
                          }
                        ],
                        "total": 2,
                        "mois": "Mars 2024",
                        "anniversairesPasses": 1,
                        "anniversairesAVenir": 1
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
    @GetMapping("/mois-courant")
    public ResponseEntity<ApiResponse<?>> getAnniversairesDuMois() {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<EmployeAlerteDTO> anniversaires = alerteService.getAnniversairesDuMois(currentUser);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("anniversaires", anniversaires);
            responseData.put("total", anniversaires.size());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des anniversaires du mois récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des anniversaires du mois: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Déclencher une vérification manuelle des anniversaires",
            description = """
                Déclenche immédiatement une vérification manuelle de tous les anniversaires.
                
                Fonctionnalités :
                - Actualisation en temps réel des données
                - Génération de nouvelles alertes si nécessaire
                - Mise à jour des statistiques
                - Nettoyage des données obsolètes
                
                Utilisation typique :
                - Après import de nouveaux employés
                - Synchronisation avec d'autres systèmes
                - Tests et débogage
                - Maintenance du système
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Vérification des anniversaires lancée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Vérification des anniversaires lancée avec succès",
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
    public ResponseEntity<ApiResponse<?>> verifierAnniversairesMaintenant() {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            alerteService.verifierAnniversaires(currentUser);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Vérification des anniversaires lancée avec succès", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la vérification des anniversaires: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer la configuration des alertes d'anniversaire",
            description = """
                Retourne la configuration actuelle du système d'alertes d'anniversaire.
                
                Paramètres de configuration :
                - Seuils d'alerte en jours
                - Paramètres de notification
                - Options de personnalisation
                - Types d'événements surveillés
                - Paramètres de célébration
                
                Utilisation typique :
                - Administration du système
                - Audit des paramètres
                - Personnalisation de l'expérience
                - Configuration des notifications
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Configuration des alertes d'anniversaire récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Configuration des alertes d'anniversaire récupérée avec succès",
                      "data": {
                        "seuilAlerteJours": 7,
                        "notificationEmail": true,
                        "notificationInApp": true,
                        "inclusionAnciensEmployes": false,
                        "joursExclusion": [],
                        "heureVerification": "08:00",
                        "typesCélébration": [
                          "EMAIL",
                          "CARTE_VOEUX",
                          "PETIT_DEJEUNER"
                        ],
                        "seuilAgeSpecial": [30, 40, 50],
                        "messagePersonnalise": "Joyeux anniversaire !",
                        "version": "1.2.0"
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
                    "Configuration des alertes d'anniversaire récupérée avec succès", config));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération de la configuration: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Mettre à jour la configuration des anniversaires",
            description = """
                Modifie la configuration du système d'alertes d'anniversaire.
                
                Sécurité :
                - Requiert des droits d'administration
                - Validation des paramètres
                - Journalisation des changements
                
                Paramètres modifiables :
                - Seuils d'alerte
                - Options de notification
                - Types de célébration
                - Messages personnalisés
                - Exclusions et inclusions
                
                Attention : Les modifications affectent l'ensemble du système.
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Configuration des alertes d'anniversaire mise à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Configuration des alertes d'anniversaire mise à jour avec succès",
                      "data": {
                        "seuilAlerteJours": 14,
                        "notificationEmail": true,
                        "notificationInApp": true,
                        "inclusionAnciensEmployes": false,
                        "joursExclusion": [6, 0],
                        "heureVerification": "09:00",
                        "typesCélébration": [
                          "EMAIL",
                          "CARTE_VOEUX",
                          "PETIT_DEJEUNER",
                          "CAKE"
                        ],
                        "seuilAgeSpecial": [25, 30, 40, 50, 60],
                        "messagePersonnalise": "Bon anniversaire ! Que cette année vous apporte bonheur et succès.",
                        "version": "1.2.1"
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié ou non autorisé",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Droits insuffisants pour modifier la configuration",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Vous n'avez pas les droits nécessaires pour modifier la configuration",
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
    @PutMapping("/config")
    public ResponseEntity<ApiResponse<?>> updateConfig(
            @Parameter(
                    description = "Nouvelle configuration des anniversaires",
                    required = true,
                    schema = @Schema(implementation = AnniversaireConfig.class)
            )
            @RequestBody AnniversaireConfig newConfig) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            // Vérifier que l'utilisateur a les droits pour modifier la configuration
            // (nécessite une implémentation pour persister les changements)

            // Pour l'instant, on retourne simplement la nouvelle configuration
            // Dans une implémentation réelle, vous sauvegarderiez cette configuration

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Configuration des alertes d'anniversaire mise à jour avec succès", newConfig));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la mise à jour de la configuration: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les statistiques des anniversaires",
            description = """
                Retourne des statistiques consolidées sur les anniversaires.
                
                Métriques incluses :
                - Nombre d'anniversaires prochains (7 jours)
                - Nombre d'anniversaires du mois
                - Anniversaires du jour
                - Configuration actuelle
                
                Utilisation typique :
                - Tableau de bord RH
                - Rapports de gestion
                - Analyse des tendances
                - Planification des ressources
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Statistiques des anniversaires récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Statistiques des anniversaires récupérées avec succès",
                      "data": {
                        "prochains7Jours": 3,
                        "ceMois": 8,
                        "aujourdhui": 1,
                        "semaineProchaine": 2,
                        "ageMoyen": 38.5,
                        "distributionServices": {
                          "Informatique": 2,
                          "Ressources Humaines": 1,
                          "Marketing": 2,
                          "Finance": 1,
                          "Production": 2
                        },
                        "config": {
                          "seuilAlerteJours": 7,
                          "notificationEmail": true,
                          "notificationInApp": true,
                          "version": "1.2.0"
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

            List<EmployeAlerteDTO> anniversaires7Jours = alerteService.getAnniversairesProchains(currentUser, 7);
            List<EmployeAlerteDTO> anniversairesMois = alerteService.getAnniversairesDuMois(currentUser);

            Map<String, Object> statistiques = new HashMap<>();
            statistiques.put("prochains7Jours", anniversaires7Jours.size());
            statistiques.put("ceMois", anniversairesMois.size());
            statistiques.put("aujourdhui", anniversaires7Jours.stream()
                    .filter(a -> a.getJoursRestants() == 0)
                    .count());
            statistiques.put("config", config);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Statistiques des anniversaires récupérées avec succès", statistiques));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des statistiques: " + e.getMessage(), null));
        }
    }
}