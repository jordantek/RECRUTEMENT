package com.tpc.tpcgestpaie.localapp.model.conge;

import com.tpc.tpcgestpaie.localapp.enums.ProvisionCongeStatut;
import com.tpc.tpcgestpaie.localapp.enums.TypeProvision;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "provision_conges",
        indexes = {
                @Index(name = "idx_prov_employe_mois", columnList = "employe_id, mois_reference"),
                @Index(name = "idx_prov_statut", columnList = "statut"),
                @Index(name = "idx_prov_type", columnList = "type_provision")
        },
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"employe_id", "mois_reference"},
                name = "uk_provision_employe_mois"
        ))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvisionConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "mois_reference", nullable = false, length = 7)
    private String moisReference;

    @Column(name = "jours_acquis", precision = 5, scale = 2, nullable = false)
    private BigDecimal joursAcquis;

    @Column(name = "salaire_brut_mois", precision = 15, scale = 2)
    private BigDecimal salaireBrutMois;

    @Column(name = "jours_travailles_mois", precision = 5, scale = 2)
    private BigDecimal joursTravaillesMois;

    @Column(name = "provision_mensuelle", precision = 15, scale = 2)
    private BigDecimal provisionMensuelle;

    @Column(name = "jours_consommes", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal joursConsommes = BigDecimal.ZERO;

    @Column(name = "montant_consomme", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal montantConsomme = BigDecimal.ZERO;

    @Column(name = "jours_restant", precision = 5, scale = 2)
    private BigDecimal joursRestant;

    @Column(name = "solde_financier", precision = 15, scale = 2)
    private BigDecimal soldeFinancier;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 30)
    @Builder.Default
    private ProvisionCongeStatut statut = ProvisionCongeStatut.ACTIF;

    // 🆕 NOUVEAU : Type de provision
    @Enumerated(EnumType.STRING)
    @Column(name = "type_provision", nullable = false, length = 30)
    @Builder.Default
    private TypeProvision typeProvision = TypeProvision.NORMALE;

    // 🆕 NOUVEAU : Verrouillage (pour provisions initiales non modifiables)
    @Column(name = "est_verrouille")
    @Builder.Default
    private Boolean estVerrouille = false;

    // 🆕 NOUVEAU : Commentaire pour traçabilité
    @Column(name = "commentaire_initial", length = 1000)
    private String commentaireInitial;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void calculerSoldes() {
        if (joursConsommes == null) joursConsommes = BigDecimal.ZERO;
        if (montantConsomme == null) montantConsomme = BigDecimal.ZERO;

        this.joursRestant = joursAcquis.subtract(joursConsommes);
        this.soldeFinancier = provisionMensuelle.subtract(montantConsomme);

        if (joursRestant.compareTo(BigDecimal.ZERO) == 0) {
            this.statut = ProvisionCongeStatut.CLOTURE;
        } else if (joursConsommes.compareTo(BigDecimal.ZERO) > 0) {
            this.statut = ProvisionCongeStatut.PARTIELLEMENT_CONSOMME;
        } else {
            this.statut = ProvisionCongeStatut.ACTIF;
        }
    }

    public boolean peutConsommer() {
        return joursRestant.compareTo(BigDecimal.ZERO) > 0
                && statut != ProvisionCongeStatut.ANNULE;
    }

    public void consommer(BigDecimal jours, BigDecimal montant) {
        this.joursConsommes = this.joursConsommes.add(jours);
        this.montantConsomme = this.montantConsomme.add(montant);
    }

    public void restaurer(BigDecimal jours, BigDecimal montant) {
        this.joursConsommes = this.joursConsommes.subtract(jours);
        this.montantConsomme = this.montantConsomme.subtract(montant);

        if (this.joursConsommes.compareTo(BigDecimal.ZERO) < 0) {
            this.joursConsommes = BigDecimal.ZERO;
        }
        if (this.montantConsomme.compareTo(BigDecimal.ZERO) < 0) {
            this.montantConsomme = BigDecimal.ZERO;
        }
    }

    @Transient
    public YearMonth getMoisReferenceAsYearMonth() {
        return YearMonth.parse(moisReference);
    }

    public boolean estProvisionInitiale() {
        return this.typeProvision == TypeProvision.SYNTHESE_INITIALE
                || this.typeProvision == TypeProvision.SYNTHESE_AVEC_HISTORIQUE;
    }
}