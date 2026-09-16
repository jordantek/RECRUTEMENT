package com.tpc.tpcgestpaie.localapp.model.absence;

import com.tpc.tpcgestpaie.localapp.model.Employe;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_demande_absences")
public class NotificationDemandeAbsence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_absence_id")
    private DemandeAbsence demandeAbsence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id")
    private Employe destinataire;

    private String typeNotification; // VALIDATION_N1, VALIDATION_N2, REJET, etc.
    private String message;
    private boolean lu = false;
    private LocalDateTime dateEnvoi;
    private LocalDateTime dateLecture;

    @PrePersist
    protected void onCreate() {
        dateEnvoi = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DemandeAbsence getDemandeAbsence() { return demandeAbsence; }
    public void setDemandeAbsence(DemandeAbsence demandeAbsence) { this.demandeAbsence = demandeAbsence; }

    public Employe getDestinataire() { return destinataire; }
    public void setDestinataire(Employe destinataire) { this.destinataire = destinataire; }

    public String getTypeNotification() { return typeNotification; }
    public void setTypeNotification(String typeNotification) { this.typeNotification = typeNotification; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public LocalDateTime getDateLecture() { return dateLecture; }
    public void setDateLecture(LocalDateTime dateLecture) { this.dateLecture = dateLecture; }
}