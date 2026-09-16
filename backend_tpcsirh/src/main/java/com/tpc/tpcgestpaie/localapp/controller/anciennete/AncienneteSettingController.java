package com.tpc.tpcgestpaie.localapp.controller.anciennete;

import com.tpc.tpcgestpaie.localapp.dto.anciennete.AncienneteSettingDTO;
import com.tpc.tpcgestpaie.localapp.service.anciennete.AncienneteSettingService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(
        name = "Paramètres d'Ancienneté",
        description = """
        API de gestion des paramètres de calcul de l'ancienneté.
        Configuration centralisée des règles de calcul de l'ancienneté par entreprise.
        
        Fonctionnalités principales :
        - Configuration de l'écart minimum entre deux calculs d'ancienneté
        - Activation/Désactivation du paiement de l'ancienneté
        - Gestion des paramètres par entreprise
        - Vérification de l'état d'activation
        
        Règles métier :
        - Un seul paramètre par entreprise
        - L'écart minimum est exprimé en mois
        - Les paramètres sont obligatoires pour le calcul de l'ancienneté
        """
)
@RestController
@RequestMapping("/api/settings/anciennete")
@RequiredArgsConstructor
public class AncienneteSettingController {

    private final AncienneteSettingService ancienneteSettingService;

    @Operation(
            summary = "Récupérer tous les paramètres d'ancienneté",
            description = """
                Retourne la liste complète de tous les paramètres d'ancienneté configurés.
                
                Utilisation typique :
                - Vue d'ensemble de toutes les configurations
                - Administration système
                - Audit des paramètres
                - Export des configurations
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des paramètres récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Paramètres d'ancienneté récupérés",
                      "data": [
                        {
                          "id": 1,
                          "companyId": 1001,
                          "companyName": "Entreprise A",
                          "nombreMoisEcart": 6,
                          "payeAnciennete": true,
                          "dateCreation": "2024-01-15T10:30:00",
                          "dateModification": "2024-03-01T14:20:00"
                        },
                        {
                          "id": 2,
                          "companyId": 1002,
                          "companyName": "Entreprise B",
                          "nombreMoisEcart": 12,
                          "payeAnciennete": false,
                          "dateCreation": "2024-02-10T09:15:00",
                          "dateModification": "2024-02-10T09:15:00"
                        }
                      ]
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur interne",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Erreur lors de la récupération des paramètres: [message d'erreur]",
                      "data": null
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<AncienneteSettingDTO>>> getAll() {
        List<AncienneteSettingDTO> settings = ancienneteSettingService.getAll();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Paramètres d'ancienneté récupérés", settings)
        );
    }

    @Operation(
            summary = "Récupérer un paramètre d'ancienneté par ID",
            description = """
                Retourne les détails d'un paramètre d'ancienneté spécifique.
                
                Validation :
                - Vérification de l'existence de l'ID
                - Retourne 404 si non trouvé
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paramètre d'ancienneté trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Paramètre d'ancienneté trouvé",
                      "data": {
                        "id": 1,
                        "companyId": 1001,
                        "companyName": "Entreprise A",
                        "nombreMoisEcart": 6,
                        "payeAnciennete": true,
                        "dateCreation": "2024-01-15T10:30:00",
                        "dateModification": "2024-03-01T14:20:00"
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Paramètre d'ancienneté non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Paramètre d'ancienneté non trouvé",
                      "data": null
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AncienneteSettingDTO>> getById(
            @Parameter(
                    description = "ID du paramètre d'ancienneté",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        Optional<AncienneteSettingDTO> setting = ancienneteSettingService.getById(id);
        return setting.map(dto -> ResponseEntity.ok(
                new ApiResponse<>(true, "Paramètre d'ancienneté trouvé", dto)
        )).orElseGet(() -> new ResponseEntity<>(
                new ApiResponse<>(false, "Paramètre d'ancienneté non trouvé", null),
                HttpStatus.NOT_FOUND
        ));
    }

    @Operation(
            summary = "Récupérer les paramètres d'ancienneté par ID d'entreprise",
            description = """
                Retourne les paramètres d'ancienneté configurés pour une entreprise spécifique.
                
                Règles métier :
                - Une entreprise ne peut avoir qu'un seul paramètre d'ancienneté
                - Les paramètres sont obligatoires pour le calcul de l'ancienneté
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paramètre d'ancienneté de l'entreprise trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Paramètre d'ancienneté de l'entreprise trouvé",
                      "data": {
                        "id": 1,
                        "companyId": 1001,
                        "companyName": "Entreprise A",
                        "nombreMoisEcart": 6,
                        "payeAnciennete": true,
                        "dateCreation": "2024-01-15T10:30:00",
                        "dateModification": "2024-03-01T14:20:00"
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Aucun paramètre d'ancienneté trouvé pour cette entreprise",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Aucun paramètre d'ancienneté trouvé pour cette entreprise",
                      "data": null
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping("/entreprise/{companyId}")
    public ResponseEntity<ApiResponse<AncienneteSettingDTO>> getByCompanyId(
            @Parameter(
                    description = "ID de l'entreprise",
                    example = "1001",
                    required = true
            )
            @PathVariable Long companyId) {
        Optional<AncienneteSettingDTO> setting = ancienneteSettingService.getByCompanyId(companyId);
        return setting.map(dto -> ResponseEntity.ok(
                new ApiResponse<>(true, "Paramètre d'ancienneté de l'entreprise trouvé", dto)
        )).orElseGet(() -> new ResponseEntity<>(
                new ApiResponse<>(false, "Aucun paramètre d'ancienneté trouvé pour cette entreprise", null),
                HttpStatus.NOT_FOUND
        ));
    }

    @Operation(
            summary = "Créer ou mettre à jour des paramètres d'ancienneté",
            description = """
                Crée de nouveaux paramètres d'ancienneté ou met à jour des paramètres existants.
                
                Logique métier :
                - Si l'ID est fourni → mise à jour
                - Si l'ID n'est pas fourni → création
                - Validation des champs obligatoires
                - Vérification des contraintes métier
                
                Règles de validation :
                - L'ID de l'entreprise est obligatoire
                - Le nombre de mois d'écart doit être > 0
                - Le paiement de l'ancienneté est optionnel (défaut: false)
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paramètre d'ancienneté enregistré avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Paramètre d'ancienneté enregistré avec succès",
                      "data": {
                        "id": 1,
                        "companyId": 1001,
                        "companyName": "Entreprise A",
                        "nombreMoisEcart": 6,
                        "payeAnciennete": true,
                        "dateCreation": "2024-01-15T10:30:00",
                        "dateModification": "2024-03-01T14:20:00"
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Erreurs de validation",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Erreurs de validation",
                      "data": [
                        {
                          "field": "companyId",
                          "message": "L'ID de l'entreprise est requis"
                        },
                        {
                          "field": "nombreMoisEcart",
                          "message": "Le nombre de mois d'écart doit être supérieur à 0"
                        }
                      ]
                    }
                    """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<?> createOrUpdate(@RequestBody AncienneteSettingDTO dto) {
        try {
            List<ErrorResponse> errors = new ArrayList<>();

            // Validation
            if (dto.getCompanyId() == null) {
                errors.add(new ErrorResponse("companyId", "L'ID de l'entreprise est requis"));
            }

            if (dto.getNombreMoisEcart() == null || dto.getNombreMoisEcart() <= 0) {
                errors.add(new ErrorResponse("nombreMoisEcart",
                        "Le nombre de mois d'écart doit être supérieur à 0"));
            }

            if (dto.getPayeAnciennete() == null) {
                dto.setPayeAnciennete(false); // Valeur par défaut
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            AncienneteSettingDTO saved = ancienneteSettingService.save(dto);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Paramètre d'ancienneté enregistré avec succès", saved)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de l'enregistrement: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Mettre à jour un paramètre d'ancienneté existant",
            description = """
                Met à jour un paramètre d'ancienneté existant identifié par son ID.
                
                Validation :
                - Vérification de l'existence de l'ID
                - Application des règles de validation
                - Mise à jour de la date de modification
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paramètre d'ancienneté mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Paramètre d'ancienneté mis à jour avec succès",
                      "data": {
                        "id": 1,
                        "companyId": 1001,
                        "companyName": "Entreprise A",
                        "nombreMoisEcart": 12,
                        "payeAncienneté": true,
                        "dateCreation": "2024-01-15T10:30:00",
                        "dateModification": "2024-03-10T16:45:00"
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Paramètre d'ancienneté non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Paramètre d'ancienneté non trouvé",
                      "data": null
                    }
                    """
                            )
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(
                    description = "ID du paramètre à mettre à jour",
                    example = "1",
                    required = true
            )
            @PathVariable Long id,
            @RequestBody AncienneteSettingDTO dto) {
        try {
            if (!ancienneteSettingService.getById(id).isPresent()) {
                return new ResponseEntity<>(
                        new ApiResponse<>(false, "Paramètre d'ancienneté non trouvé", null),
                        HttpStatus.NOT_FOUND
                );
            }

            dto.setId(id);
            AncienneteSettingDTO updated = ancienneteSettingService.save(dto);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Paramètre d'ancienneté mis à jour avec succès", updated)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la mise à jour: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Supprimer un paramètre d'ancienneté",
            description = """
                Supprime définitivement un paramètre d'ancienneté.
                
                Conséquences :
                - Suppression irréversible
                - Impact sur les calculs d'ancienneté futurs
                - Nécessite une nouvelle configuration pour réactiver
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paramètre d'ancienneté supprimé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Paramètre d'ancienneté supprimé avec succès",
                      "data": null
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Paramètre d'ancienneté non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Paramètre d'ancienneté non trouvé",
                      "data": null
                    }
                    """
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(
                    description = "ID du paramètre à supprimer",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        if (!ancienneteSettingService.getById(id).isPresent()) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Paramètre d'ancienneté non trouvé", null),
                    HttpStatus.NOT_FOUND
            );
        }
        try {
            ancienneteSettingService.delete(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Paramètre d'ancienneté supprimé avec succès", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,
                            "Erreur lors de la suppression: " + e.getMessage(), null));
        }
    }

    @Operation(
            summary = "Vérifier si l'ancienneté est activée pour une entreprise",
            description = """
                Vérifie si le paiement de l'ancienneté est activé pour une entreprise spécifique.
                
                Utilisation typique :
                - Vérification avant calcul d'ancienneté
                - Configuration des processus RH
                - Affichage conditionnel dans l'interface
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Statut de l'ancienneté récupéré",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Statut de l'ancienneté récupéré",
                      "data": true
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Paramètre d'ancienneté non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Paramètre d'ancienneté non trouvé",
                      "data": null
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping("/entreprise/{companyId}/actif")
    public ResponseEntity<ApiResponse<Boolean>> isAncienneteEnabled(
            @Parameter(
                    description = "ID de l'entreprise",
                    example = "1001",
                    required = true
            )
            @PathVariable Long companyId) {
        Optional<AncienneteSettingDTO> setting = ancienneteSettingService.getByCompanyId(companyId);
        if (!setting.isPresent()) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Paramètre d'ancienneté non trouvé", null),
                    HttpStatus.NOT_FOUND
            );
        }
        boolean enabled = ancienneteSettingService.isAncienneteEnabledForCompany(companyId);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Statut de l'ancienneté récupéré", enabled)
        );
    }

    @Operation(
            summary = "Récupérer l'écart en mois configuré pour une entreprise",
            description = """
                Retourne le nombre de mois d'écart minimum configuré entre deux calculs d'ancienneté.
                
                Signification métier :
                - Période minimale entre deux paiements d'ancienneté
                - Contrôle de fréquence des calculs
                - Paramètre de régulation des coûts
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Écart en mois récupéré",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Écart en mois récupéré",
                      "data": 6
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Paramètre d'ancienneté non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Paramètre d'ancienneté non trouvé",
                      "data": null
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping("/entreprise/{companyId}/ecart")
    public ResponseEntity<ApiResponse<Integer>> getEcartMois(
            @Parameter(
                    description = "ID de l'entreprise",
                    example = "1001",
                    required = true
            )
            @PathVariable Long companyId) {
        Optional<AncienneteSettingDTO> setting = ancienneteSettingService.getByCompanyId(companyId);
        if (!setting.isPresent()) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Paramètre d'ancienneté non trouvé", null),
                    HttpStatus.NOT_FOUND
            );
        }
        Integer ecart = ancienneteSettingService.getEcartMoisForCompany(companyId);
        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Écart en mois récupéré", ecart)
        );
    }
}