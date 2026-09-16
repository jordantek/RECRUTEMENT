package com.tpc.tpcgestpaie.localapp.dto.conge.historique;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Historique complet d'une demande de congé")
public class HistoriqueDemandeDTO {

    // ====== Identité ======
    @Schema(description = "ID de la demande", example = "1001")
    private Long demandeId;

    @Schema(description = "ID employé", example = "42")
    private Long employeId;

    @Schema(description = "Nom employé", example = "KARIM Benali")
    private String nomEmploye;

    @Schema(description = "Matricule", example = "EMP001")
    private String matricule;

    // ====== Période ======
    @Schema(description = "Date début prévue", example = "2025-03-10")
    private LocalDate dateDebut;

    @Schema(description = "Date fin prévue", example = "2025-03-14")
    private LocalDate dateFin;

    @Schema(description = "Date début effective", example = "2025-03-10")
    private LocalDate dateDebutEffective;

    @Schema(description = "Date fin effective", example = "2025-03-14")
    private LocalDate dateFinEffective;

    @Schema(description = "Jours demandés", example = "5")
    private Integer nombreJoursDemandes;

    @Schema(description = "Jours effectivement déduits", example = "4.0")
    private BigDecimal joursEffectifsDeduits;

    // ====== Statut ======
    @Schema(description = "Statut actuel", example = "EN_COURS")
    private String statut;

    @Schema(description = "Solde a été déduit ?", example = "true")
    private Boolean soldeDeduit;

    @Schema(description = "Date de déduction", example = "2025-03-10")
    private LocalDate dateDeduction;

    @Schema(description = "Date de confirmation de départ", example = "2025-03-10T08:30:00")
    private LocalDateTime dateConfirmationDepart;

    // ====== Financier ======
    @Schema(description = "Montant total alloué (€)", example = "410.00")
    private BigDecimal montantTotalAllocation;

    @Schema(description = "Répartitions actives")
    private List<RepartitionDetailDTO> repartitionsActives;

    @Schema(description = "Historique des annulations éventuelles")
    private List<RepartitionAnnuleeDTO> historiqueAnnulations;

    // ====== Audit ======
    @Schema(description = "Date création demande")
    private LocalDateTime dateCreation;

    @Schema(description = "Date dernière modification")
    private LocalDateTime dateModification;

    @Schema(description = "Créée par", example = "RH001")
    private String creePar;

    // ====== Chronologie ======
    @Schema(description = "Étapes de la demande")
    private List<EtapeDemandeDTO> chronologie;
}