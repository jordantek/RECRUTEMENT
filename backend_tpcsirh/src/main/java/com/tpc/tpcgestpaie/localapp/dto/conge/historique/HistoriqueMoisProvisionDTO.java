package com.tpc.tpcgestpaie.localapp.dto.conge.historique;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Schema(description = "Historique d'un mois de provision (vue audit)")
public class HistoriqueMoisProvisionDTO {

    // ====== Identité ======
    @Schema(description = "ID provision", example = "101")
    private Long provisionId;

    @Schema(description = "Mois", example = "Janvier 2025")
    private String moisLibelle;

    @Schema(description = "Référence (YYYY-MM)", example = "2025-01")
    private String moisReference;

    @Schema(description = "ID employé", example = "42")
    private Long employeId;

    @Schema(description = "Nom employé", example = "KARIM Benali")
    private String nomEmploye;

    // ====== Données initiales ======
    @Schema(description = "Jours acquis", example = "2.5")
    private BigDecimal joursAcquis;

    @Schema(description = "Provision initiale (€)", example = "250.00")
    private BigDecimal provisionInitiale;

    @Schema(description = "Salaire brut de référence (€)", example = "3000.00")
    private BigDecimal salaireBrutReference;

    @Schema(description = "Jours travaillés de référence", example = "30.0")
    private BigDecimal joursTravaillesReference;

    @Schema(description = "Valeur d'un jour (€)", example = "100.00")
    private BigDecimal valeurJour;

    // ====== Soldes actuels ======
    @Schema(description = "Jours consommés", example = "2.5")
    private BigDecimal joursConsommes;

    @Schema(description = "Montant consommé (€)", example = "250.00")
    private BigDecimal montantConsomme;

    @Schema(description = "Jours restants", example = "0.0")
    private BigDecimal joursRestants;

    @Schema(description = "Solde financier (€)", example = "0.00")
    private BigDecimal soldeFinancier;

    @Schema(description = "Statut", example = "CLOTURE")
    private String statut;

    // ====== Consommations ======
    @Schema(description = "Nombre de demandes ayant consommé", example = "2")
    private Integer nombreDemandesConcernees;

    @Schema(description = "Liste des consommations")
    private List<ConsommationMoisDTO> consommations;

    // ====== Chronologie ======
    @Schema(description = "Date création provision")
    private String dateCreation;

    @Schema(description = "Date dernière consommation")
    private String dateDerniereConsommation;
}