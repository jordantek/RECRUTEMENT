package com.tpc.tpcgestpaie.localapp.controller.administration;

import com.tpc.tpcgestpaie.localapp.model.TypeAbsence;
import com.tpc.tpcgestpaie.localapp.service.administration.TypeAbsenceService;
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

import java.util.List;
import java.util.Optional;

@Tag(
        name = "Gestion administrative/Types d'Absence",
        description = "API de gestion des types d'absence : création, consultation, modification et suppression des catégories d'absence (congés, maladies, formations, etc.)"
)
@RestController
@RequestMapping("/api/administration/type-absences")
public class TypeAbsenceController {

    private final TypeAbsenceService typeAbsenceService;

    public TypeAbsenceController(TypeAbsenceService typeAbsenceService) {
        this.typeAbsenceService = typeAbsenceService;
    }

    @Operation(
            summary = "Lister tous les types d'absence",
            description = "Récupère la liste complète de tous les types d'absence définis dans le système"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des types d'absence récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des types d'absence récupérée avec succès",
                      "data": [
                        {
                          "id": 1,
                          "libelle": "CONGE_PAYE",
                          "description": "Congés payés annuels",
                          "created_at": "2024-01-15T10:30:00",
                          "updated_at": null
                        },
                        {
                          "id": 2,
                          "libelle": "MALADIE",
                          "description": "Absence pour maladie",
                          "created_at": "2024-01-15T10:30:00",
                          "updated_at": "2024-02-20T14:45:00"
                        },
                        {
                          "id": 3,
                          "libelle": "CONGE_MATERNITE",
                          "description": "Congé de maternité",
                          "created_at": "2024-01-15T10:30:00",
                          "updated_at": null
                        },
                        {
                          "id": 4,
                          "libelle": "CONGE_FORMATION",
                          "description": "Congé pour formation professionnelle",
                          "created_at": "2024-02-01T09:15:00",
                          "updated_at": null
                        }
                      ]
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
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<TypeAbsence> list = typeAbsenceService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des types d'absence récupérée avec succès", list));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Récupérer un type d'absence par son identifiant",
            description = "Retourne les détails d'un type d'absence spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Type d'absence trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Type d'absence trouvé",
                      "data": {
                        "id": 1,
                        "libelle": "CONGE_PAYE",
                        "description": "Congés payés annuels accumulés par l'employé",
                        "created_at": "2024-01-15T10:30:00",
                        "updated_at": null
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Type d'absence non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Type d'absence non trouvé",
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
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @Parameter(description = "Identifiant du type d'absence", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Optional<TypeAbsence> type = typeAbsenceService.findById(id);
            return type.map(value ->
                    ResponseEntity.ok(new ApiResponse<>(true, "Type d'absence trouvé", value))
            ).orElseGet(() ->
                    new ResponseEntity<>(new ApiResponse<>(false, "Type d'absence non trouvé", null), HttpStatus.NOT_FOUND)
            );
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Créer un nouveau type d'absence",
            description = """
                Ajoute un nouveau type d'absence dans le système.
                
                Validations effectuées :
                - Le libellé est obligatoire et ne peut pas être vide
                - Le libellé est automatiquement formaté en majuscules
                - Vérification des doublons (un même libellé ne peut exister deux fois)
                - La description est optionnelle
                
                Comportement automatique :
                - Trim des espaces au début et à la fin du libellé
                - Conversion en majuscules pour uniformisation
                - Vérification d'unicité avant sauvegarde
                
                Exemples de types d'absence courants :
                - CONGE_PAYE
                - MALADIE
                - CONGE_MATERNITE
                - CONGE_PATERNITE
                - CONGE_FORMATION
                - CONGE_SANS_SOLDE
                - ABSENCE_NON_JUSTIFIEE
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Type d'absence créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Type d'absence créé avec succès",
                      "data": {
                        "id": 5,
                        "libelle": "CONGE_PATERNITE",
                        "description": "Congé de paternité après naissance d'un enfant",
                        "created_at": "2024-03-20T14:30:00",
                        "updated_at": null
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Libellé vide",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Le libellé est vide",
                      "data": null
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Libellé déjà existant",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Ce libellé existe déjà.",
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
    @PostMapping
    public ResponseEntity<?> create(
            @Parameter(
                    description = "Données du nouveau type d'absence",
                    required = true,
                    schema = @Schema(implementation = TypeAbsence.class)
            )
            @RequestBody TypeAbsence typeAbsence) {
        try {
            if (typeAbsence.getLibelle() == null || typeAbsence.getLibelle().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le libellé est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            String libelleFormate = typeAbsence.getLibelle().trim().toUpperCase();

            if (typeAbsenceService.existsByLibelle(libelleFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Ce libellé existe déjà.", null));
            }

            typeAbsence.setLibelle(libelleFormate);
            TypeAbsence saved = typeAbsenceService.save(typeAbsence);
            return new ResponseEntity<>(new ApiResponse<>(true, "Type d'absence créé avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Mettre à jour un type d'absence existant",
            description = """
                Modifie les informations d'un type d'absence existant.
                
                Validations effectuées :
                - Vérification de l'existence du type d'absence
                - Le libellé est automatiquement formaté en majuscules si fourni
                - La description peut être mise à jour
                - Mise à jour de la date de modification
                
                Champs modifiables :
                - libelle (formaté automatiquement)
                - description
                - updated_at (géré automatiquement par le système)
                
                Note : La vérification d'unicité du libellé n'est pas effectuée lors de la mise à jour.
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Type d'absence mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Type d'absence mis à jour avec succès",
                      "data": {
                        "id": 2,
                        "libelle": "MALADIE_AVEC_CERTIFICAT",
                        "description": "Absence pour maladie nécessitant un certificat médical",
                        "created_at": "2024-01-15T10:30:00",
                        "updated_at": "2024-03-20T15:45:00"
                      }
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Type d'absence non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Type d'absence non trouvé",
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
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(description = "Identifiant du type d'absence à modifier", required = true, example = "2")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données du type d'absence", required = true)
            @RequestBody TypeAbsence updated) {
        try {
            Optional<TypeAbsence> existing = typeAbsenceService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Type d'absence non trouvé", null), HttpStatus.NOT_FOUND);
            }

            TypeAbsence type = existing.get();
            String libelleFormate = updated.getLibelle() != null ? updated.getLibelle().trim().toUpperCase() : null;
            type.setLibelle(libelleFormate);
            type.setDescription(updated.getDescription());
            type.setUpdated_at(updated.getUpdated_at());

            TypeAbsence saved = typeAbsenceService.save(type);
            return ResponseEntity.ok(new ApiResponse<>(true, "Type d'absence mis à jour avec succès", saved));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Supprimer un type d'absence",
            description = """
                Supprime définitivement un type d'absence du système.
                
                Attention : 
                - Cette opération est irréversible
                - Les absences existantes qui référencent ce type d'absence peuvent être affectées
                - Vérifier qu'aucune absence n'utilise ce type avant suppression
                
                Recommandation :
                - Préférer la désactivation plutôt que la suppression si possible
                - Consulter les rapports d'utilisation avant suppression
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Type d'absence supprimé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Type d'absence supprimé avec succès",
                      "data": null
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Type d'absence non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Type d'absence non trouvé",
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
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @Parameter(description = "Identifiant du type d'absence à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Optional<TypeAbsence> existing = typeAbsenceService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Type d'absence non trouvé", null), HttpStatus.NOT_FOUND);
            }

            typeAbsenceService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Type d'absence supprimé avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}