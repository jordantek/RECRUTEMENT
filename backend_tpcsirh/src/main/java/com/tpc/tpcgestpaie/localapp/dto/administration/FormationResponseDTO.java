package com.tpc.tpcgestpaie.localapp.dto.administration;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormationResponseDTO {
    private Long id;
    private String theme;
    private String description;
    private String lieu;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer duree;
    private Long companyId;
    private Long addedById;
    private Long updatedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<EmployeNomPrenomDTO> employes;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Long getAddedById() { return addedById; }
    public void setAddedById(Long addedById) { this.addedById = addedById; }

    public Long getUpdatedById() { return updatedById; }
    public void setUpdatedById(Long updatedById) { this.updatedById = updatedById; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<EmployeNomPrenomDTO> getEmployes() { return employes; }
    public void setEmployes(List<EmployeNomPrenomDTO> employes) { this.employes = employes; }
}
