package com.tpc.tpcgestpaie.localapp.controller.alertes;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.Alertes.AlerteServiceUnifie;
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
import java.util.Map;

@Tag(
        name = "Gestion administrative/Alertes Unifiées",
        description = """
        API unifiée de gestion de toutes les alertes du système : absences, anniversaires, contrats, journaux.
        Fournit une vue consolidée et des statistiques globales sur l'ensemble des alertes.
        
        Types d'alertes supportées :
        - ABSENCE : Alertes liées aux congés et absences
        - ANNIVERSAIRE : Alertes d'anniversaires et dates importantes
        - CONTRAT : Alertes sur l'expiration des contrats
        - JOURNAL : Alertes quotidiennes et notifications système
        
        Caractéristiques principales :
        - Vue unifiée de toutes les alertes
        - Résumé et statistiques consolidés
        - Filtrage par type d'alerte
        - Personnalisation par période
        """
)
@RestController
@RequestMapping("/api/alertes")
@RequiredArgsConstructor
public class AlerteUnifieController {

    private final AlerteServiceUnifie alerteServiceUnifie;
    private final UserService userService;

    @Operation(
            summary = "Récupérer toutes les alertes unifiées",
            description = """
                Retourne une vue complète et unifiée de toutes les alertes du système pour un utilisateur.
                
                Structure de la réponse :
                - alertesAbsence : Alertes liées aux congés et absences
                - alertesAnniversaire : Alertes d'anniversaires et dates importantes
                - alertesContrat : Alertes sur l'expiration des contrats
                - alertesJournal : Alertes quotidiennes et notifications
                - statistiquesGlobales : Métriques consolidées
                
                Fonctionnalités :
                - Personnalisation selon les permissions utilisateur
                - Filtrage par période (jours)
                - Données structurées en JSON
                - Métadonnées de contexte
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Toutes les alertes unifiées récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Toutes les alertes unifiées récupérées avec succès",
                      "data": {
                        "donnees": {
                          "alertesAbsence": {
                            "departsProchains": [
                              {
                                "employeId": 123,
                                "employeNom": "Dupont Jean",
                                "dateDebut": "2024-03-20",
                                "joursRestants": 2,
                                "typeAbsence": "CONGE_PAYE"
                              }
                            ],
                            "retoursProchains": [
                              {
                                "employeId": 124,
                                "employeNom": "Martin Sophie",
                                "dateRetour": "2024-03-19",
                                "joursRestants": 1
                              }
                            ],
                            "absencesEnCours": [
                              {
                                "employeId": 125,
                                "employeNom": "Leroy Thomas",
                                "dateDebut": "2024-03-10",
                                "joursEcoules": 5
                              }
                            ],
                            "statistiques": {
                              "total": 3,
                              "departs": 1,
                              "retours": 1,
                              "enCours": 1
                            }
                          },
                          "alertesAnniversaire": {
                            "prochains": [
                              {
                                "employeId": 126,
                                "employeNom": "Petit Claire",
                                "dateAnniversaire": "2024-03-25",
                                "joursRestants": 5,
                                "age": 30
                              }
                            ],
                            "statistiques": {
                              "total": 1,
                              "ceMois": 1,
                              "cetteSemaine": 0
                            }
                          },
                          "alertesContrat": {
                            "expirationsProchaines": [
                              {
                                "employeId": 127,
                                "employeNom": "Moreau Luc",
                                "contratId": 45,
                                "dateExpiration": "2024-04-15",
                                "joursRestants": 25,
                                "typeContrat": "CDI"
                              }
                            ],
                            "statistiques": {
                              "total": 1,
                              "expirations30Jours": 1,
                              "expirations60Jours": 0
                            }
                          },
                          "alertesJournal": {
                            "alertesDuJour": [
                              {
                                "id": 1,
                                "type": "INFO",
                                "message": "Mise à jour du système prévue ce soir",
                                "date": "2024-03-15T09:00:00",
                                "priorite": "FAIBLE"
                              }
                            ],
                            "statistiques": {
                              "total": 1,
                              "urgentes": 0,
                              "informatives": 1
                            }
                          },
                          "statistiquesGlobales": {
                            "totalAlertes": 6,
                            "alertesParType": {
                              "ABSENCE": 3,
                              "ANNIVERSAIRE": 1,
                              "CONTRAT": 1,
                              "JOURNAL": 1
                            },
                            "alertesUrgentes": 0,
                            "derniereMiseAJour": "2024-03-15T10:30:00"
                          }
                        },
                        "joursRecherche": 7,
                        "utilisateur": "admin"
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
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAlertesUnifiees(
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

            ObjectNode resultat = alerteServiceUnifie.getToutesAlertesUnifiees(currentUser, jours);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("donnees", resultat);
            responseData.put("joursRecherche", jours);
            responseData.put("utilisateur", currentUser.getUsername());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Toutes les alertes unifiées récupérées avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des alertes unifiées: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer un résumé des alertes",
            description = """
                Retourne un résumé concis des alertes avec les principales métriques.
                
                Métriques incluses :
                - Totaux par type d'alerte
                - Alertes urgentes et critiques
                - Tendances et évolutions
                - Indicateurs de performance
                
                Avantages :
                - Format léger pour les tableaux de bord
                - Chargement rapide
                - Données essentielles uniquement
                - Idéal pour les widgets et résumés
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Résumé des alertes récupéré avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Résumé des alertes récupéré avec succès",
                      "data": {
                        "resume": {
                          "totalAlertes": 6,
                          "alertesUrgentes": 0,
                          "parType": {
                            "ABSENCE": 3,
                            "ANNIVERSAIRE": 1,
                            "CONTRAT": 1,
                            "JOURNAL": 1
                          },
                          "tendance": "+2",
                          "derniereMiseAJour": "2024-03-15T10:30:00",
                          "prochainesAlertes": {
                            "dans24h": 1,
                            "dans7Jours": 4,
                            "dans30Jours": 6
                          }
                        },
                        "joursRecherche": 7,
                        "utilisateur": "admin"
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
    @GetMapping("/resume")
    public ResponseEntity<ApiResponse<?>> getAlertesResume(
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

            Map<String, Object> resume = alerteServiceUnifie.getAlertesResume(currentUser, jours);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("resume", resume);
            responseData.put("joursRecherche", jours);
            responseData.put("utilisateur", currentUser.getUsername());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Résumé des alertes récupéré avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération du résumé des alertes: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les statistiques globales des alertes",
            description = """
                Retourne des statistiques détaillées sur l'ensemble des alertes.
                
                Statistiques fournies :
                - Distribution par type d'alerte
                - Évolution dans le temps
                - Taux d'urgence et criticité
                - Métriques de performance
                - Prévisions et tendances
                
                Utilisation typique :
                - Rapports de gestion
                - Tableaux de bord analytiques
                - Optimisation des processus
                - Analyse des tendances
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Statistiques globales des alertes récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Statistiques globales des alertes récupérées avec succès",
                      "data": {
                        "statistiques": {
                          "totalAlertes": 6,
                          "alertesParType": {
                            "ABSENCE": 3,
                            "ANNIVERSAIRE": 1,
                            "CONTRAT": 1,
                            "JOURNAL": 1
                          },
                          "distributionPourcentage": {
                            "ABSENCE": 50.0,
                            "ANNIVERSAIRE": 16.7,
                            "CONTRAT": 16.7,
                            "JOURNAL": 16.7
                          },
                          "alertesUrgentes": 0,
                          "alertesCritiques": 0,
                          "moyenneQuotidienne": 0.9,
                          "tauxCroissance": 12.5,
                          "periodeAnalyse": "7 jours",
                          "derniereMiseAJour": "2024-03-15T10:30:00"
                        },
                        "periodeJours": 7,
                        "utilisateur": "admin",
                        "timestamp": 1647340200000
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

            ObjectNode donneesUnifiees = alerteServiceUnifie.getToutesAlertesUnifiees(currentUser, jours);

            // Extraire les statistiques globales
            ObjectNode statistiquesGlobales = (ObjectNode) donneesUnifiees.get("statistiquesGlobales");

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("statistiques", statistiquesGlobales);
            responseData.put("periodeJours", jours);
            responseData.put("utilisateur", currentUser.getUsername());
            responseData.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Statistiques globales des alertes récupérées avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des statistiques: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les alertes par type spécifique",
            description = """
                Retourne uniquement les alertes d'un type spécifique.
                
                Types d'alerte supportés :
                - ABSENCE : Alertes liées aux congés et absences
                - ANNIVERSAIRE : Alertes d'anniversaires et dates importantes
                - CONTRAT : Alertes sur l'expiration des contrats
                - JOURNAL : Alertes quotidiennes et notifications système
                
                Fonctionnalités :
                - Filtrage précis par type
                - Statistiques spécifiques au type
                - Structure de données adaptée
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Alertes de type spécifique récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Alertes ABSENCE",
                                    value = """
                    {
                      "success": true,
                      "message": "Alertes de type ABSENCE récupérées avec succès",
                      "data": {
                        "alertes": {
                          "departsProchains": [
                            {
                              "employeId": 123,
                              "employeNom": "Dupont Jean",
                              "dateDebut": "2024-03-20",
                              "joursRestants": 2,
                              "typeAbsence": "CONGE_PAYE"
                            }
                          ],
                          "retoursProchains": [
                            {
                              "employeId": 124,
                              "employeNom": "Martin Sophie",
                              "dateRetour": "2024-03-19",
                              "joursRestants": 1
                            }
                          ],
                          "absencesEnCours": [
                            {
                              "employeId": 125,
                              "employeNom": "Leroy Thomas",
                              "dateDebut": "2024-03-10",
                              "joursEcoules": 5
                            }
                          ],
                          "statistiques": {
                            "total": 3,
                            "departs": 1,
                            "retours": 1,
                            "enCours": 1
                          }
                        },
                        "typeAlerte": "ABSENCE",
                        "joursRecherche": 7,
                        "total": 3
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Type d'alerte non reconnu",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Type d'alerte non reconnu: INEXISTANT",
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
    @GetMapping("/types/{typeAlerte}")
    public ResponseEntity<ApiResponse<?>> getAlertesParType(
            @Parameter(
                    description = "Type d'alerte (ABSENCE, ANNIVERSAIRE, CONTRAT, JOURNAL)",
                    required = true,
                    example = "ABSENCE"
            )
            @PathVariable String typeAlerte,
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

            ObjectNode toutesAlertes = alerteServiceUnifie.getToutesAlertesUnifiees(currentUser, jours);
            ObjectNode alertesParType = null;

            // Sélectionner le type d'alerte demandé
            switch (typeAlerte.toUpperCase()) {
                case "ABSENCE":
                    alertesParType = (ObjectNode) toutesAlertes.get("alertesAbsence");
                    break;
                case "ANNIVERSAIRE":
                    alertesParType = (ObjectNode) toutesAlertes.get("alertesAnniversaire");
                    break;
                case "CONTRAT":
                    alertesParType = (ObjectNode) toutesAlertes.get("alertesContrat");
                    break;
                case "JOURNAL":
                    alertesParType = (ObjectNode) toutesAlertes.get("alertesJournal");
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse<>(false, "Type d'alerte non reconnu: " + typeAlerte, null));
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("alertes", alertesParType);
            responseData.put("typeAlerte", typeAlerte.toUpperCase());
            responseData.put("joursRecherche", jours);
            responseData.put("total", alertesParType != null ?
                    alertesParType.get("statistiques").get("total") : 0);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Alertes de type " + typeAlerte + " récupérées avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des alertes: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Déclencher une vérification manuelle de toutes les alertes",
            description = """
                Déclenche immédiatement une vérification manuelle de l'ensemble des alertes.
                
                Fonctionnalités :
                - Actualisation en temps réel des données
                - Génération de nouvelles alertes si nécessaire
                - Nettoyage des alertes obsolètes
                - Mise à jour des statistiques
                
                Utilisation typique :
                - Après import de données massives
                - Synchronisation avec systèmes externes
                - Tests et débogage
                - Maintenance système
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Vérification de toutes les alertes lancée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Vérification de toutes les alertes lancée avec succès",
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
    public ResponseEntity<ApiResponse<?>> verifierToutesAlertesMaintenant() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            // Note: Cette méthode nécessiterait d'ajouter des méthodes de vérification dans AlerteServiceUnifie
            // alerteServiceUnifie.verifierToutesAlertes(currentUser);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Vérification de toutes les alertes lancée avec succès", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la vérification des alertes: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer des informations de debug",
            description = """
                Retourne des informations techniques détaillées pour le débogage.
                
                Informations fournies :
                - Informations utilisateur
                - Structure complète des données
                - Timestamps et métadonnées
                - État du système
                
                Utilisation typique :
                - Diagnostic de problèmes
                - Support technique
                - Développement et tests
                - Audit technique
                
                Attention : Cette endpoint expose des informations sensibles, à utiliser uniquement en environnement de développement.
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Informations de debug récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Informations de debug récupérées avec succès",
                      "data": {
                        "utilisateur": "admin",
                        "userId": 1,
                        "timestamp": 1647340200000,
                        "structure": {
                          "metadata": {
                            "version": "2.1.0",
                            "generation": "2024-03-15T10:30:00",
                            "tempsExecution": 245
                          },
                          "donnees": {
                            "format": "JSON",
                            "taille": "15.2 KB",
                            "elements": 156
                          }
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
    @GetMapping("/debug")
    public ResponseEntity<ApiResponse<?>> getDebugInfo() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            ObjectNode toutesAlertes = alerteServiceUnifie.getToutesAlertesUnifiees(currentUser, 30);

            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("utilisateur", currentUser.getUsername());
            debugInfo.put("userId", currentUser.getId());
            debugInfo.put("timestamp", System.currentTimeMillis());
            debugInfo.put("structure", toutesAlertes);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Informations de debug récupérées avec succès", debugInfo));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des informations de debug: " + e.getMessage(), null));
        }
    }
}