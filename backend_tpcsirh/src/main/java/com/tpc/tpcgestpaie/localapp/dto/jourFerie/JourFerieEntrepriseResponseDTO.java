package com.tpc.tpcgestpaie.localapp.dto.jourFerie;

import com.tpc.tpcgestpaie.localapp.enums.StatutJourFerieEntreprise;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour les jours fériés spécifiques à une entreprise
 * Utilise un constructeur pour le mapping direct depuis JPA
 */
@Data
@NoArgsConstructor
public class JourFerieEntrepriseResponseDTO {

    // ========================================
    // IDENTIFIANTS
    // ========================================
    private Long id;

    // ========================================
    // ENTREPRISE
    // ========================================
    private Long companyId;
    private String companyName;

    // ========================================
    // JOUR FÉRIÉ NATIONAL (si applicable)
    // ========================================
    private Long jourFerieId;

    // ========================================
    // STATUT ET INFORMATIONS PRINCIPALES
    // ========================================
    private StatutJourFerieEntreprise statut;
    private String statutLibelle; // "Ajouté" ou "Retiré"

    // ========================================
    // INFORMATIONS DU JOUR FÉRIÉ (copiées)
    // ========================================
    private String slug;
    private String libelle;
    private LocalDate dateFerie;
    private String pays;
    private Boolean estFixe;
    private Boolean estRecurrent;

    // ========================================
    // MÉTADONNÉES
    // ========================================
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private String createdByName;
    private String updatedByName;

    // ========================================
    // CHAMPS CALCULÉS / D'AFFICHAGE
    // ========================================
    private String jourSemaine;
    private Boolean tombeEnWeekend;
    private Integer annee;
    private Boolean estPersonnalise; // Vrai si statut = ADD
    private Boolean estNationalRetire; // Vrai si statut = REMOVE

    // ========================================
    // COMMENTAIRE / NOTES
    // ========================================
    private String commentaire;

    // ========================================
    // CONSTRUCTEUR POUR MAPPING DIRECT JPA
    // ========================================

    /**
     * Constructeur utilisé par JPA pour le mapping direct depuis la requête
     * Utilisable avec @Query et les projections Spring Data JPA
     *
     * Exemple d'utilisation dans le repository:
     * @Query("SELECT new com.tpc.tpcgestpaie.localapp.dto.jourFerie.JourFerieEntrepriseResponseDTO(" +
     *        "jfe.id, jfe.company.id, jfe.company.name, jfe.company.code, jfe.jourFerie.id, " +
     *        "jfe.statut, jfe.slug, jfe.libelle, jfe.dateFerie, jfe.pays, " +
     *        "jfe.estFixe, jfe.estRecurrent, jfe.createdAt, jfe.updatedAt, " +
     *        "jfe.createdBy, jfe.updatedBy, creator.fullName, updater.fullName, jfe.commentaire) " +
     *        "FROM JourFerieEntreprise jfe " +
     *        "LEFT JOIN User creator ON jfe.createdBy = creator.id " +
     *        "LEFT JOIN User updater ON jfe.updatedBy = updater.id " +
     *        "WHERE jfe.company.id = :companyId AND jfe.deletedAt IS NULL")
     */
    public JourFerieEntrepriseResponseDTO(
            Long id,
            Long companyId,
            String companyName,
            Long jourFerieId,
            StatutJourFerieEntreprise statut,
            String slug,
            String libelle,
            LocalDate dateFerie,
            String pays,
            Boolean estFixe,
            Boolean estRecurrent,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long createdBy,
            Long updatedBy,
            String createdByName,
            String updatedByName,
            String commentaire) {

        this.id = id;
        this.companyId = companyId;
        this.companyName = companyName;
        this.jourFerieId = jourFerieId;
        this.statut = statut;
        this.slug = slug;
        this.libelle = libelle;
        this.dateFerie = dateFerie;
        this.pays = pays;
        this.estFixe = estFixe;
        this.estRecurrent = estRecurrent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdByName = createdByName;
        this.updatedByName = updatedByName;
        this.commentaire = commentaire;

        // Calculer automatiquement les champs dérivés
        calculerChampsAdditionnels();
    }

