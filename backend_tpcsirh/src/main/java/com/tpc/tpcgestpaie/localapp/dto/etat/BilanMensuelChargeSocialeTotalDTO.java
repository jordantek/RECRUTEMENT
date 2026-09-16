package com.tpc.tpcgestpaie.localapp.dto.etat;

import java.math.BigDecimal;

public class BilanMensuelChargeSocialeTotalDTO {
    private BigDecimal totalSalaireBrut;
    private BigDecimal totalMontantCnss;
    private BigDecimal totalMontantCnssEmployeur;
    private BigDecimal totalCharges;

    public BilanMensuelChargeSocialeTotalDTO(BigDecimal totalSalaireBrut, BigDecimal totalMontantCnss,
                                             BigDecimal totalMontantCnssEmployeur, BigDecimal totalCharges) {
        this.totalSalaireBrut = totalSalaireBrut;
        this.totalMontantCnss = totalMontantCnss;
        this.totalMontantCnssEmployeur = totalMontantCnssEmployeur;
        this.totalCharges = totalCharges;
    }

    // getters & setters


    public BigDecimal getTotalSalaireBrut() {
        return totalSalaireBrut;
    }

    public void setTotalSalaireBrut(BigDecimal totalSalaireBrut) {
        this.totalSalaireBrut = totalSalaireBrut;
    }

    public BigDecimal getTotalMontantCnss() {
        return totalMontantCnss;
    }

    public void setTotalMontantCnss(BigDecimal totalMontantCnss) {
        this.totalMontantCnss = totalMontantCnss;
    }

    public BigDecimal getTotalMontantCnssEmployeur() {
        return totalMontantCnssEmployeur;
    }

    public void setTotalMontantCnssEmployeur(BigDecimal totalMontantCnssEmployeur) {
        this.totalMontantCnssEmployeur = totalMontantCnssEmployeur;
    }

    public BigDecimal getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(BigDecimal totalCharges) {
        this.totalCharges = totalCharges;
    }
}
