package com.tpc.tpcgestpaie.localapp.model.numerisation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "document_sub_categories")
@Data
public class DocumentSubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private DocumentCategory category;

    @Column(nullable = false)
    private String name;

    private String description;
    private Boolean isMandatory = false;
    private Integer retentionPeriod; // en mois
    private Integer displayOrder = 0;

    @OneToMany(mappedBy = "subCategory")
    @JsonIgnore
    private List<EmployeeDocument> documents = new ArrayList<>();

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