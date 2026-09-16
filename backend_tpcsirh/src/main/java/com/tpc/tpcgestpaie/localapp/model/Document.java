package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // référence interne
    private String reference;

    // titre
    private String title;

    @Lob
    @Column(nullable = true)
    private String description;

    // fichier
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;

    // relation
    private Long employeeId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private DocumentsCategory category;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private DocumentModel model;

    // dates
    private LocalDate signatureDate;
    private LocalDate validFrom;
    private LocalDate validTo;

    @Column(nullable = true)
    private Boolean isScanned;

    /**
     * Indique si ce document doit apparaître
     * sur la fiche de l'employé
     */
    @Column(name = "show_on_employee_profile")
    private Boolean showOnEmployeeProfile = false;

    // audit
    private Long companyId;

    @Column(name = "created_by")
    private Long createdBy;

    // =============================
    // Timestamps
    // =============================

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
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

    // =============================
    // Soft delete
    // =============================

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deletedAt = null;
    }

}