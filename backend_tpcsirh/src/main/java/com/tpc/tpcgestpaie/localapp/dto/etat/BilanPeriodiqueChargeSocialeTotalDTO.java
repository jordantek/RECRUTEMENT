package com.tpc.tpcgestpaie.localapp.dto.etat;

import java.math.BigDecimal;

public class BilanPeriodiqueChargeSocialeTotalDTO {
    private BigDecimal totalSalaire;
    private BigDecimal totalCnssEmploye;
    private BigDecimal totalCnssEmployeur;
    private BigDecimal totalCharges;

    public BilanPeriodiqueChargeSocialeTotalDTO(
            BigDecimal totalSalaire,
            BigDecimal totalCnssEmploye,
            BigDecimal totalCnssEmployeur,
            BigDecimal totalCharges
    ) {
        this.totalSalaire = totalSalaire;
        this.totalCnssEmploye = totalCnssEmploye;
        this.totalCnssEmployeur = totalCnssEmployeur;
        this.totalCharges = totalCharges;
    }

    // getters / setters


    public BigDecimal getTotalSalaire() {
        return totalSalaire;
    }

    public void setTotalSalaire(BigDecimal totalSalaire) {
        this.totalSalaire = totalSalaire;
    }

    public BigDecimal getTotalCnssEmploye() {
        return totalCnssEmploye;
    }

    public void setTotalCnssEmploye(BigDecimal totalCnssEmploye) {
        this.totalCnssEmploye = totalCnssEmploye;
    }

    public BigDecimal getTotalCnssEmployeur() {
        return totalCnssEmployeur;
    }

    public void setTotalCnssEmployeur(BigDecimal totalCnssEmployeur) {
        this.totalCnssEmployeur = totalCnssEmployeur;
    }

    public BigDecimal getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(BigDecimal totalCharges) {
        this.totalCharges = totalCharges;
    }
}
