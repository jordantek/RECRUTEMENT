package com.tpc.tpcgestpaie.localapp.dto.accessoire;

import java.math.BigDecimal;
import java.util.List;

public class ResultatCongeDTO {

    private double totalTemps;
    private BigDecimal totalSalaire;
    private BigDecimal salaireJournalierProvisoire;
    private BigDecimal salaireJournalierNormal;
    private long nbJours;
    private BigDecimal montantTotal;
    private String typeOperation;
    private List<BulletinResumeDTO> bulletinsUtilises;
    private String moisCalculSalaire;

    private EmployeInfoDTO employe;

    public ResultatCongeDTO(double totalTemps, BigDecimal totalSalaire,
                            BigDecimal salaireJournalierProvisoire, BigDecimal salaireJournalierNormal,
                            long nbJours, BigDecimal montantTotal, String typeOperation, List<BulletinResumeDTO> bulletinsDTO, EmployeInfoDTO employeInfos, String  moisCalculSalaire) {
        this.totalTemps = totalTemps;
        this.totalSalaire = totalSalaire;
        this.salaireJournalierProvisoire = salaireJournalierProvisoire;
        this.salaireJournalierNormal = salaireJournalierNormal;
        this.nbJours = nbJours;
        this.montantTotal = montantTotal;
        this.typeOperation = typeOperation;
        this.bulletinsUtilises = bulletinsDTO;
        this.employe = employeInfos;
        this.moisCalculSalaire = moisCalculSalaire;
    }

    public String getMoisCalculSalaire() {
        return moisCalculSalaire;
    }

    public void setMoisCalculSalaire(String moisCalculSalaire) {
        this.moisCalculSalaire = moisCalculSalaire;
    }

    public ResultatCongeDTO() {
    }
    // Getters et setters (ou utilise Lombok si tu préfères)
    public double getTotalTemps() {
        return totalTemps;
    }

    public void setTotalTemps(double totalTemps) {
        this.totalTemps = totalTemps;
    }

    public BigDecimal getTotalSalaire() {
        return totalSalaire;
    }

    public void setTotalSalaire(BigDecimal totalSalaire) {
        this.totalSalaire = totalSalaire;
    }

    public BigDecimal getSalaireJournalierProvisoire() {
        return salaireJournalierProvisoire;
    }

    public void setSalaireJournalierProvisoire(BigDecimal salaireJournalierProvisoire) {
        this.salaireJournalierProvisoire = salaireJournalierProvisoire;
    }

    public BigDecimal getSalaireJournalierNormal() {
        return salaireJournalierNormal;
    }

    public void setSalaireJournalierNormal(BigDecimal salaireJournalierNormal) {
        this.salaireJournalierNormal = salaireJournalierNormal;
    }

    public long getNbJours() {
        return nbJours;
    }

    public void setNbJours(long nbJours) {
        this.nbJours = nbJours;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getTypeOperation() {
        return typeOperation;
    }

    public void setTypeOperation(String typeOperation) {
        this.typeOperation = typeOperation;
    }

    public List<BulletinResumeDTO> getBulletinsUtilises() {
        return bulletinsUtilises;
    }

    public void setBulletinsUtilises(List<BulletinResumeDTO> bulletinsUtilises) {
        this.bulletinsUtilises = bulletinsUtilises;
    }

    public EmployeInfoDTO getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeInfoDTO employe) {
        this.employe = employe;
    }
}
