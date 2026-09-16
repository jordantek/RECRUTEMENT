package com.tpc.tpcgestpaie.localapp.dto.conge.solde;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Schema(description = "Évolution du solde pour un mois spécifique")
public class MoisHistoriqueDTO {

    @Schema(description = "Référence mois (YYYY-MM)", example = "2025-01")
    private String moisReference;

    @Schema(description = "Libellé", example = "Janvier 2025")
    private String moisLibelle;

    // ====== Entrées ======
    @Schema(description = "Jours acquis ce mois", example = "2.5")
    private BigDecimal joursAcquis;

    @Schema(description = "Provision générée (€)", example = "250.00")
    private BigDecimal provisionInitiale;

    // ====== Sorties ======
    @Schema(description = "Jours consommés ce mois", example = "2.5")
    private BigDecimal joursConsommes;

    @Schema(description = "Montant consommé (€)", example = "250.00")
    private BigDecimal montantConsomme;

    // ====== Solde du mois ======
    @Schema(description = "Jours restants sur ce mois", example = "0.0")
    private BigDecimal joursRestantsMois;

    @Schema(description = "Solde financier du mois (€)", example = "0.00")
    private BigDecimal soldeFinancierMois;

    @Schema(description = "Statut du mois", example = "CLOTURE")
    private String statutMois;

    // ====== Cumuls (running totals) ======
    @Schema(description = "Solde cumulé en jours après ce mois", example = "5.5")
    private BigDecimal soldeJoursCumule;

    @Schema(description = "Solde cumulé en € après ce mois", example = "583.34")
    private BigDecimal soldeFinancierCumule;

    // ====== Détail des consommations ======
    @Schema(description = "Liste des consommations sur ce mois")
    private List<ConsommationResumeDTO> consommations;
}