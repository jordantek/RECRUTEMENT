package com.tpc.tpcgestpaie.localapp.dto.conge.historique;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Détail d'une répartition annulée")
public class RepartitionAnnuleeDTO {

    @Schema(description = "ID répartition", example = "501")
    private Long repartitionId;

    @Schema(description = "Mois concerné", example = "Janvier 2025")
    private String moisConcerne;

    @Schema(description = "Référence mois (YYYY-MM)", example = "2025-01")
    private String moisReference;

    @Schema(description = "Jours annulés", example = "2.5")
    private BigDecimal joursAnnules;

    @Schema(description = "Montant annulé (€)", example = "250.00")
    private BigDecimal montantAnnule;

    @Schema(description = "Valeur jour historique", example = "100.00")
    private BigDecimal valeurJourHistorique;

    @Schema(description = "Date d'annulation", example = "2025-03-11T14:22:10")
    private LocalDateTime dateAnnulation;

    @Schema(description = "Motif de l'annulation", example = "Erreur de saisie des dates")
    private String motifAnnulation;

    @Schema(description = "Annulé par", example = "DUPONT Marie")
    private String annulePar;

    @Schema(description = "ID utilisateur annulation", example = "15")
    private Long annuleParId;
}