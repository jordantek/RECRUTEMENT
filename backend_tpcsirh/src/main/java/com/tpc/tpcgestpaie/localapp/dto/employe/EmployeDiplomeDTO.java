package com.tpc.tpcgestpaie.localapp.dto.employe;

import com.tpc.tpcgestpaie.localapp.model.Diplome;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.EmployeDiplome;
import com.tpc.tpcgestpaie.localapp.model.User;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeDiplomeDTO {

    private Long id;

    private Long employeId;
    private Long diplomeId;

    private String libelle;
    private String anneeObtention;
    private String denomination;

    private Long addedById;
    private Long updatedById;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static EmployeDiplomeDTO fromEntity(EmployeDiplome entity) {
        if (entity == null) return null;

        EmployeDiplomeDTO dto = new EmployeDiplomeDTO();

        dto.setId(entity.getId());
        dto.setLibelle(entity.getDiplome().getName());
        dto.setEmployeId(entity.getEmploye() != null ? entity.getEmploye().getId() : null);
        dto.setDiplomeId(entity.getDiplome() != null ? entity.getDiplome().getId() : null);
        dto.setAnneeObtention(entity.getAnnee_obtention());
        dto.setDenomination(entity.getDenomination());
        dto.setAddedById(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        dto.setUpdatedById(entity.getUpdated_by() != null ? entity.getUpdated_by().getId() : null);
        dto.setCreatedAt(entity.getCreated_at());
        dto.setUpdatedAt(entity.getUpdated_at());
        dto.setDeletedAt(entity.getDeleted_at());

        return dto;
    }

    public EmployeDiplome toEntity() {
        EmployeDiplome entity = new EmployeDiplome();

        entity.setId(this.id);

        if (this.employeId != null) {
            Employe employe = new Employe();
            employe.setId(this.employeId);
            entity.setEmploye(employe);
        }

        if (this.diplomeId != null) {
            Diplome diplome = new Diplome();
            diplome.setId(this.diplomeId);
            entity.setDiplome(diplome);
        }

        entity.setAnnee_obtention(this.anneeObtention);
        entity.setDenomination(this.denomination);

        if (this.addedById != null) {
            User user = new User();
            user.setId(this.addedById);
            entity.setAdded_by(user);
        }

        if (this.updatedById != null) {
            User user = new User();
            user.setId(this.updatedById);
            entity.setUpdated_by(user);
        }

        entity.setCreated_at(this.createdAt);
        entity.setUpdated_at(this.updatedAt);
        entity.setDeleted_at(this.deletedAt);

        return entity;
    }


    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public Long getDiplomeId() {
        return diplomeId;
    }

    public void setDiplomeId(Long diplomeId) {
        this.diplomeId = diplomeId;
    }

    public String getAnneeObtention() {
        return anneeObtention;
    }

    public void setAnneeObtention(String anneeObtention) {
        this.anneeObtention = anneeObtention;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public Long getAddedById() {
        return addedById;
    }

    public void setAddedById(Long addedById) {
        this.addedById = addedById;
    }

    public Long getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(Long updatedById) {
        this.updatedById = updatedById;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
