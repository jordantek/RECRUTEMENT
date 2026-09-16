package com.tpc.tpcgestpaie.localapp.dto.conge.annulation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Détail de restauration sur un mois de provision")
public class DetailRestaurationMoisDTO {

    @Schema(description = "ID de la provision restaurée", example = "101")
    private Long provisionId;

    @Schema(description = "Référence du mois (YYYY-MM)", example = "2025-01")
    private String moisReference;

    @Schema(description = "Libellé du mois", example = "Janvier 2025")
    private String moisLibelle;

    @Schema(description = "Jours restaurés sur ce mois", example = "2.5")
    private BigDecimal joursRestaures;

    @Schema(description = "Montant restauré sur ce mois (€)", example = "250.00")
    private BigDecimal montantRestaure;

    @Schema(description = "Valeur du jour historique (au moment de la conso)", example = "100.00")
    private BigDecimal valeurJourHistorique;

    @Schema(description = "Nouveau solde en jours après restauration", example = "2.5")
    private BigDecimal nouveauSoldeJours;

    @Schema(description = "Nouveau solde en € après restauration", example = "250.00")
    private BigDecimal nouveauSoldeMontant;

    @Schema(description = "Nouveau statut du mois", example = "ACTIF",
            allowableValues = {"ACTIF", "PARTIELLEMENT_CONSOMME", "CLOTURE"})
    private String nouveauStatut;

    @Schema(description = "État avant restauration", example = "CLOTURE")
    private String statutAvant;

    @Schema(description = "ID de la répartition annulée", example = "501")
    private Long repartitionId;
}