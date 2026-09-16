package com.tpc.tpcgestpaie.localapp.model.conge;

import com.tpc.tpcgestpaie.localapp.model.Employe;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_solde_conge")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueSoldeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;


    @Column(name = "company_id")
    private Long companyId;


    @Column(name = "date_evenement", nullable = false)
    private LocalDate dateEvenement;        // Date de l'événement

    @Enumerated(EnumType.STRING)
    @Column(name = "type_evenement", nullable = false, length = 30)
    private TypeEvenementSolde typeEvenement;

    // Détail de l'événement
    @Column(name = "description", length = 500)
    private String description;

    // Mouvements
    @Column(name = "jours_entree", precision = 10, scale = 2)
    private BigDecimal joursEntree;         // + (acquis, saisi...)

    @Column(name = "jours_sortie", precision = 10, scale = 2)
    private BigDecimal joursSortie;         // - (pris, ajusté...)

    @Column(name = "montant_entree", precision = 15, scale = 2)
    private BigDecimal montantEntree;

    @Column(name = "montant_sortie", precision = 15, scale = 2)
    private BigDecimal montantSortie;

    // Soldes après opération
    @Column(name = "solde_jours_apres", precision = 10, scale = 2, nullable = false)
    private BigDecimal soldeJoursApres;

    @Column(name = "solde_montant_apres", precision = 15, scale = 2, nullable = false)
    private BigDecimal soldeMontantApres;

    // Références
    @Column(name = "reference_id")          // ID de la demande, provision, etc.
    private Long referenceId;

    @Column(name = "reference_type", length = 50) // "DEMANDE_ABSENCE", "PROVISION", "SAISIE_INITIALE"
    private String referenceType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    public enum TypeEvenementSolde {
        SAISIE_INITIALE,        // Solde initial migré
        PROVISION_MENSUELLE,    // Calcul mensuel
        ACQUISITION_ANNUELLE,   // 2.5 jours/mois ou règle annuelle
        CONSOMMATION,           // Congé pris
        ANNULATION_CONSOMMATION, // Demande annulée
        AJUSTEMENT_MANUEL,      // Correction admin
        REPORT_SOLDE,           // Report année N-1
        REGULARISATION          // Régularisation fin de contrat
    }
}