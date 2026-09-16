package com.tpc.tpcgestpaie.localapp.dto;

import java.math.BigDecimal;

public class MontantParContratDTO {
    private Long contratId;
    private Long employeId;
    private String nomEmploye;
    private String prenomEmploye;
    private BigDecimal montantTotal;
    private BigDecimal montantAjoutTotal;

    public MontantParContratDTO(Long contratId, Long employeId, String nomEmploye, String prenomEmploye,
                                BigDecimal montantTotal, BigDecimal montantAjoutTotal) {
        this.contratId = contratId;
        this.employeId = employeId;
        this.nomEmploye = nomEmploye;
        this.prenomEmploye = prenomEmploye;
        this.montantTotal = montantTotal;
        this.montantAjoutTotal = montantAjoutTotal;
    }

    public Long getContratId() {
        return contratId;
    }

    public void setContratId(Long contratId) {
        this.contratId = contratId;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public String getNomEmploye() {
        return nomEmploye;
    }

    public void setNomEmploye(String nomEmploye) {
        this.nomEmploye = nomEmploye;
    }

    public String getPrenomEmploye() {
        return prenomEmploye;
    }

    public void setPrenomEmploye(String prenomEmploye) {
        this.prenomEmploye = prenomEmploye;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public BigDecimal getMontantAjoutTotal() {
        return montantAjoutTotal;
    }

    public void setMontantAjoutTotal(BigDecimal montantAjoutTotal) {
        this.montantAjoutTotal = montantAjoutTotal;
    }

    // Getters et setters (ou records en Java 16+)
}
