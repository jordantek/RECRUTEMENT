package com.tpc.tpcgestpaie.localapp.dto.conge.historique;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ConsommationMoisDTO {

    @Schema(description = "ID demande", example = "1001")
    private Long demandeId;

    @Schema(description = "Nom employé (si vue RH)", example = "KARIM Benali")
    private String employeNom;

    @Schema(description = "Période congé", example = "10/03/2025 - 14/03/2025")
    private String periodeConge;

    @Schema(description = "Date début congé", example = "2025-03-10")
    private LocalDate dateDebutConge;

    @Schema(description = "Jours pris sur ce mois", example = "2.5")
    private BigDecimal joursPris;

    @Schema(description = "Montant pris (€)", example = "250.00")
    private BigDecimal montantPris;

    @Schema(description = "Date consommation (confirmation départ)", example = "2025-03-10T08:30:00")
    private LocalDateTime dateConsommation;

    @Schema(description = "Statut demande", example = "EN_COURS")
    private String statutDemande;

    @Schema(description = "Est annulée ?", example = "false")
    private Boolean estAnnulee;
}