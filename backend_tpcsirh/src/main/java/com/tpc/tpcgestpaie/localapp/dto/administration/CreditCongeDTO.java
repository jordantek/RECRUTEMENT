package com.tpc.tpcgestpaie.localapp.dto.administration;

import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreditCongeDTO {

    private Long id;
    private Long contratEmployeId;
    private Long employeId;

    private EmployeDTO employeDTO;
    private Long companyId;
    private LocalDate dateReference;
    private String statut;
    private Long addedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public EmployeDTO getEmployeDTO() {
        return employeDTO;
    }

    public void setEmployeDTO(EmployeDTO employeDTO) {
        this.employeDTO = employeDTO;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }


    public LocalDate getDateReference() {
        return dateReference;
    }

    public void setDateReference(LocalDate dateReference) {
        this.dateReference = dateReference;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
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


    // Getters & setters...

    // Conversion from entity to DTO
    public static CreditCongeDTO fromEntity(CreditConge entity) {
        if (entity == null) return null;

        CreditCongeDTO dto = new CreditCongeDTO();
        dto.setId(entity.getId());
        dto.setContratEmployeId(entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null);
        dto.setEmployeId(entity.getEmploye() != null ? entity.getEmploye().getId() : null);
        dto.setEmployeDTO(convertEmployeToDTO(entity.getEmploye()));
        dto.setCompanyId(entity.getCompany() != null ? entity.getCompany().getId() : null);
        dto.setDateReference(entity.getDateReference());
        dto.setStatut(entity.getStatut());
        dto.setAddedById(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        dto.setCreatedAt(entity.getCreated_at());
        dto.setUpdatedAt(entity.getUpdated_at());
        return dto;
    }

    // Conversion from DTO to entity
    public CreditConge toEntity(
            ContratEmploye contratEmploye,
            Employe employe,
            Company company,
            User addedBy
    ) {
        CreditConge entity = new CreditConge();
        entity.setId(this.id);
        entity.setContratEmploye(contratEmploye);
        entity.setEmploye(employe);
        entity.setCompany(company);
        entity.setDateReference(this.dateReference);
        entity.setStatut(this.statut);
        entity.setAdded_by(addedBy);
        entity.setCreated_at(this.createdAt);
        entity.setUpdated_at(this.updatedAt);
        return entity;
    }

    private static EmployeDTO convertEmployeToDTO(Employe employe) {
        if (employe == null) return null;

        EmployeDTO dto = new EmployeDTO();
        dto.setId(employe.getId());
        dto.setNom(employe.getNom());
        dto.setPrenom(employe.getPrenom());
        return dto;
    }

}
