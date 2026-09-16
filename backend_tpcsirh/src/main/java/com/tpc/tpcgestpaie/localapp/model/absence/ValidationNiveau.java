package com.tpc.tpcgestpaie.localapp.model.absence;

import com.tpc.tpcgestpaie.localapp.config.ConditionAcceptationCongeConverter;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "validation_niveaux")
@Data
public class ValidationNiveau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_absence_id")
    private DemandeAbsence demandeAbsence;

    @Column(name = "ordre_niveau")
    private Integer ordreNiveau; // 1, 2, 3...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validateur_id")
    private Employe validateur;

    @Column(name = "fonction_validateur")
    private String fonctionValidateur;

    @Column(name = "statut")
    private String statut; // "EN_ATTENTE", "APPROUVE", "REJETE"

    @Column(name = "date_action")
    private LocalDateTime dateAction;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "raison_rejet", columnDefinition = "TEXT")
    private String raisonRejet;

    // ✅ Convertisseur personnalisé pour gérer null/vide
    @Column(name = "conditions_acceptation", length = 50, nullable = true)
    private String conditionAcceptationConge;

    // Méthode utilitaire pour récupérer l'enum
    public GlobalEnums.ConditionAcceptationConge getConditionAcceptationEnum() {
        if (conditionAcceptationConge == null) return null;
        try {
            return GlobalEnums.ConditionAcceptationConge.valueOf(conditionAcceptationConge);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}