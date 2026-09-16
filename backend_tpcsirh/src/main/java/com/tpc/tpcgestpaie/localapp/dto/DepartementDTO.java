package com.tpc.tpcgestpaie.localapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpc.tpcgestpaie.localapp.model.Departement;

import java.time.LocalDateTime;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartementDTO {

    private Long id;
    private String libelle;
    private String description;
    private Long companyId;
    private Long addedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeur
    public DepartementDTO(Long id, String libelle, String description, Long companyId, Long addedById, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.libelle = libelle;
        this.description = description;
        this.companyId = companyId;
        this.addedById = addedById;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public DepartementDTO(Long id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getAddedById() {
        return addedById;
    }

    public void setAddedById(Long addedById) {
        this.addedById = addedById;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Méthode de conversion d'entité en DTO

    public static DepartementDTO fromEntity1(Departement d) {
        if (d == null) return null;
        return new DepartementDTO(d.getId(), d.getLibelle());
    }


    public static DepartementDTO fromEntity(Departement departement) {
        return new DepartementDTO(
                departement.getId(),
                departement.getLibelle(),
                departement.getDescription(),
                departement.getCompany_id(),
                departement.getAdded_by() != null ? departement.getAdded_by().getId() : null,
                departement.getCreatedAt(),
                departement.getUpdatedAt()
        );
    }
}
