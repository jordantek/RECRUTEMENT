package com.tpc.tpcgestpaie.localapp.model.jourFerie;

import com.tpc.tpcgestpaie.localapp.enums.TypeJourFerie;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant un jour férié (national, mobile ou personnalisé)
 */
@Entity
@Table(name = "jours_feries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourFerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Code unique du jour férié (ex: JOUR_AN, PAQUES, FETE_TRAVAIL)
     * Utile pour identifier les jours fériés récurrents
     */
    @Column(name = "slug", length = 50) // ⚠️ Enlevé unique = true
    private String slug;

    /**
     * Libellé du jour férié (ex: "Jour de l'An", "Fête du Travail")
     */
    @Column(name = "libelle", nullable = false, length = 255)
    private String libelle;

    /**
     * Date du jour férié
     */
    @Column(name = "date_ferie", nullable = false)
    private LocalDate dateFerie;

    // 🌍 AJOUT DU PAYS
    /**
     * Code pays ISO (ex: BJ, FR, CI, TG, SN)
     */
    @Column(name = "pays", nullable = false, length = 3)
    private String pays = "";

    public void setPays(String pays) {
        this.pays = (pays == null) ? "" : pays.toUpperCase();
    }

   /**
     * Est-ce un jour férié mobile (comme Pâques) ?
     */
    @Column(name = "est_fixe", nullable = false)
    private Boolean estFixe = true;

    /**
     * Est-ce un jour férié récurrent (chaque année) ?
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
        return dateFerie.getDayOfWeek().getValue() >= 6; // 6=samedi, 7=dimanche
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