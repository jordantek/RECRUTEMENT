package com.tpc.tpcgestpaie.localapp.dto.conge.historique;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Détail d'une répartition active")
public class RepartitionDetailDTO {

    @Schema(description = "ID répartition", example = "501")
    private Long repartitionId;

    @Schema(description = "ID provision concernée", example = "101")
    private Long provisionId;

    @Schema(description = "Mois concerné", example = "Janvier 2025")
    private String moisConcerne;

    @Schema(description = "Référence mois (YYYY-MM)", example = "2025-01")
    private String moisReference;

    @Schema(description = "Jours consommés sur ce mois", example = "2.5")
    private BigDecimal joursConsommes;

    @Schema(description = "Montant consommé (€)", example = "250.00")
    private BigDecimal montantConsomme;

    @Schema(description = "Taux journalier appliqué (€)", example = "100.00")
    private BigDecimal tauxJournalier;

    @Schema(description = "Date de consommation", example = "2025-03-10T08:30:00")
    private LocalDateTime dateConsommation;

    @Schema(description = "Solde restant sur ce mois après conso", example = "0.0")
    private BigDecimal soldeRestantMois;

    @Schema(description = "Statut du mois après conso", example = "CLOTURE")
    private String statutMoisApresConso;
}