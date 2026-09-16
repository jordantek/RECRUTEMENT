package com.tpc.tpcgestpaie.localapp.dto.paie;

import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProvisionCongeDTO {

    private Long id;
    private String moisProvisionConge;
    private Long contratEmployeId;

    private ContractEmployeDTO contractEmployeDTO;
    private Long employeId;
    private EmployeDTO employeDTO;
  // Si tu veux afficher un nom par exemple
    private Long companyId;
    private CompanyDTO companyDTO;// Si utile
    private Long creditCongeId;

    private double nombreJourAccorde;

    private BigDecimal montantProvisionConge;
    private Long lastUpdateUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeur vide
    public ProvisionCongeDTO() {
    }

    // Constructeur complet (facultatif si tu utilises un mapper ou builder)
    public ProvisionCongeDTO(Long id, String moisProvisionConge, Long contratEmployeId, Long employeId, String employeNom,
                             Long companyId, String companyName, Long creditCongeId, double nombreJourAccorde,
                             BigDecimal montantProvisionConge, Long lastUpdateUserId,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.moisProvisionConge = moisProvisionConge;
        this.contratEmployeId = contratEmployeId;
        this.employeId = employeId;
        this.companyId = companyId;
        this.creditCongeId = creditCongeId;
        this.nombreJourAccorde = nombreJourAccorde;
        this.montantProvisionConge = montantProvisionConge;
        this.lastUpdateUserId = lastUpdateUserId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters et Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMoisProvisionConge() { return moisProvisionConge; }
    public void setMoisProvisionConge(String moisProvisionConge) { this.moisProvisionConge = moisProvisionConge; }

    public Long getContratEmployeId() { return contratEmployeId; }
    public void setContratEmployeId(Long contratEmployeId) { this.contratEmployeId = contratEmployeId; }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public ContractEmployeDTO getContractEmployeDTO() {
        return contractEmployeDTO;
    }

    public void setContractEmployeDTO(ContractEmployeDTO contractEmployeDTO) {
        this.contractEmployeDTO = contractEmployeDTO;
    }

    public EmployeDTO getEmployeDTO() {
        return employeDTO;
    }

    public void setEmployeDTO(EmployeDTO employeDTO) {
        this.employeDTO = employeDTO;
    }

    public CompanyDTO getCompanyDTO() {
        return companyDTO;
    }

    public void setCompanyDTO(CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
    }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }


    public Long getCreditCongeId() { return creditCongeId; }
    public void setCreditCongeId(Long creditCongeId) { this.creditCongeId = creditCongeId; }

    public double getNombreJourAccorde() { return nombreJourAccorde; }
    public void setNombreJourAccorde(double nombreJourAccorde) { this.nombreJourAccorde = nombreJourAccorde; }

    public BigDecimal getMontantProvisionConge() { return montantProvisionConge; }
    public void setMontantProvisionConge(BigDecimal montantProvisionConge) { this.montantProvisionConge = montantProvisionConge; }

    public Long getLastUpdateUserId() { return lastUpdateUserId; }
    public void setLastUpdateUserId(Long lastUpdateUserId) { this.lastUpdateUserId = lastUpdateUserId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
