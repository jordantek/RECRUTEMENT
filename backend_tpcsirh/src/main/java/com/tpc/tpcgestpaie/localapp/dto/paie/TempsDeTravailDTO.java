package com.tpc.tpcgestpaie.localapp.dto.paie;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TempsDeTravailDTO {


    private Long id;
    private Long employeId;
    private EmployeDTO employe;
    private Long contratEmployeId;
    private ContractEmployeDTO contratEmploye;
    private Long companyId;
    private CompanyDTO company;
    private LocalDate date;
    private String mois;
    private double nombreJour;


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
    public EmployeDTO getEmploye() {
        return employe;
    }
    public void setEmploye(EmployeDTO employe) {
        this.employe = employe;
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
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getMois() {
        return mois;
    }
    public void setMois(String mois) {
        this.mois = mois;
    }
    public double getNombreJour() {
        return nombreJour;
    }
    public void setNombreJour(double nombreJour) {
        this.nombreJour = nombreJour;
    }
    @Override
    public String toString() {
        return "TempsDeTravailDTO{" +
                "employeId=" + employeId +
                ", nombreJour=" + nombreJour +
                ", mois='" + mois + '\'' +
                '}';
    }

}
