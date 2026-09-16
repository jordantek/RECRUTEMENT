package com.tpc.tpcgestpaie.localapp.dto.administration;

import com.tpc.tpcgestpaie.localapp.model.Formation;
import com.tpc.tpcgestpaie.localapp.model.Employe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FormationDTO {

    private Long id;
    private String theme;
    private String description;
    private String lieu;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String duree;

    private Long companyId;
    private Long addedById;
    private Long updatedById;

    private List<Long> employeIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===========================================
    // Conversion entity -> DTO
    // ===========================================
    public static FormationDTO fromEntity(Formation formation) {
        if (formation == null) {
            return null;
        }

        FormationDTO dto = new FormationDTO();
        dto.setId(formation.getId());
        dto.setTheme(formation.getTheme());
        dto.setDescription(formation.getDescription());
        dto.setLieu(formation.getLieu());
        dto.setDateDebut(formation.getDateDebut());
        dto.setDateFin(formation.getDateFin());
        dto.setDuree(formation.getDuree());
        dto.setCreatedAt(formation.getCreatedAt());
        dto.setUpdatedAt(formation.getUpdatedAt());

        dto.setCompanyId(formation.getCompany() != null ? formation.getCompany().getId() : null);
        dto.setAddedById(formation.getAddedBy() != null ? formation.getAddedBy().getId() : null);
        dto.setUpdatedById(formation.getUpdated_by() != null ? formation.getUpdated_by().getId() : null);

        if (formation.getEmployes() != null) {
            dto.setEmployeIds(
                    formation.getEmployes().stream()
                            .map(Employe::getId)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    // ===========================================
    // Conversion DTO -> entity (sans associations complexes)
    // ===========================================
    public Formation toEntity() {
        Formation formation = new Formation();
        formation.setId(this.id);
        formation.setTheme(this.theme);
        formation.setDescription(this.description);
        formation.setLieu(this.lieu);
        formation.setDateDebut(this.dateDebut);
        formation.setDateFin(this.dateFin);
        formation.setDuree(this.duree);
        formation.setCreatedAt(this.createdAt);
        formation.setUpdatedAt(this.updatedAt);

        // ⚠ Les associations (company, addedBy, employes) doivent être gérées dans le service !

        return formation;
    }

    // ===========================================
    // Getters / Setters
    // ===========================================

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

    public String getDuree() { return duree; }
    public void setDuree(String duree) { this.duree = duree; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Long getAddedById() { return addedById; }
    public void setAddedById(Long addedById) { this.addedById = addedById; }

    public Long getUpdatedById() { return updatedById; }
    public void setUpdatedById(Long updatedById) { this.updatedById = updatedById; }

    public List<Long> getEmployeIds() { return employeIds; }
    public void setEmployeIds(List<Long> employeIds) { this.employeIds = employeIds; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
