package com.tpc.tpcgestpaie.localapp.dto.administration;

import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SanctionDTO {
    private Long id;
    private Long employeId;
    private EmployeDTO employe;

    private Long contratEmployeId;
    private ContratEmploye contratEmploye;
    private Long companyId;
    private CompanyDTO company;

    private LocalDate datePlainte;
    private String contenuePlainte;
    private LocalDate dateDemandeExplication;
    private LocalDate dateReponse;
    private String sanctionDonnee;
    private String observation;

    private Long addedById;
    private LocalDateTime createdAt;
    private Long updatedById;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Getters & Setters


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

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public EmployeDTO getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeDTO employe) {
        this.employe = employe;
    }

    public ContratEmploye getContratEmploye() {
        return contratEmploye;
    }

    public void setContratEmploye(ContratEmploye contratEmploye) {
        this.contratEmploye = contratEmploye;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public LocalDate getDatePlainte() {
        return datePlainte;
    }

    public void setDatePlainte(LocalDate datePlainte) {
        this.datePlainte = datePlainte;
    }

    public String getContenuePlainte() {
        return contenuePlainte;
    }

    public void setContenuePlainte(String contenuePlainte) {
        this.contenuePlainte = contenuePlainte;
    }

    public LocalDate getDateDemandeExplication() {
        return dateDemandeExplication;
    }

    public void setDateDemandeExplication(LocalDate dateDemandeExplication) {
        this.dateDemandeExplication = dateDemandeExplication;
    }

    public LocalDate getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(LocalDate dateReponse) {
        this.dateReponse = dateReponse;
    }

    public String getSanctionDonnee() {
        return sanctionDonnee;
    }

    public void setSanctionDonnee(String sanctionDonnee) {
        this.sanctionDonnee = sanctionDonnee;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
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

    public Long getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(Long updatedById) {
        this.updatedById = updatedById;
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
