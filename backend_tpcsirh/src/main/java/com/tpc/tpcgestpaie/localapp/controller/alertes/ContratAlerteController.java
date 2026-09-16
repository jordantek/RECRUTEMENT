package com.tpc.tpcgestpaie.localapp.controller.alertes;

import com.tpc.tpcgestpaie.localapp.config.ContratConfig;
import com.tpc.tpcgestpaie.localapp.dto.alertes.ContratAlerteDTO;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.Alertes.AlerteContratService;
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
        name = "Gestion administrative/Alertes Contrats",
        description = """
        API de gestion des alertes liées aux contrats de travail : suivi des échéances, périodes d'essai et anniversaires de recrutement.
        Système d'alerte préventif pour la gestion proactive des contrats.
        
        Types d'alertes supportées :
        - FIN_ESSAI : Fin de période d'essai prochaine
        - FIN_CONTRAT : Échéance de contrat à venir
        - ANNIVERSAIRE_RECRUTEMENT : Anniversaire d'embauche
        
        Fonctionnalités principales :
        - Détection précoce des échéances
        - Alertes personnalisables par période
        - Statistiques de gestion contractuelle
        - Configuration flexible des seuils
        """
)
@RestController
@RequestMapping("/api/alertes-contrats")
public class ContratAlerteController {

    private final AlerteContratService alerteContratService;
    private final ContratConfig contratConfig;
    private final UserService userService;

    public ContratAlerteController(AlerteContratService alerteContratService, ContratConfig contratConfig, UserService userService) {
        this.alerteContratService = alerteContratService;
        this.contratConfig = contratConfig;
        this.userService = userService;
    }

