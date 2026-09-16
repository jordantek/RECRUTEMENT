package com.tpc.tpcgestpaie.localapp.dto.paie;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.UserDTO;
import com.tpc.tpcgestpaie.localapp.model.Institution;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MensualiteDTO {
    private Long id;

    private int dureeMensualite;
    private String moisDemarrage;
    private String moisFin;
    private BigDecimal montantMensuel;
    private boolean mensualiteSolde;

    private String statut;
    private LocalDateTime dateSoldee;
    private String motifAnnulation;

    private Long companyId;
    private CompanyDTO company;

    private Long institutionId;
    private Institution institution;

    private Long contratEmployeId;
    private ContractEmployeDTO contratEmploye;

    private Long employeId;
    private EmployeDTO employe;

    private Long lastUpdateUserId;
    private UserDTO lastUpdateUser;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;



    // Getters + Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDureeMensualite() {
        return dureeMensualite;
    }

    public void setDureeMensualite(int dureeMensualite) {
        this.dureeMensualite = dureeMensualite;
    }

    public String getMoisDemarrage() {
        return moisDemarrage;
    }

    public void setMoisDemarrage(String moisDemarrage) {
        this.moisDemarrage = moisDemarrage;
    }

    public String getMoisFin() {
        return moisFin;
    }

    public void setMoisFin(String moisFin) {
        this.moisFin = moisFin;
    }

    public BigDecimal getMontantMensuel() {
        return montantMensuel;
    }

    public void setMontantMensuel(BigDecimal montantMensuel) {
        this.montantMensuel = montantMensuel;
    }

    public boolean isMensualiteSolde() {
        return mensualiteSolde;
    }

    public void setMensualiteSolde(boolean mensualiteSolde) {
        this.mensualiteSolde = mensualiteSolde;
    }


    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }


    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public ContractEmployeDTO getContratEmploye() {
        return contratEmploye;
    }

    public void setContratEmploye(ContractEmployeDTO contratEmploye) {
        this.contratEmploye = contratEmploye;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public EmployeDTO getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeDTO employe) {
        this.employe = employe;
    }

    public Long getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setLastUpdateUserId(Long lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    public UserDTO getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(UserDTO lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateSoldee() {
        return dateSoldee;
    }

    public void setDateSoldee(LocalDateTime dateSoldee) {
        this.dateSoldee = dateSoldee;
    }

    public String getMotifAnnulation() {
        return motifAnnulation;
    }

    public void setMotifAnnulation(String motifAnnulation) {
        this.motifAnnulation = motifAnnulation;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
