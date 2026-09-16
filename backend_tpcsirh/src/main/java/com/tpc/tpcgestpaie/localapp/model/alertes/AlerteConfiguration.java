package com.tpc.tpcgestpaie.localapp.model.alertes;

import com.tpc.tpcgestpaie.localapp.model.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "alerte_configurations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"type_alerte", "user_id"})
        })
@Data
public class AlerteConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_alerte", nullable = false)
    private TypeAlerte typeAlerte;

    @Column(nullable = false)
    private String libelle;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Échéances en jours (ex: "7,3,1" pour 7 jours, 3 jours, 1 jour avant)
    @Column(name = "echeances_jours", nullable = false)
    private String echeancesJours;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "notifier_jour_j", nullable = false)
    private boolean notifierJourJ = true;

    // Pour les absences seulement
    private String motifsImportants;

    // Référence directe à l'utilisateur/gestionnaire
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Méthodes utilitaires
    public List<Integer> getEcheancesJoursAsList() {
        if (echeancesJours == null || echeancesJours.trim().isEmpty()) {
            return List.of();
        }
        return List.of(echeancesJours.split(","))
                .stream()
                .map(String::trim)
                .map(Integer::parseInt)
                .sorted((a, b) -> Integer.compare(b, a)) // Tri décroissant
                .collect(Collectors.toList());
    }

    public List<String> getMotifsImportantsAsList() {
        if (motifsImportants == null || motifsImportants.trim().isEmpty()) {
            return List.of();
        }
        return List.of(motifsImportants.split(","))
                .stream()
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public enum TypeAlerte {
        ANNIVERSAIRE_NAISSANCE("Anniversaires de naissance"),
        ANNIVERSAIRE_RECRUTEMENT("Anniversaires de recrutement"),
        FIN_ESSAI("Fins de période d'essai"),
        FIN_CONTRAT("Fins de contrat"),
        DEBUT_ABSENCE("Départs en absence"),
        FIN_ABSENCE("Retours d'absence"),
        JOURNAL_RH("Journal RH");


        private final String libelle;

        TypeAlerte(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }
}