    @Operation(
            summary = "Récupérer les fins de période d'essai prochaines",
            description = """
                Retourne la liste des contrats dont la période d'essai se termine dans les jours à venir.
                
                Importance RH :
                - Décision de confirmation ou non du contrat
                - Évaluation de la période d'essai
                - Préparation des entretiens de fin d'essai
                - Respect des délais légaux
                
                Fonctionnalités :
                - Filtrage par nombre de jours personnalisable
                - Informations détaillées sur le contrat et l'employé
                - Calcul automatique des jours restants
                - Personnalisation selon les permissions
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des fins d'essai prochaines récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des fins d'essai prochaines récupérée avec succès",
                      "data": {
                        "alertes": [
                          {
                            "contratId": 45,
                            "employeId": 123,
                            "employeNom": "Dupont Jean",
                            "typeContrat": "CDI",
                            "dateDebutEssai": "2024-01-15",
                            "dateFinEssai": "2024-03-15",
                            "joursRestants": 2,
                            "statutEssai": "EN_COURS",
                            "service": "Informatique",
                            "poste": "Développeur Junior",
                            "evaluateur": "Responsable Informatique",
                            "actionsRecommandees": [
                              "Planifier entretien d'évaluation",
                              "Préparer fiche d'évaluation",
                              "Décider confirmation ou non"
                            ]
                          },
                          {
                            "contratId": 46,
                            "employeId": 124,
                            "employeNom": "Martin Sophie",
                            "typeContrat": "CDD",
                            "dateDebutEssai": "2024-02-01",
                            "dateFinEssai": "2024-03-17",
                            "joursRestants": 4,
                            "statutEssai": "EN_COURS",
                            "service": "Marketing",
                            "poste": "Assistant Marketing",
                            "evaluateur": "Responsable Marketing",
                            "actionsRecommandees": [
                              "Évaluer performance",
                              "Consulter feedback équipe",
                              "Préparer décision finale"
                            ]
                          }
                        ],
                        "total": 2,
                        "joursRecherche": 15,
                        "type": "FIN_ESSAI"
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
    @GetMapping("/fins-essai")
    public ResponseEntity<ApiResponse<?>> getFinsEssaiProchaines(
            @Parameter(
                    description = "Nombre de jours pour la recherche (défaut: 15)",
                    example = "15"
            )
            @RequestParam(defaultValue = "15") int jours) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<ContratAlerteDTO> alertes = alerteContratService.getFinsEssaiProchaines(currentUser, jours);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("alertes", alertes);
            responseData.put("total", alertes.size());
            responseData.put("joursRecherche", jours);
            responseData.put("type", "FIN_ESSAI");

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des fins d'essai prochaines récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des fins d'essai: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les fins de contrat prochaines",
            description = """
                Retourne la liste des contrats dont l'échéance est prévue dans les jours à venir.
                
                Importance RH :
                - Renouvellement ou non des contrats
                - Préparation des négociations
                - Respect des délais de préavis
                - Gestion des transitions
                
                Fonctionnalités :
                - Alertes pour CDD et contrats à durée déterminée
                - Inclut les contrats à renouveler
                - Informations sur les conditions de renouvellement
                - Actions recommandées par type de contrat
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des fins de contrat prochaines récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des fins de contrat prochaines récupérée avec succès",
                      "data": {
                        "alertes": [
                          {
                            "contratId": 47,
                            "employeId": 125,
                            "employeNom": "Leroy Thomas",
                            "typeContrat": "CDD",
                            "dateDebut": "2023-09-01",
                            "dateFin": "2024-04-01",
                            "joursRestants": 25,
                            "dureeTotale": "7 mois",
                            "renouvelable": true,
                            "conditionsRenouvellement": "Sous réserve de performance",
                            "service": "Production",
                            "poste": "Opérateur",
                            "priorite": "MOYENNE",
                            "actionsRecommandees": [
                              "Évaluer performance",
                              "Décider renouvellement",
                              "Préparer nouveau contrat si nécessaire"
                            ]
                          }
                        ],
                        "total": 1,
                        "joursRecherche": 30,
                        "type": "FIN_CONTRAT"
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
    @GetMapping("/fins-contrat")
    public ResponseEntity<ApiResponse<?>> getFinsContratProchaines(
            @Parameter(
                    description = "Nombre de jours pour la recherche (défaut: 30)",
                    example = "30"
            )
            @RequestParam(defaultValue = "30") int jours) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<ContratAlerteDTO> alertes = alerteContratService.getFinsContratProchaines(currentUser, jours);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("alertes", alertes);
            responseData.put("total", alertes.size());
            responseData.put("joursRecherche", jours);
            responseData.put("type", "FIN_CONTRAT");

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des fins de contrat prochaines récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des fins de contrat: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les anniversaires de recrutement prochains",
            description = """
                Retourne la liste des employés dont l'anniversaire d'embauche est prévu dans les jours à venir.
                
                Importance RH :
                - Reconnaissance de l'ancienneté
                - Célébration des carrières
                - Analyse de la rétention
                - Planification des évolutions
                
                Fonctionnalités :
                - Alertes pour les anniversaires clés (1 an, 3 ans, 5 ans, etc.)
                - Calcul automatique de l'ancienneté
                - Suggestions de reconnaissance
                - Analyse des parcours professionnels
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des anniversaires de recrutement prochains récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des anniversaires de recrutement prochains récupérée avec succès",
                      "data": {
                        "alertes": [
                          {
                            "employeId": 126,
                            "employeNom": "Petit Claire",
                            "dateEmbauche": "2023-03-25",
                            "anniversaire": "2024-03-25",
                            "joursRestants": 10,
                            "anciennete": "1 an",
                            "typeAnniversaire": "1_AN",
                            "service": "Finance",
                            "poste": "Comptable",
                            "evolutions": [
                              "Prime d'ancienneté applicable",
                              "Révision salariale possible",
                              "Entretien de carrière recommandé"
                            ],
                            "suggestionsCelebration": [
                              "Carte de remerciement",
                              "Petit cadeau entreprise",
                              "Announcement en réunion d'équipe"
                            ]
                          },
                          {
                            "employeId": 127,
                            "employeNom": "Moreau Luc",
                            "dateEmbauche": "2019-04-15",
                            "anniversaire": "2024-04-15",
                            "joursRestants": 30,
                            "anciennete": "5 ans",
                            "typeAnniversaire": "5_ANS",
                            "service": "Production",
                            "poste": "Chef d'équipe",
                            "evolutions": [
                              "Prime d'ancienneté majorée",
                              "Possibilité de promotion",
                              "Formation leadership recommandée"
                            ],
                            "suggestionsCelebration": [
                              "Cadeau significatif",
                              "Repas d'équipe",
                              "Reconnaissance publique"
                            ]
                          }
                        ],
                        "total": 2,
                        "joursRecherche": 30,
                        "type": "ANNIVERSAIRE_RECRUTEMENT"
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
    @GetMapping("/anniversaires-recrutement")
    public ResponseEntity<ApiResponse<?>> getAnniversairesRecrutement(
            @Parameter(
                    description = "Nombre de jours pour la recherche (défaut: 30)",
                    example = "30"
            )
            @RequestParam(defaultValue = "30") int jours) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }

            List<ContratAlerteDTO> alertes = alerteContratService.getAnniversairesRecrutementProchains(currentUser, jours);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("alertes", alertes);
            responseData.put("total", alertes.size());
            responseData.put("joursRecherche", jours);
            responseData.put("type", "ANNIVERSAIRE_RECRUTEMENT");

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Liste des anniversaires de recrutement prochains récupérée avec succès", responseData));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des anniversaires de recrutement: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Déclencher une vérification manuelle des alertes de contrat",
            description = """
                Déclenche immédiatement une vérification manuelle de toutes les alertes liées aux contrats.
                
                Fonctionnalités :
                - Actualisation en temps réel des données
                - Génération de nouvelles alertes si nécessaire
                - Nettoyage des alertes obsolètes
                - Mise à jour des statistiques
                
                Utilisation typique :
                - Après signature de nouveaux contrats
                - Synchronisation avec le système RH
                - Tests et débogage
                - Maintenance du système
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Vérification des alertes contrat lancée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Vérification des alertes contrat lancée avec succès",
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

            alerteContratService.verifierAlertesContrats(currentUser);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Vérification des alertes contrat lancée avec succès", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la vérification des alertes: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer les statistiques des alertes de contrat",
            description = """
                Retourne des statistiques consolidées sur les alertes liées aux contrats.
                
                Métriques incluses :
                - Nombre de fins d'essai prochaines (15 jours)
                - Nombre de fins de contrat prochaines (30 jours)
                - Nombre d'anniversaires de recrutement (30 jours)
                - Configuration actuelle du système
                
                Utilisation typique :
                - Tableau de bord RH
                - Rapports de gestion contractuelle
                - Analyse des tendances d'embauche
                - Planification des ressources humaines
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Statistiques des alertes contrat récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Statistiques des alertes contrat récupérées avec succès",
                      "data": {
                        "finsEssaiProchaines": 2,
                        "finsContratProchaines": 1,
                        "anniversairesRecrutement": 2,
                        "tauxRenouvellement": 75.0,
                        "moyenneAnciennete": "2.5 ans",
                        "distributionTypesContrat": {
                          "CDI": 45,
                          "CDD": 15,
                          "INTERIM": 5,
                          "APPRENTISSAGE": 3
                        },
                        "config": {
                          "seuilFinEssai": 15,
                          "seuilFinContrat": 30,
                          "seuilAnniversaire": 30,
                          "notificationEmail": true,
                          "notificationInApp": true,
                          "typesContratSurveilles": [
                            "CDI",
                            "CDD",
                            "INTERIM"
                          ],
                          "version": "2.0.0"
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

            List<ContratAlerteDTO> finsEssai = alerteContratService.getFinsEssaiProchaines(currentUser, 15);
            List<ContratAlerteDTO> finsContrat = alerteContratService.getFinsContratProchaines(currentUser, 30);
            List<ContratAlerteDTO> anniversaires = alerteContratService.getAnniversairesRecrutementProchains(currentUser, 30);

            Map<String, Object> statistiques = new HashMap<>();
            statistiques.put("finsEssaiProchaines", finsEssai.size());
            statistiques.put("finsContratProchaines", finsContrat.size());
            statistiques.put("anniversairesRecrutement", anniversaires.size());
            statistiques.put("config", contratConfig);

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Statistiques des alertes contrat récupérées avec succès", statistiques));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération des statistiques: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Récupérer la configuration des alertes de contrat",
            description = """
                Retourne la configuration actuelle du système d'alertes de contrat.
                
                Paramètres de configuration :
                - Seuils d'alerte pour chaque type d'événement
                - Paramètres de notification
                - Types de contrat surveillés
                - Options de personnalisation
                - Paramètres de rappel
                
                Utilisation typique :
                - Administration du système
                - Audit des paramètres
                - Personnalisation des alertes
                - Configuration des notifications
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Configuration des alertes contrat récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Configuration des alertes contrat récupérée avec succès",
                      "data": {
                        "seuilFinEssai": 15,
                        "seuilFinContrat": 30,
                        "seuilAnniversaireRecrutement": 30,
                        "notificationEmail": true,
                        "notificationInApp": true,
                        "rappelsActives": true,
                        "frequenceRappels": "QUOTIDIEN",
                        "typesContratSurveilles": [
                          "CDI",
                          "CDD",
                          "INTERIM",
                          "APPRENTISSAGE"
                        ],
                        "exclusionsServices": [],
                        "joursOuverture": [1, 2, 3, 4, 5],
                        "heureVerification": "08:00",
                        "actionsAutomatiques": [
                          "GENERER_ALERTE",
                          "ENVOYER_NOTIFICATION",
                          "CREER_TACHE_RH"
                        ],
                        "version": "2.0.0"
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
                    "Configuration des alertes contrat récupérée avec succès", contratConfig));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la récupération de la configuration: " + e.getMessage(), null));
        }
    }
}