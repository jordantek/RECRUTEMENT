package com.tpc.tpcgestpaie.localapp.controller.administration;

import com.tpc.tpcgestpaie.localapp.model.MotifAbsence;
import com.tpc.tpcgestpaie.localapp.service.administration.MotifAbsenceService;
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
        name = "Gestion administrative/Motifs d'Absence",
        description = "API de gestion des motifs d'absence : création, consultation, modification et suppression des motifs justifiant les absences des employés"
)
@RestController
@RequestMapping("/api/arh/motifs-absence")
public class MotifAbsenceController {

    private final MotifAbsenceService motifAbsenceService;

    public MotifAbsenceController(MotifAbsenceService motifAbsenceService) {
        this.motifAbsenceService = motifAbsenceService;
    }

    @Operation(
            summary = "Lister tous les motifs d'absence",
            description = "Récupère la liste complète de tous les motifs d'absence définis dans le système"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Liste des motifs d'absence récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Liste des motifs d'absence récupérée avec succès",
                      "data": [
                        {
                          "id": 1,
                          "libelle": "MALADIE",
                          "created_at": "2024-01-15T10:30:00",
                          "updated_at": null
                        },
                        {
                          "id": 2,
                          "libelle": "CONGE_PAYE",
                          "created_at": "2024-01-15T10:30:00",
                          "updated_at": null
                        },
                        {
                          "id": 3,
                          "libelle": "CONGE_SANS_SOLDE",
                          "created_at": "2024-01-15T10:30:00",
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
            List<MotifAbsence> motifs = motifAbsenceService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des motifs d'absence récupérée avec succès", motifs));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des motifs", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Récupérer un motif d'absence par son identifiant",
            description = "Retourne les détails d'un motif d'absence spécifique"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Motif d'absence trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Motif d'absence trouvé",
                      "data": {
                        "id": 1,
                        "libelle": "MALADIE",
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
                    description = "Motif d'absence non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Motif d'absence non trouvé",
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
            @Parameter(description = "Identifiant du motif d'absence", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Optional<MotifAbsence> motif = motifAbsenceService.findById(id);
            return motif.map(value -> ResponseEntity.ok(new ApiResponse<>(true, "Motif d'absence trouvé", value)))
                    .orElseGet(() -> new ResponseEntity<>(new ApiResponse<>(false, "Motif d'absence non trouvé", null), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération du motif", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Créer un nouveau motif d'absence",
            description = """
                Ajoute un nouveau motif d'absence dans le système.
                
                Validations effectuées :
                - Le libellé est obligatoire et ne peut pas être vide
                - Le libellé est automatiquement formaté en majuscules
                - Vérification des doublons (un même libellé ne peut exister deux fois)
                
                Comportement automatique :
                - Trim des espaces au début et à la fin
                - Conversion en majuscules
                - Vérification d'unicité
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Motif d'absence créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Motif d'absence créé avec succès",
                      "data": {
                        "id": 4,
                        "libelle": "CONGE_MATERNITE",
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
                      "message": "Le libellé existe déjà.",
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
                    description = "Données du nouveau motif d'absence",
                    required = true,
                    schema = @Schema(implementation = MotifAbsence.class)
            )
            @RequestBody MotifAbsence motifAbsence) {
        try {
            if (motifAbsence.getLibelle() == null || motifAbsence.getLibelle().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le libellé est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            String libelleFormate = motifAbsence.getLibelle().trim().toUpperCase();

            if (motifAbsenceService.existsByLibelle(libelleFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Le libellé existe déjà.", null));
            }

            motifAbsence.setLibelle(libelleFormate);
            MotifAbsence saved = motifAbsenceService.save(motifAbsence);
            return new ResponseEntity<>(new ApiResponse<>(true, "Motif d'absence créé avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création du motif", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Mettre à jour un motif d'absence existant",
            description = """
                Modifie les informations d'un motif d'absence existant.
                
                Validations effectuées :
                - Vérification de l'existence du motif
                - Le libellé est automatiquement formaté en majuscules si fourni
                - Mise à jour de la date de modification
                
                Note : La vérification d'unicité du libellé n'est pas effectuée lors de la mise à jour.
                """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Motif d'absence mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès",
                                    value = """
                    {
                      "success": true,
                      "message": "Motif d'absence mis à jour avec succès",
                      "data": {
                        "id": 1,
                        "libelle": "MALADIE_AVEC_CERTIFICAT",
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
                    description = "Motif d'absence non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Motif d'absence non trouvé",
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
            @Parameter(description = "Identifiant du motif d'absence à modifier", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données du motif d'absence", required = true)
            @RequestBody MotifAbsence updatedMotif) {
        try {
            Optional<MotifAbsence> existing = motifAbsenceService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Motif d'absence non trouvé", null), HttpStatus.NOT_FOUND);
            }

            MotifAbsence motif = existing.get();
            String libelleFormate = updatedMotif.getLibelle() != null ? updatedMotif.getLibelle().trim().toUpperCase() : null;
            motif.setLibelle(libelleFormate);
            motif.setUpdated_at(updatedMotif.getUpdated_at());

            MotifAbsence saved = motifAbsenceService.save(motif);
            return ResponseEntity.ok(new ApiResponse<>(true, "Motif d'absence mis à jour avec succès", saved));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour du motif", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Supprimer un motif d'absence",
            description = "Supprime définitivement un motif d'absence du système. Attention : cette opération est irréversible et peut affecter les absences existantes qui référencent ce motif."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Motif d'absence supprimé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": true,
                      "message": "Motif d'absence supprimé avec succès",
                      "data": null
                    }
                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Motif d'absence non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                    {
                      "success": false,
                      "message": "Motif d'absence non trouvé",
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
            @Parameter(description = "Identifiant du motif d'absence à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Optional<MotifAbsence> existing = motifAbsenceService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Motif d'absence non trouvé", null), HttpStatus.NOT_FOUND);
            }

            motifAbsenceService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Motif d'absence supprimé avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression du motif", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}