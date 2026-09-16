package com.tpc.tpcgestpaie.localapp.model.conge;

import com.tpc.tpcgestpaie.localapp.enums.TypeRepartition;
import com.tpc.tpcgestpaie.localapp.model.absence.DemandeAbsence;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "repartition_consommations",
        indexes = {
                @Index(name = "idx_rep_demande", columnList = "demande_absence_id"),
                @Index(name = "idx_rep_provision", columnList = "provision_conge_id"),
                @Index(name = "idx_rep_date", columnList = "date_consommation")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepartitionConsommation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_absence_id")
    private DemandeAbsence demandeAbsence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provision_conge_id", nullable = false)
    private ProvisionConge provisionConge;

    @Column(name = "jours_consommes", precision = 5, scale = 2, nullable = false)
    private BigDecimal joursConsommes;

    @Column(name = "montant_consomme", precision = 15, scale = 2, nullable = false)
    private BigDecimal montantConsomme;

    @Column(name = "valeur_jour_calculee", precision = 15, scale = 4)
    private BigDecimal valeurJourCalculee;

    @CreationTimestamp
    @Column(name = "date_consommation", updatable = false)
    private LocalDateTime dateConsommation;

    @Column(name = "est_annule")
    @Builder.Default
    private Boolean estAnnule = false;

    @Column(name = "date_annulation")
    private LocalDateTime dateAnnulation;

    @Column(name = "motif_annulation", length = 500)
    private String motifAnnulation;

    @Column(name = "annulation_par_demande_id")
    private Long annulationParDemandeId;

    // 🆕 NOUVEAU : Type de répartition
    @Enumerated(EnumType.STRING)
    @Column(name = "type_repartition", length = 30)
    @Builder.Default
    private TypeRepartition typeRepartition = TypeRepartition.NORMALE;

    // 🆕 NOUVEAU : Commentaire
    @Column(name = "commentaire", length = 500)
    private String commentaire;

    public void annuler(String motif, Long demandeAnnulationId) {
        this.estAnnule = true;
        this.dateAnnulation = LocalDateTime.now();
        this.motifAnnulation = motif;
        this.annulationParDemandeId = demandeAnnulationId;
    }

    public boolean estActive() {
        return !Boolean.TRUE.equals(estAnnule);
    }
}