    /**
     * Constructeur simplifié sans les noms d'utilisateurs
     */
    public JourFerieEntrepriseResponseDTO(
            Long id,
            Long companyId,
            String companyName,
            Long jourFerieId,
            StatutJourFerieEntreprise statut,
            String slug,
            String libelle,
            LocalDate dateFerie,
            String pays,
            Boolean estFixe,
            Boolean estRecurrent,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long createdBy,
            Long updatedBy) {

        this(id, companyId, companyName, jourFerieId, statut,
                slug, libelle, dateFerie, pays, estFixe, estRecurrent,
                createdAt, updatedAt, createdBy, updatedBy, null, null, null);
    }

    // ========================================
    // MÉTHODES UTILITAIRES PRIVÉES
    // ========================================

    /**
     * Calcule et remplit les champs additionnels
     */
    private void calculerChampsAdditionnels() {
        // Statut libellé
        this.statutLibelle = (statut == StatutJourFerieEntreprise.ADD) ? "Ajouté" : "Retiré";

        // Année
        if (dateFerie != null) {
            this.annee = dateFerie.getYear();
        }

        // Flags
        this.estPersonnalise = (statut == StatutJourFerieEntreprise.ADD);
        this.estNationalRetire = (statut == StatutJourFerieEntreprise.REMOVE);

        // Jour de la semaine
        if (dateFerie != null) {
            this.jourSemaine = getJourSemaineFromDate(dateFerie);
        }

        // Weekend
        if (dateFerie != null) {
            this.tombeEnWeekend = (dateFerie.getDayOfWeek().getValue() >= 6);
        }
    }

    /**
     * Retourne le jour de la semaine en français
     */
    private String getJourSemaineFromDate(LocalDate date) {
        if (date == null) return "";
        return switch (date.getDayOfWeek()) {
            case MONDAY -> "Lundi";
            case TUESDAY -> "Mardi";
            case WEDNESDAY -> "Mercredi";
            case THURSDAY -> "Jeudi";
            case FRIDAY -> "Vendredi";
            case SATURDAY -> "Samedi";
            case SUNDAY -> "Dimanche";
        };
    }

    // ========================================
    // MÉTHODES UTILITAIRES PUBLIQUES
    // ========================================

    /**
     * Vérifie si c'est un jour férié ajouté (personnalisé)
     */
    public boolean isJourPersonnalise() {
        return statut == StatutJourFerieEntreprise.ADD;
    }

    /**
     * Vérifie si c'est un jour férié national retiré
     */
    public boolean isJourNationalRetire() {
        return statut == StatutJourFerieEntreprise.REMOVE;
    }

    /**
     * Retourne le type de jour férié pour l'affichage
     */
    public String getTypeAffichage() {
        if (isJourPersonnalise()) {
            return "Personnalisé";
        } else if (isJourNationalRetire()) {
            return "National retiré";
        } else {
            return "National";
        }
    }

    /**
     * Retourne la couleur selon le statut (pour l'UI)
     */
    public String getCouleurStatut() {
        return switch (statut) {
            case ADD -> "green";
            case REMOVE -> "red";
            default -> "gray";
        };
    }

    /**
     * Retourne l'icône selon le statut (pour l'UI)
     */
    public String getIconeStatut() {
        return switch (statut) {
            case ADD -> "add_circle";
            case REMOVE -> "remove_circle";
            default -> "event";
        };
    }

    /**
     * Retourne la date à afficher
     */
    public LocalDate getDateAAfficher() {
        return dateFerie;
    }

    /**
     * Vérifie si le jour férié est récurrent
     */
    public boolean isRecurrent() {
        return Boolean.TRUE.equals(estRecurrent);
    }

    /**
     * Vérifie si le jour férié est fixe
     */
    public boolean isFixe() {
        return Boolean.TRUE.equals(estFixe);
    }

    /**
     * Retourne une description complète pour l'affichage
     */
    public String getDescriptionComplete() {
        StringBuilder sb = new StringBuilder();
        sb.append(libelle);

        if (isJourPersonnalise()) {
            sb.append(" (Jour férié personnalisé)");
        } else if (isJourNationalRetire()) {
            sb.append(" (Jour férié national retiré)");
        }

        if (commentaire != null && !commentaire.trim().isEmpty()) {
            sb.append(" - ").append(commentaire);
        }

        return sb.toString();
    }
}