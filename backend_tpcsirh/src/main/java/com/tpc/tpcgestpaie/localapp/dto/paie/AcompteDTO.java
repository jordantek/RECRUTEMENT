package com.tpc.tpcgestpaie.localapp.dto.paie;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.*;

import java.math.BigDecimal;
import java.time.YearMonth;

public class AcompteDTO {

    private Long id;

    private Long contratEmployeId;
    private Long employeId;

    private EmployeDTO employe;
    private Long companyId;

    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth mois;
    private String observation;

    private BigDecimal montant;

    private BigDecimal salaireNet;

    private BigDecimal solde;

    private Long addedById;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContratEmployeId() {
        return contratEmployeId;
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

    public EmployeDTO getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeDTO employe) {
        this.employe = employe;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public YearMonth getMois() {
        return mois;
    }

    public void setMois(YearMonth mois) {
        this.mois = mois;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Long getAddedById() {
        return addedById;
    }

    public void setAddedById(Long addedById) {
        this.addedById = addedById;
    }

    public BigDecimal getSalaireNet() {
        return salaireNet;
    }

    public void setSalaireNet(BigDecimal salaireNet) {
        this.salaireNet = salaireNet;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public void setSolde(BigDecimal solde) {
        this.solde = solde;
    }

    // --- Conversion depuis l'entité ---
    public static AcompteDTO fromEntity(Acompte entity) {
        AcompteDTO dto = new AcompteDTO();
        dto.setId(entity.getId());
        dto.setContratEmployeId(entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null);
        dto.setEmployeId(entity.getEmploye() != null ? entity.getEmploye().getId() : null);
        dto.setEmploye(convertEmployeToDTO(entity.getEmploye()));

        dto.setCompanyId(entity.getCompany() != null ? entity.getCompany().getId() : null);
        dto.setMois(entity.getMoisAsYearMonth());
        dto.setMontant(entity.getMontant());
        dto.setSalaireNet(entity.getSalaireNet());
        dto.setSolde(entity.getSolde());
        dto.setObservation(entity.getObservation());
        dto.setAddedById(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        return dto;
    }

    // --- Conversion vers l'entité ---
    public Acompte toEntity(
            ContratEmploye contratEmploye,
            Employe employe,
            Company company,
            User addedBy
    ) {
        Acompte entity = new Acompte();
        entity.setId(this.id);
        entity.setContratEmploye(contratEmploye);
        entity.setEmploye(employe);
        entity.setCompany(company);
        entity.setMoisFromYearMonth(this.mois);
        entity.setMontant(this.montant);
        entity.setSalaireNet(this.salaireNet);
        entity.setSolde(this.solde);
        entity.setObservation(observation);
        entity.setAdded_by(addedBy);
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
