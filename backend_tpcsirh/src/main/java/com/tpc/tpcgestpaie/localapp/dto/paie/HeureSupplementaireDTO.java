package com.tpc.tpcgestpaie.localapp.dto.paie;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpc.tpcgestpaie.localapp.dto.UserDTO;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HeureSupplementaireDTO {

    private Long id;
    private LocalDate date;
    private String mois;

    private int salaireBaseContrat;
    private int salaireBrutContrat;

    private double heures12;
    private double heures35;
    private double heures50;
    private double heures100;

    private double majoration12;
    private double majoration35;
    private double majoration50;
    private double majoration100;

    private double totalHeures;

    private double primeAnciennete;
    private BigDecimal montant;
    private String observation;

    private Long contratEmployeId;
    private ContractEmployeDTO contratEmploye;

    private Long employeId;
    private EmployeDTO employe;

    private Long companyId;
    private CompanyDTO company;

    private Long lastUpdateUserId;
    private UserDTO lastUpdateUser;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getMois() { return mois; }
    public void setMois(String mois) { this.mois = mois; }

    public int getSalaireBaseContrat() { return salaireBaseContrat; }
    public void setSalaireBaseContrat(int salaireBaseContrat) { this.salaireBaseContrat = salaireBaseContrat; }

    public int getSalaireBrutContrat() { return salaireBrutContrat; }
    public void setSalaireBrutContrat(int salaireBrutContrat) { this.salaireBrutContrat = salaireBrutContrat; }

    public double getHeures12() { return heures12; }
    public void setHeures12(double heures12) { this.heures12 = heures12; }

    public double getHeures35() { return heures35; }
    public void setHeures35(double heures35) { this.heures35 = heures35; }

    public double getHeures50() { return heures50; }
    public void setHeures50(double heures50) { this.heures50 = heures50; }

    public double getHeures100() { return heures100; }
    public void setHeures100(double heures100) { this.heures100 = heures100; }

    public double getMajoration12() { return majoration12; }
    public void setMajoration12(double majoration12) { this.majoration12 = majoration12; }

    public double getMajoration35() { return majoration35; }
    public void setMajoration35(double majoration35) { this.majoration35 = majoration35; }

    public double getMajoration50() { return majoration50; }
    public void setMajoration50(double majoration50) { this.majoration50 = majoration50; }

    public double getMajoration100() { return majoration100; }
    public void setMajoration100(double majoration100) { this.majoration100 = majoration100; }

    public double getTotalHeures() { return totalHeures; }
    public void setTotalHeures(double totalHeures) { this.totalHeures = totalHeures; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }

    public Long getContratEmployeId() { return contratEmployeId; }
    public void setContratEmployeId(Long contratEmployeId) { this.contratEmployeId = contratEmployeId; }

    public ContractEmployeDTO getContratEmploye() { return contratEmploye; }
    public void setContratEmploye(ContractEmployeDTO contratEmploye) { this.contratEmploye = contratEmploye; }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public EmployeDTO getEmploye() { return employe; }
    public void setEmploye(EmployeDTO employe) { this.employe = employe; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public CompanyDTO getCompany() { return company; }
    public void setCompany(CompanyDTO company) { this.company = company; }

    public Long getLastUpdateUserId() { return lastUpdateUserId; }
    public void setLastUpdateUserId(Long lastUpdateUserId) { this.lastUpdateUserId = lastUpdateUserId; }

    public UserDTO getLastUpdateUser() { return lastUpdateUser; }
    public void setLastUpdateUser(UserDTO lastUpdateUser) { this.lastUpdateUser = lastUpdateUser; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public double getPrimeAnciennete() {
        return primeAnciennete;
    }

    public void setPrimeAnciennete(double primeAnciennete) {
        this.primeAnciennete = primeAnciennete;
    }
}
