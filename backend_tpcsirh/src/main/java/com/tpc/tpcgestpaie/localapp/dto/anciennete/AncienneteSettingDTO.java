package com.tpc.tpcgestpaie.localapp.dto.anciennete;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.anciennete.AncienneteSetting;

import java.time.LocalDateTime;

public class AncienneteSettingDTO {

    private Long id;
    private Long companyId;
    private String companyName;
    private Integer nombreMoisEcart;
    private Boolean payeAnciennete;
    private Long addedById;
    private String addedByUsername;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    // Constructeurs
    public AncienneteSettingDTO() {}

    public AncienneteSettingDTO(Long companyId, Integer nombreMoisEcart, Boolean payeAnciennete) {
        this.companyId = companyId;
        this.nombreMoisEcart = nombreMoisEcart;
        this.payeAnciennete = payeAnciennete;
    }

    // Méthodes de conversion
    public static AncienneteSettingDTO fromEntity(AncienneteSetting entity) {
        AncienneteSettingDTO dto = new AncienneteSettingDTO();
        dto.setId(entity.getId());
        dto.setCompanyId(entity.getCompany() != null ? entity.getCompany().getId() : null);
        dto.setCompanyName(entity.getCompany() != null ? entity.getCompany().getName() : null);
        dto.setNombreMoisEcart(entity.getNombreMoisEcart());
        dto.setPayeAnciennete(entity.getPayeAnciennete());
        dto.setAddedById(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        dto.setAddedByUsername(entity.getAdded_by() != null ? entity.getAdded_by().getUsername() : null);
        dto.setCreated_at(entity.getCreated_at());
        dto.setUpdated_at(entity.getUpdated_at());
        return dto;
    }

    public AncienneteSetting toEntity(
            Company company,
            User addedBy
    ) {
        AncienneteSetting entity = new AncienneteSetting();
        entity.setId(this.id);
        entity.setCompany(company);
        entity.setNombreMoisEcart(this.nombreMoisEcart);
        entity.setPayeAnciennete(this.payeAnciennete != null ? this.payeAnciennete : false);
        entity.setAdded_by(addedBy);
        return entity;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getNombreMoisEcart() {
        return nombreMoisEcart;
    }

    public void setNombreMoisEcart(Integer nombreMoisEcart) {
        this.nombreMoisEcart = nombreMoisEcart;
    }

    public Boolean getPayeAnciennete() {
        return payeAnciennete;
    }

    public void setPayeAnciennete(Boolean payeAnciennete) {
        this.payeAnciennete = payeAnciennete;
    }

    public Long getAddedById() {
        return addedById;
    }

    public void setAddedById(Long addedById) {
        this.addedById = addedById;
    }

    public String getAddedByUsername() {
        return addedByUsername;
    }

    public void setAddedByUsername(String addedByUsername) {
        this.addedByUsername = addedByUsername;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}