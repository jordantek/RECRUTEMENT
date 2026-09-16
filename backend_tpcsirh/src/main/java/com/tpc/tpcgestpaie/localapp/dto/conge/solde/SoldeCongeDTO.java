package com.tpc.tpcgestpaie.localapp.dto.conge.solde;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Schema(description = "Solde de congés d'un employé")
public class SoldeCongeDTO {

    @Schema(description = "ID de l'employé", example = "42")
    private Long employeId;

    @Schema(description = "Nom complet de l'employé", example = "KARIM Benali")
    private String nomEmploye;

    @Schema(description = "Matricule", example = "EMP001")
    private String matricule;

    @Schema(description = "Date du calcul", example = "2025-03-10")
    private LocalDate dateCalcul;

    // ====== Totaux ======
    @Schema(description = "Total des jours acquis depuis l'embauche", example = "12.5")
    private BigDecimal totalJoursAcquis;

    @Schema(description = "Total des jours consommés", example = "4.0")
    private BigDecimal totalJoursConsommes;

    @Schema(description = "Jours disponibles (solde)", example = "8.5")
    private BigDecimal soldeJoursDisponibles;

    @Schema(description = "Valeur estimée du solde (€)", example = "906.67")
    private BigDecimal valeurEstimeeSolde;

    @Schema(description = "Montant total provisionné (€)", example = "1333.34")
    private BigDecimal montantTotalProvisionne;

    @Schema(description = "Montant total consommé (€)", example = "426.67")
    private BigDecimal montantTotalConsomme;

    // ====== Détail par mois ======
    @Schema(description = "Détail mois par mois")
    private List<DetailSoldeMoisDTO> detailsParMois;

    // ====== Statistiques ======
    @Schema(description = "Nombre de mois avec solde disponible", example = "3")
    private Integer nombreMoisDisponibles;

    @Schema(description = "Nombre de mois clôturés", example = "1")
    private Integer nombreMoisClotures;

    @Schema(description = "Prochain mois à épuiser", example = "Février 2025")
    private String prochainMoisFifo;

    @Schema(description = "Jours disponibles dans le prochain mois FIFO", example = "1.0")
    private BigDecimal joursDisponiblesProchainMois;

    // ====== Alertes ======
    @Schema(description = "Alerte solde faible (< 5 jours)", example = "false")
    private Boolean alerteSoldeFaible;

    @Schema(description = "Alerte solde épuisé", example = "false")
    private Boolean alerteSoldeEpuise;

    @Schema(description = "Message d'alerte éventuel", example = "Votre solde est inférieur à 5 jours")
    private String messageAlerte;
}