package com.tpc.tpcgestpaie.localapp.model.conge;

import com.tpc.tpcgestpaie.localapp.model.Employe;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "provision_conge_mensuelle",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employe_id", "annee_mois"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvisionCongeMensuelle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    // Clé métier : Année-Mois (ex: 2024-01)
    @Column(name = "annee_mois", nullable = false, length = 7)
    private String anneeMois;               // Format: YYYY-MM

    // Données de calcul
    @Column(name = "salaire_brut_mois", nullable = false)
    private BigDecimal salaireBrutMois;

    @Column(name = "jours_travailles_mois", nullable = false)
    private BigDecimal joursTravaillesMois;  // Normalement 26 ou 30

    @Column(name = "jours_conge_acquis_mois", nullable = false)
    private BigDecimal joursCongeAcquisMois; // 2 ou 2.5 selon convention

    @Column(name = "montant_provision", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantProvision;    // Règle de trois: (salaire/jours_travailles) * jours_conge_acquis

    // Cumuls depuis date référence
    @Column(name = "cumul_jours_acquis", nullable = false)
    private BigDecimal cumulJoursAcquis;

    @Column(name = "cumul_montant_provisions", nullable = false)
    private BigDecimal cumulMontantProvisions;

    // Solde avant consommation du mois
    @Column(name = "solde_jours_pre_conso", nullable = false)
    private BigDecimal soldeJoursPreConso;

    @Column(name = "solde_montant_pre_conso", nullable = false)
    private BigDecimal soldeMontantPreConso;

    // Consommations du mois
    @Column(name = "jours_consommes_mois")
    private BigDecimal joursConsommesMois;

    @Column(name = "montant_consomme_mois")
    private BigDecimal montantConsommeMois;

    // Solde final après consommation
    @Column(name = "solde_jours_final", nullable = false)
    private BigDecimal soldeJoursFinal;

    @Column(name = "solde_montant_final", nullable = false)
    private BigDecimal soldeMontantFinal;

    // Flag pour savoir si cette provision a été consommée (partiellement ou totalement)
    @Column(name = "est_consommee", nullable = false)
    @Builder.Default
    private Boolean estConsommee = false;

    @Column(name = "jours_restants_apres_conso")
    private BigDecimal joursRestantsApresConso;  // Si consommation partielle

    @Column(name = "montant_reste_apres_conso")
    private BigDecimal montantResteApresConso;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "calcul_par")
    private Long calculPar;
}