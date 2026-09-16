package com.tpc.tpcgestpaie.localapp.model.conge;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_consommation_conge")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueConsommationConge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employe_id", nullable = false)
    private Long employeId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    // Référence à la demande d'absence
    @Column(name = "demande_absence_id", nullable = false)
    private Long demandeAbsenceId;

    // Référence à la table absence
    @Column(name = "absence_id", nullable = false)
    private Long absenceId;

    // Date de la consommation
    @Column(name = "date_consommation", nullable = false)
    private LocalDate dateConsommation;

    // Total de la demande
    @Column(name = "jours_demandes_total", nullable = false)
    private BigDecimal joursDemandesTotal;

    @Column(name = "montant_total_alloue", nullable = false)
    private BigDecimal montantTotalAlloue;

    // Détail par provision (mois) - stocké en JSON ou ligne séparée
    // Option 1: Ligne par mois consommé
    @Column(name = "provision_annee_mois", nullable = false)
    private String provisionAnneeMois;  // ex: "2024-01"

    @Column(name = "jours_pris_sur_ce_mois", nullable = false)
    private BigDecimal joursPrisSurCeMois;

    @Column(name = "montant_pris_sur_ce_mois", nullable = false)
    private BigDecimal montantPrisSurCeMois;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}