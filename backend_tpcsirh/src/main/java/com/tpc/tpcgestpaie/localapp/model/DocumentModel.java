package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "deleted_at IS NULL")
@Table(name = "documents_model")
public class DocumentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String code;
    private String description;
    @Column(name = "category_id", nullable = false)
    private Long categoryId;
    @Column(name = "category_name")
    private String categoryName;

    // =============================
    // Options du document
    // =============================

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "ocr_enabled")
    private Boolean ocrEnabled = false;

    @Column(name = "ai_extraction")
    private Boolean aiExtraction = false;

    /**
     * Indique si ce document doit apparaître
     * sur la fiche de l'employé
     */
    @Column(name = "show_on_employee_profile")
    private Boolean showOnEmployeeProfile = true;

    @Column(name = "company_id")
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