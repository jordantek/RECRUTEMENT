package com.tpc.tpcgestpaie.localapp.model.jourFerie;

import com.tpc.tpcgestpaie.localapp.model.Company;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Historique des modifications des jours fériés
 * Permet de tracer toutes les actions effectuées sur les jours fériés
 */
@Entity
@Table(name = "jours_feries_historiques",
        indexes = {
                @Index(name = "idx_jfh_company", columnList = "company_id"),
                @Index(name = "idx_jfh_jour_ferie_entreprise", columnList = "jour_ferie_entreprise_id"),
                @Index(name = "idx_jfh_date_modification", columnList = "date_modification"),
                @Index(name = "idx_jfh_type_action", columnList = "type_action")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourFerieHistorique {
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
     * Association jour férié - entreprise concernée
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jour_ferie_entreprise_id")
    private JourFerieEntreprise jourFerieEntreprise;

    /**
     * Type d'action effectuée
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type_action", nullable = false, length = 30)
    private TypeActionJourFerie typeAction;

    /**
     * Description de l'action
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Valeur avant modification (JSON)
     */
    @Column(name = "valeur_avant", columnDefinition = "TEXT")
    private String valeurAvant;

    /**
     * Valeur après modification (JSON)
     */
    @Column(name = "valeur_apres", columnDefinition = "TEXT")
    private String valeurApres;

    /**
     * Date de la modification
     */
    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    /**
     * Utilisateur ayant effectué la modification
     */
    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    /**
     * Adresse IP de l'utilisateur
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * User agent (navigateur)
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @PrePersist
    protected void onCreate() {
        if (dateModification == null) {
            dateModification = LocalDateTime.now();
        }
    }

    /**
     * Types d'actions possibles
     */
    public enum TypeActionJourFerie {
        CREATION("Création d'un jour férié"),
        MODIFICATION("Modification d'un jour férié"),
        SUPPRESSION("Suppression d'un jour férié"),
        ACTIVATION("Activation du jour férié"),
        DESACTIVATION("Désactivation du jour férié"),
        REPORT("Report du jour férié"),
        GENERATION_AUTO("Génération automatique des jours fériés");

        private final String libelle;

        TypeActionJourFerie(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }
}