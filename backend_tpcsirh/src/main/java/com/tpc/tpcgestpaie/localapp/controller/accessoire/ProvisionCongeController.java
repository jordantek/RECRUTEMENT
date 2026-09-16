package com.tpc.tpcgestpaie.localapp.controller.accessoire;

import com.tpc.tpcgestpaie.localapp.dto.accessoire.ProvisionCongeSimulationRequestDTO;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(
        name = "Gestion administrative/Provision de Congé",
        description = "API de simulation et calcul des provisions de congé basées sur le salaire brut annuel, le temps de travail et les jours à provisionner"
)
@RestController
@RequestMapping("/api/provision-conge")
public class ProvisionCongeController {

    @Operation(
            summary = "Simuler le calcul de provision de congé",
            description = """
                    Calcule le montant de la provision de congé à constituer pour un employé en fonction de :
                    - Son salaire brut total sur 12 mois
                    - Son temps de travail total (en jours)
                    - Le nombre de jours de congé à provisionner
                    
                    Formule de calcul :
                    1. Salaire provisoire = Total salaire brut 12 mois / Total temps travail
                    2. Salaire normal = (Salaire provisoire × 30) / 24
                    3. Provision = Salaire normal × Jours à provisionner
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Provision calculée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Succès du calcul",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Provision calculée avec succès",
                                      "data": 250000
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Erreurs de validation des paramètres d'entrée",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Salaire invalide",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Erreurs de validation",
                                              "data": [
                                                {
                                                  "field": "totalSalaireBrut12mois",
                                                  "message": "Le total du salaire brut sur 12 mois doit être supérieur à zéro."
                                                }
                                              ]
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Temps de travail invalide",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Erreurs de validation",
                                              "data": [
                                                {
                                                  "field": "totalTempsTravail",
                                                  "message": "Le total du temps de travail doit être supérieur à zéro."
                                                }
                                              ]
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Jours à provisionner invalides",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Erreurs de validation",
                                              "data": [
                                                {
                                                  "field": "joursAProvisionner",
                                                  "message": "Le nombre de jours à provisionner doit être supérieur à zéro."
                                                }
                                              ]
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Multiples erreurs",
                                            value = """
                                            {
                                              "success": false,
                                              "message": "Erreurs de validation",
                                              "data": [
                                                {
                                                  "field": "totalSalaireBrut12mois",
                                                  "message": "Le total du salaire brut sur 12 mois doit être supérieur à zéro."
                                                },
                                                {
                                                  "field": "totalTempsTravail",
                                                  "message": "Le total du temps de travail doit être supérieur à zéro."
                                                },
                                                {
                                                  "field": "joursAProvisionner",
                                                  "message": "Le nombre de jours à provisionner doit être supérieur à zéro."
                                                }
                                              ]
                                            }
                                            """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/simuler")
    public ResponseEntity<?> simulerProvision(
            @Parameter(
                    description = """
                            Données pour la simulation de provision de congé :
                            - totalSalaireBrut12mois : Total du salaire brut sur 12 mois (doit être > 0)
                            - totalTempsTravail : Nombre total de jours travaillés (doit être > 0)
                            - joursAProvisionner : Nombre de jours de congé à provisionner (doit être > 0)
                            """,
                    required = true,
                    schema = @Schema(
                            implementation = ProvisionCongeSimulationRequestDTO.class,
                            example = """
                            {
                              "totalSalaireBrut12mois": 3600000,
                              "totalTempsTravail": 288,
                              "joursAProvisionner": 2.5
                            }
                            """
                    )
            )
            @RequestBody ProvisionCongeSimulationRequestDTO request) {
        List<ErrorResponse> errors = new ArrayList<>();

        // Vérifier totalSalaireBrut12mois > 0
        if (request.getTotalSalaireBrut12mois() == null || request.getTotalSalaireBrut12mois().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(new ErrorResponse("totalSalaireBrut12mois", "Le total du salaire brut sur 12 mois doit être supérieur à zéro."));
        }

        // Vérifier totalTempsTravail > 0
        if (request.getTotalTempsTravail() <= 0) {
            errors.add(new ErrorResponse("totalTempsTravail", "Le total du temps de travail doit être supérieur à zéro."));
        }

        // Vérifier joursAProvisionner > 0
        if (request.getJoursAProvisionner() <= 0) {
            errors.add(new ErrorResponse("joursAProvisionner", "Le nombre de jours à provisionner doit être supérieur à zéro."));
        }

        // Si erreurs, on retourne directement
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de validation", errors));
        }

        // Sinon on calcule la provision
        BigDecimal provision = simulerProvisionConge(
                request.getTotalSalaireBrut12mois(),
                request.getTotalTempsTravail(),
                request.getJoursAProvisionner()
        );


        return new ResponseEntity<>(new ApiResponse<>(true, "Provision calculée avec succès", provision), HttpStatus.CREATED);


    }

    /**
     * Méthode privée de calcul de la provision de congé
     *
     * @param totalSalaireBrut12mois Total du salaire brut sur 12 mois
     * @param totalTempsTravail Nombre total de jours travaillés
     * @param joursAProvisionner Nombre de jours de congé à provisionner
     * @return Le montant de la provision calculée
     */
    public BigDecimal simulerProvisionConge(BigDecimal totalSalaireBrut12mois, double totalTempsTravail, long joursAProvisionner) {
        if (totalTempsTravail <= 0 || totalSalaireBrut12mois == null || totalSalaireBrut12mois.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Étape 1 : Calculer le salaire provisoire par jour travaillé
        BigDecimal salaireProvisoire = totalSalaireBrut12mois
                .divide(BigDecimal.valueOf(totalTempsTravail), 2, RoundingMode.HALF_UP);

        // Étape 2 : Calculer le salaire normal mensuel (30 jours) basé sur 24 jours ouvrables
        BigDecimal salaireNormal = salaireProvisoire
                .multiply(BigDecimal.valueOf(30))
                .divide(BigDecimal.valueOf(24), 2, RoundingMode.HALF_UP);

        // Étape 3 : Calculer la provision finale (arrondi à l'entier le plus proche)
        return salaireNormal
                .multiply(BigDecimal.valueOf(joursAProvisionner))
                .setScale(0, RoundingMode.HALF_UP);
    }


}