package com.tpc.tpcgestpaie.localapp.dto.conge.solde;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Schema(description = "Historique complet des congés d'un employé")
public class HistoriqueEmployeDTO {

    // ====== Identité ======
    @Schema(description = "ID employé", example = "42")
    private Long employeId;

    @Schema(description = "Nom complet", example = "KARIM Benali")
    private String nomEmploye;

    @Schema(description = "Matricule", example = "EMP001")
    private String matricule;

    @Schema(description = "Date d'embauche", example = "2020-01-15")
    private LocalDate dateEmbauche;

    @Schema(description = "Ancienneté en années", example = "5")
    private Integer ancienneteAnnees;

    // ====== Solde actuel ======
    @Schema(description = "Solde actuel en jours", example = "8.5")
    private BigDecimal soldeJoursActuel;

    @Schema(description = "Solde actuel en €", example = "906.67")
    private BigDecimal soldeMontantActuel;

    // ====== Statistiques globales ======
    @Schema(description = "Total jours acquis depuis début", example = "150.0")
    private BigDecimal totalJoursAcquisHistorique;

    @Schema(description = "Total jours consommés", example = "141.5")
    private BigDecimal totalJoursConsommesHistorique;

    @Schema(description = "Total jours annulés/restaurés", example = "5.0")
    private BigDecimal totalJoursAnnules;

    @Schema(description = "Nombre total de demandes", example = "45")
    private Integer nombreTotalDemandes;

    @Schema(description = "Nombre de demandes en cours", example = "2")
    private Integer nombreDemandesEnCours;

    @Schema(description = "Nombre de demandes terminées", example = "38")
    private Integer nombreDemandesTerminees;

    @Schema(description = "Nombre de demandes annulées", example = "5")
    private Integer nombreDemandesAnnulees;

    // ====== Évolution mensuelle ======
    @Schema(description = "Évolution mois par mois")
    private List<MoisHistoriqueDTO> evolutionMensuelle;

    // ====== Demandes récentes ======
    @Schema(description = "5 dernières demandes")
    private List<DemandeResumeDTO> dernieresDemandes;

    // ====== Alertes ======
    @Schema(description = "Alertes éventuelles")
    private List<String> alertes;
}