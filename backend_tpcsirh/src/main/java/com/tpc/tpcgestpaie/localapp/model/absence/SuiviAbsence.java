package com.tpc.tpcgestpaie.localapp.model.absence;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "suivi_absences")
@Data
public class SuiviAbsence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "demande_absence_id", unique = true)
    private DemandeAbsence demandeAbsence;

    // ================= DÉPART =================
    @Column(name = "date_depart_prevue")
    private LocalDate dateDepartPrevue;

    @Column(name = "heure_depart_prevue")
    private LocalTime heureDepartPrevue; // Nouvelle colonne pour heure prévue

    @Column(name = "date_depart_effective")
    private LocalDate dateDepartEffective;

    @Column(name = "heure_depart_effective")
    private LocalTime heureDepartEffective; // Nouvelle colonne pour heure effective

    @Column(name = "date_notification_depart")
    private LocalDateTime dateNotificationDepart;

    @Column(name = "date_confirmation_depart")
    private LocalDateTime dateConfirmationDepart;

    @Column(name = "depart_confirme")
    private Boolean departConfirme = false;

    @Column(name = "commentaire_depart", columnDefinition = "TEXT")
    private String commentaireDepart;

    // ================= RETOUR =================
    @Column(name = "date_retour_prevue")
    private LocalDate dateRetourPrevue;

    @Column(name = "heure_arrivee_prevue")
    private LocalTime heureArriveePrevue; // Nouvelle colonne pour heure prévue

    @Column(name = "date_retour_effective")
    private LocalDate dateRetourEffective;

    @Column(name = "heure_arrivee_effective")
    private LocalTime heureArriveeEffective; // Nouvelle colonne pour heure effective

    @Column(name = "date_notification_retour")
    private LocalDateTime dateNotificationRetour;

    @Column(name = "date_confirmation_retour")
    private LocalDateTime dateConfirmationRetour;

    @Column(name = "retour_confirme")
    private Boolean retourConfirme = false;

    @Column(name = "commentaire_retour", columnDefinition = "TEXT")
    private String commentaireRetour;

    // ================= CALCULS =================
    @Column(name = "jours_prevu")
    private Integer joursPrevu;

    @Column(name = "heures_prevu")
    private Integer heuresPrevu; // Nouvelle colonne pour heures prévues

    @Column(name = "jours_effectifs")
    private Integer joursEffectifs;

    @Column(name = "heures_effectifs")
    private Integer heuresEffectifs; // Nouvelle colonne pour heures effectives

    @Column(name = "ecart_jours")
    private Integer ecartJours; // Différence entre prévu et effectif

    @Column(name = "ecart_heures")
    private Integer ecartHeures; // Différence heures

    // ================= REPORT =================
    @Column(name = "a_ete_reporte")
    private Boolean aEteReporte = false;

    @Column(name = "motif_report", columnDefinition = "TEXT")
    private String motifReport;

    @Column(name = "date_demande_report")
    private LocalDateTime dateDemandeReport;

    // ================= JUSTIFICATIFS =================
    @Column(name = "justificatif_retard", columnDefinition = "TEXT")
    private String justificatifRetard;

    @Column(name = "justificatif_avance", columnDefinition = "TEXT")
    private String justificatifAvance;

    // ================= MÉTADONNÉES =================
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "jours_calendaires_effectifs")
    private Integer joursCalendairesEffectifs;  // Jours calendaires entre départ et retour effectifs

    @Column(name = "jours_feries_effectifs")
    private Integer joursFeriesEffectifs;       // Jours fériés exclus du calcul effectif

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
