package com.tpc.tpcgestpaie.localapp.dto.conge.solde;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Détail du solde pour un mois de provision")
public class DetailSoldeMoisDTO {

    @Schema(description = "ID de la provision", example = "101")
    private Long provisionId;

    @Schema(description = "Référence du mois (YYYY-MM)", example = "2025-01")
    private String moisReference;

    @Schema(description = "Libellé du mois", example = "Janvier 2025")
    private String moisLibelle;

    // ====== Acquisitions ======
    @Schema(description = "Jours acquis ce mois", example = "2.5")
    private BigDecimal joursAcquis;

    @Schema(description = "Provision initiale (€)", example = "250.00")
    private BigDecimal provisionInitiale;

    @Schema(description = "Salaire brut du mois (€)", example = "3000.00")
    private BigDecimal salaireBrutMois;

    @Schema(description = "Jours travaillés dans le mois", example = "30.0")
    private BigDecimal joursTravaillesMois;

    @Schema(description = "Valeur d'un jour de congé (€)", example = "100.00")
    private BigDecimal valeurJour;

    // ====== Consommations ======
    @Schema(description = "Jours consommés sur ce mois", example = "2.5")
    private BigDecimal joursConsommes;

    @Schema(description = "Montant consommé (€)", example = "250.00")
    private BigDecimal montantConsomme;

    // ====== Soldes ======
    @Schema(description = "Jours restants", example = "0.0")
    private BigDecimal joursRestants;

    @Schema(description = "Solde financier restant (€)", example = "0.00")
    private BigDecimal soldeFinancier;

    @Schema(description = "Statut", example = "CLOTURE",
            allowableValues = {"ACTIF", "PARTIELLEMENT_CONSOMME", "CLOTURE", "ANNULE"})
    private String statut;

    @Schema(description = "Pourcentage consommé", example = "100.0")
    private BigDecimal pourcentageConsomme;

    // ====== Historique récent ======
    @Schema(description = "Dernière consommation sur ce mois", example = "10/03/2025")
    private String dateDerniereConsommation;

    @Schema(description = "Nombre de demandes ayant consommé ce mois", example = "2")
    private Integer nombreDemandesConcernees;
}