package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted_at IS NULL") // filtre soft delete
public class HrEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type d'événement RH
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_type_id", nullable = false)
    private HrEventType eventType;
    private String type; // ex: "BIRTHDAY_EMPLOYEE", "CONTRACT_ANNIVERSARY", "CONTRACT_END"

    private LocalDate eventDate; // date de base (jour + mois, année facultative)
    private boolean recurring; // true = anniversaire récurrent, false = événement unique

    @ManyToOne
    private Employe employee;

    @ManyToOne
    private ContratEmploye contratEmploye;

    @ManyToOne
    private Company company;
    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "title",nullable = true)
    private  String title;
    @Lob
    @Column(name = "description",nullable = true)
    private String description;

    @Builder.Default
    @Column(nullable = false,name = "action_required")
    private Boolean actionRequired = false;

    // Timestamps
    @Column(nullable = false, updatable = false,name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false,name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // =============================
    // Gestion automatique des dates
    // =============================
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
