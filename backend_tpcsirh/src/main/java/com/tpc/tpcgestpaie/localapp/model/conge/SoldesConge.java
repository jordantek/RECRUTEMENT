package com.tpc.tpcgestpaie.localapp.model.conge;

import com.tpc.tpcgestpaie.localapp.model.Employe;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "soldes_conge")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoldesConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id", nullable = false, unique = true)
    private Employe employe;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    // Solde courant (jours)
    @Column(name = "solde_total_jours", precision = 10, scale = 2, nullable = false)
    private BigDecimal soldeTotalJours;

    @Column(name = "solde_consomme_jours", precision = 10, scale = 2, nullable = false)
    private BigDecimal soldeConsommeJours;

    @Column(name = "solde_restant_jours", precision = 10, scale = 2, nullable = false)
    private BigDecimal soldeRestantJours;

    // Montant total des provisions
    @Column(name = "montant_total_provisions", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantTotalProvisions;

    @Column(name = "montant_consomme", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantConsomme;

    @Column(name = "montant_restant", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantRestant;

    // Date de référence
    @Column(name = "date_reference", nullable = false)
    private LocalDate dateReference;

    // Pour les migrations
    @Column(name = "solde_initial_saisi", precision = 10, scale = 2)
    private BigDecimal soldeInitialSaisi;

    @Column(name = "date_saisie_initial")
    private LocalDateTime dateSaisieInitial;

    // 🆕 NOUVEAUX CHAMPS pour congés déjà pris
    @Column(name = "conges_deja_pris_jours", precision = 10, scale = 2)
    private BigDecimal congesDejaPrisJours;

    @Column(name = "date_dernier_conge_pris")
    private LocalDate dateDernierCongePris;

    @Column(name = "nb_mois_calcules")
    private Integer nbMoisCalcules;
    // 🆕 CORRECTION: Supprime @Column(name = "...") pour les timestamps
    // Hibernate gère automatiquement le nom de colonne
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}