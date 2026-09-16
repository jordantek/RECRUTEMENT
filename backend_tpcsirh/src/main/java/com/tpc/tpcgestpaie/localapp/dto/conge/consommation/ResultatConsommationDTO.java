package com.tpc.tpcgestpaie.localapp.dto.conge.consommation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Schema(description = "Résultat d'une consommation de provisions de congé")
public class ResultatConsommationDTO {

    @Schema(description = "ID de la demande de congé", example = "1001")
    private Long demandeId;

    @Schema(description = "Nombre total de jours consommés", example = "4.0")
    private BigDecimal joursTotal;

    @Schema(description = "Montant total alloué en €", example = "410.00")
    private BigDecimal montantTotal;

    @Schema(description = "Nombre de mois de provision touchés", example = "2")
    private Integer nombreMoisTouches;

    @Schema(description = "Détail de la consommation par mois")
    private List<DetailConsommationMoisDTO> detailsParMois;

    @Schema(description = "Message récapitulatif pour notification",
            example = "Congé confirmé. Montant total: 410.00 €\nDétail par mois:\n- Janvier 2025: 2.5 jours (250.00 €)\n- Février 2025: 1.5 jours (160.00 €)")
    private String message;

    @Schema(description = "Succès de l'opération", example = "true")
    private Boolean succes;

    @Schema(description = "Timestamp de l'opération", example = "2025-03-10T08:30:15")
    private String timestamp;

    private Integer nombreProvisionsNormalesTouches;
    private Boolean provisionInitialeEpuisee;
    private Boolean provisionInitialePartiellementConsommee;
    private BigDecimal joursRestantsDansProvisionInitiale;
}