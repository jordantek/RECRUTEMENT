package com.tpc.tpcgestpaie.localapp.dto.accessoire;

import java.math.BigDecimal;
import java.util.List;

public class HistoriqueMontantsDTO {
    private List<MontantMensuelDTO> montantsMensuels;
    private BigDecimal montantTotal;
    private BigDecimal montantMoyen;
    private EmployeInfoDTO employe;
    private String typeLicencement;
    private String moisCalculSalaire;
    private BigDecimal indemniteSelonAnciennete;
    private double anciennete;

    public HistoriqueMontantsDTO(List<MontantMensuelDTO> montantsMensuels, BigDecimal montantTotal, BigDecimal montantMoyen, EmployeInfoDTO employe,String typeLicencement, String moisCalculSalaire) {
        this.montantsMensuels = montantsMensuels;
        this.montantTotal = montantTotal;
        this.montantMoyen = montantMoyen;
        this.employe = employe;
        this.typeLicencement = typeLicencement;
        this.moisCalculSalaire = moisCalculSalaire;
    }

    // Getters et Setters
    public String getMoisCalculSalaire() {
        return moisCalculSalaire;
    }
    public void setMoisCalculSalaire(String moisCalculSalaire) {
        this.moisCalculSalaire = moisCalculSalaire;
    }
    public List<MontantMensuelDTO> getMontantsMensuels() {
        return montantsMensuels;
    }
    public void setMontantsMensuels(List<MontantMensuelDTO> montantsMensuels) {
        this.montantsMensuels = montantsMensuels;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public BigDecimal getMontantMoyen() {
        return montantMoyen;
    }

    public void setMontantMoyen(BigDecimal montantMoyen) {
        this.montantMoyen = montantMoyen;
    }

    public EmployeInfoDTO getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeInfoDTO employe) {
        this.employe = employe;
    }

    public double getAnciennete() {
        return anciennete;
    }

    public void setAnciennete(double anciennete) {
        this.anciennete = anciennete;
    }

    public BigDecimal getIndemniteSelonAnciennete() {
        return indemniteSelonAnciennete;
    }

    public void setIndemniteSelonAnciennete(BigDecimal indemniteSelonAnciennete) {
        this.indemniteSelonAnciennete = indemniteSelonAnciennete;
    }

    public String getTypeLicencement() {
        return typeLicencement;
    }

    public void setTypeLicencement(String typeLicencement) {
        this.typeLicencement = typeLicencement;
    }
}
