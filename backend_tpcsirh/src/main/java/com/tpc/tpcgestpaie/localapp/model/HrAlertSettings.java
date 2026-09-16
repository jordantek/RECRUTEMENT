package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted_at IS NULL") // filtre soft delete
@Table(name = "hr_alert_settings")
public class HrAlertSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Configuration globale si NULL
     * Configuration spécifique à une entreprise si renseigné
     */
    @Column(nullable = true)
    private Long companyId;

    /**
     * Configuration spécifique à un utilisateur si renseigné
     */
    @Column(nullable = true)
    private Long userId;

    /**
     * Nombre de jours pour les événements à venir
     */
    @Column(nullable = false)
    private Integer upcomingEventsDays = 15;

    /**
     * Nombre de jours pour les actions requises
     */
    @Column(nullable = false)
    private Integer requiredActionsDays = 7;

    /**
     * Activer / désactiver les alertes
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * Date de création
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false,name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Dernière mise à jour
     */
    @UpdateTimestamp
    @Column(nullable = false,name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Soft delete
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Soft delete
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deletedAt = null;
    }
}
