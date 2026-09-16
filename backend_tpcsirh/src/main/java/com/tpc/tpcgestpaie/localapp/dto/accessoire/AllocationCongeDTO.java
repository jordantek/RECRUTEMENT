package com.tpc.tpcgestpaie.localapp.dto.accessoire;

import java.math.BigDecimal;

public class AllocationCongeDTO {
    private Long id;
    private String nomEmploye;
    private String mois; // format "yyyy-MM"
    private BigDecimal montantTotal;
    private BigDecimal salaireJournalier;
    private Long nbJours;

    public AllocationCongeDTO(Long id, String nomEmploye, String mois, BigDecimal montantTotal, BigDecimal salaireJournalier, Long nbJours) {
        this.id = id;
        this.nomEmploye = nomEmploye;
        this.mois = mois;
        this.montantTotal = montantTotal;
        this.salaireJournalier = salaireJournalier;
        this.nbJours = nbJours;
    }

    // Getters et setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomEmploye() {
        return nomEmploye;
    }

    public void setNomEmploye(String nomEmploye) {
        this.nomEmploye = nomEmploye;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public BigDecimal getSalaireJournalier() {
        return salaireJournalier;
    }

    public void setSalaireJournalier(BigDecimal salaireJournalier) {
        this.salaireJournalier = salaireJournalier;
    }

    public Long getNbJours() {
        return nbJours;
    }

    public void setNbJours(Long nbJours) {
        this.nbJours = nbJours;
    }
}
