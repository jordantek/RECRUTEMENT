package com.tpc.tpcgestpaie.localapp.dto.accessoire;

import java.math.BigDecimal;

public class SalaireMoyenRequestDTO {
    private BigDecimal globalMensuelMoyen;
    private double ancienneteTotal;
    private String licenciement;

    // Getters et setters


    public BigDecimal getGlobalMensuelMoyen() {
        return globalMensuelMoyen;
    }

    public void setGlobalMensuelMoyen(BigDecimal globalMensuelMoyen) {
        this.globalMensuelMoyen = globalMensuelMoyen;
    }

    public double getAncienneteTotal() {
        return ancienneteTotal;
    }

    public void setAncienneteTotal(double ancienneteTotal) {
        this.ancienneteTotal = ancienneteTotal;
    }

    public String getLicenciement() {
        return licenciement;
    }

    public void setLicenciement(String licenciement) {
        this.licenciement = licenciement;
    }
}
