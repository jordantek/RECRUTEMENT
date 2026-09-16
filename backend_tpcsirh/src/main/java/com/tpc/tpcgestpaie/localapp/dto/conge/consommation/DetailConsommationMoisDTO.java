package com.tpc.tpcgestpaie.localapp.dto.conge.consommation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Détail de consommation sur un mois de provision")
public class DetailConsommationMoisDTO {

    @Schema(description = "ID de la provision", example = "101")
    private Long provisionId;

    @Schema(description = "Référence du mois (YYYY-MM)", example = "2025-01")
    private String moisReference;

    @Schema(description = "Libellé du mois", example = "Janvier 2025")
    private String moisLibelle;

    @Schema(description = "Jours acquis initialement sur ce mois", example = "2.5")
    private BigDecimal joursAcquisMois;

    @Schema(description = "Jours pris sur ce mois", example = "2.5")
    private BigDecimal joursPris;

    @Schema(description = "Jours restants après cette consommation", example = "0.0")
    private BigDecimal joursRestantsApres;

    @Schema(description = "Montant pris sur ce mois (€)", example = "250.00")
    private BigDecimal montantPris;

    @Schema(description = "Valeur d'un jour de congé pour ce mois (€)", example = "100.00")
    private BigDecimal valeurJour;

    @Schema(description = "Taux journalier calculé (identique à valeurJour)", example = "100.00")
    private BigDecimal tauxJournalier;

    @Schema(description = "Statut du mois après consommation", example = "CLOTURE",
            allowableValues = {"ACTIF", "PARTIELLEMENT_CONSOMME", "CLOTURE"})
    private String statutMois;

    @Schema(description = "Pourcentage du mois consommé", example = "100.0")
    private BigDecimal pourcentageConsomme;

    private String typeProvision;
    private Boolean estProvisionInitiale;
}