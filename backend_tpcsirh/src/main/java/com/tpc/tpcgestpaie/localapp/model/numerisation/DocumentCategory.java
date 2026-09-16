package com.tpc.tpcgestpaie.localapp.model.numerisation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tpc.tpcgestpaie.localapp.model.Company;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "document_categories")
@Data
public class DocumentCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TOUJOURS lié au client propriétaire
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private Company client;

    // OPTIONNEL : si null = catégorie partagée, si renseigné = catégorie spécifique
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = true)
    @JsonIgnore
    private Company company;

    @Column(nullable = false)
    private String name;

    private String description;
    private Integer displayOrder = 0;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DocumentSubCategory> subCategories = new ArrayList<>();

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

    // MÉTHODES UTILITAIRES
    public boolean isShared() {
        return this.company == null;
    }

    public boolean isCompanySpecific() {
        return this.company != null;
    }

    public Company getEffectiveCompany() {
        return isCompanySpecific() ? this.company : this.client;
    }
}