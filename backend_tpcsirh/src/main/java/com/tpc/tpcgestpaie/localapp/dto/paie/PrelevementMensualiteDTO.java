package com.tpc.tpcgestpaie.localapp.dto.paie;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpc.tpcgestpaie.localapp.dto.DepartementDTO;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.Institution;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrelevementMensualiteDTO {


    private EmployeDTO convertEmployeToDTO(Employe employe) {
        if (employe == null) return null;

        EmployeDTO dto = new EmployeDTO();
        dto.setId(employe.getId());
        dto.setNom(employe.getNom());
        dto.setPrenom(employe.getPrenom());
        return dto;
    }

    private CompanyDTO convertCompanyToDTO(Company company) {
        if (company == null) return null;

        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        return dto;
    }

    private ContractEmployeDTO convertContratEmployeToDTO(ContratEmploye contratEmploye) {
        if (contratEmploye == null) return null;

        ContractEmployeDTO dto = new ContractEmployeDTO();
        dto.setId(contratEmploye.getId());
     dto.setDepartement(contratEmploye.getDepartement());
        dto.setDepartementDTO(DepartementDTO.fromEntity1(contratEmploye.getDepartement()));
        return dto;
    }



    private Long id;
    private LocalDate datePrelevement;
    private String moisPrelevement;
    private BigDecimal montant;

    private Long employeId;
    private EmployeDTO employe;

    private String employeNomComplet;
    private String institutionNom;

    private Long contratEmployeId;
    private ContractEmployeDTO contratEmploye;

    private Long companyId;
    private CompanyDTO company;

    private Long institutionId;
    private Institution institution;

    private Long mensualiteId;
    private MensualiteDTO mensualite;

    private Long lastUpdateUserId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters et Setters


    public String getEmployeNomComplet() {
        return employeNomComplet;
    }

    public void setEmployeNomComplet(String employeNomComplet) {
        this.employeNomComplet = employeNomComplet;
    }

    public String getInstitutionNom() {
        return institutionNom;
    }

    public void setInstitutionNom(String institutionNom) {
        this.institutionNom = institutionNom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDatePrelevement() {
        return datePrelevement;
    }

    public void setDatePrelevement(LocalDate datePrelevement) {
        this.datePrelevement = datePrelevement;
    }

    public String getMoisPrelevement() {
        return moisPrelevement;
    }

    public void setMoisPrelevement(String moisPrelevement) {
        this.moisPrelevement = moisPrelevement;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
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

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public Long getMensualiteId() {
        return mensualiteId;
    }

    public void setMensualiteId(Long mensualiteId) {
        this.mensualiteId = mensualiteId;
    }

    public Long getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setLastUpdateUserId(Long lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
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

    public EmployeDTO getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeDTO employe) {
        this.employe = employe;
    }

    public ContractEmployeDTO getContratEmploye() {
        return contratEmploye;
    }

    public void setContratEmploye(ContractEmployeDTO contratEmploye) {
        this.contratEmploye = contratEmploye;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public MensualiteDTO getMensualite() {
        return mensualite;
    }

    public void setMensualite(MensualiteDTO mensualite) {
        this.mensualite = mensualite;
    }
}
