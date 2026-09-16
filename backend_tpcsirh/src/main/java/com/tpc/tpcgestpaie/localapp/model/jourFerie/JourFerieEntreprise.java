package com.tpc.tpcgestpaie.localapp.model.jourFerie;

import com.tpc.tpcgestpaie.localapp.enums.StatutJourFerieEntreprise;
import com.tpc.tpcgestpaie.localapp.model.Company;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant les jours fériés spécifiques à une entreprise
 * - ADD : jour férié personnalisé ajouté
 * - REMOVE : jour férié national retiré
 */
@Entity
@Table(name = "jours_feries_entreprise",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"company_id", "slug", "date_ferie"}
        ))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourFerieEntreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Entreprise concernée
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /**
     * Référence au jour férié national (optionnel)
     * Null si c'est un jour férié personnalisé (statut ADD)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jour_ferie_id")
    private JourFerie jourFerie;

    /**
     * Statut : ADD (ajouté) ou REMOVE (retiré)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 10)
    private StatutJourFerieEntreprise statut;

    // ========================================
    // COPIE DES ATTRIBUTS DE JourFerie
    // ========================================

    /**
     * Code unique du jour férié (ex: JOUR_AN, PAQUES, FETE_TRAVAIL)
     */
    @Column(name = "slug", length = 50)
    private String slug;

    /**
     * Libellé du jour férié
     */
    @Column(name = "libelle", nullable = false, length = 255)
    private String libelle;

    /**
     * Date du jour férié
     */
    @Column(name = "date_ferie", nullable = false)
    private LocalDate dateFerie;

    /**
     * Code pays ISO (ex: BJ, FR, CI, TG, SN)
     */
    @Column(name = "pays", nullable = false, length = 3)
    private String pays;

    /**
     * Est-ce un jour férié fixe ?
     */
    @Column(name = "est_fixe", nullable = false)
    private Boolean estFixe = true;

    /**
     * Est-ce un jour férié récurrent ?
     */
    @Column(name = "est_recurrent", nullable = false)
    private Boolean estRecurrent = false;

    /**
     * Date de création
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de mise à jour
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Utilisateur ayant créé l'enregistrement
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * Utilisateur ayant modifié l'enregistrement
     */
    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * Soft delete
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Vérifie si ce jour férié tombe un weekend
     */
    public boolean tombeEnWeekend() {
        if (dateFerie == null) return false;
        return dateFerie.getDayOfWeek().getValue() >= 6;
    }

    /**
     * Obtient le jour de la semaine en français
     */
    public String getJourSemaine() {
        if (dateFerie == null) return "";
        return switch (dateFerie.getDayOfWeek()) {
            case MONDAY -> "Lundi";
            case TUESDAY -> "Mardi";
            case WEDNESDAY -> "Mercredi";
            case THURSDAY -> "Jeudi";
            case FRIDAY -> "Vendredi";
            case SATURDAY -> "Samedi";
            case SUNDAY -> "Dimanche";
        };
    }
}