package com.tpc.tpcgestpaie.localapp.model.numerisation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_templates")
@Data
public class DocumentTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    @JsonIgnore
    private DocumentSubCategory subCategory;

    private String templateName;
    private String templatePath;
    private Boolean isActive = true;

    // ⚠️ RETIRER TEMPORAIREMENT le champ problématique
    // @JdbcTypeCode(SqlTypes.JSON)
    // @Column(columnDefinition = "json")
    // private List<QRCodeZone> qrCodeZones = new ArrayList<>();

    // Ajouter des champs simples pour les positions QR
    private Integer qrPage = 1;
    private Float qrPositionX;
    private Float qrPositionY;
    private Float qrWidth = 80f;
    private Float qrHeight = 80f;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

