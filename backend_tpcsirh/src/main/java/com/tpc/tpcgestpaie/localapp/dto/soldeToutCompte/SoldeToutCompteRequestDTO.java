package com.tpc.tpcgestpaie.localapp.dto.soldeToutCompte;

import java.math.BigDecimal;

public class SoldeToutCompteRequestDTO {
    private String moisCalcul; // ex: "2025-08"

    private BigDecimal salairePresence;       // A
    private BigDecimal indemniteLicenciement; // B
    private BigDecimal indemnitePreavis;      // C
    private BigDecimal indemniteConge;        // D
    private BigDecimal gratification;         // E
    private BigDecimal salaireMoyen;          // F
    // Getters & Setters

    public String getMoisCalcul() {
        return moisCalcul;
    }

    public void setMoisCalcul(String moisCalcul) {
        this.moisCalcul = moisCalcul;
    }

    public BigDecimal getSalairePresence() {
        return salairePresence;
    }

    public void setSalairePresence(BigDecimal salairePresence) {
        this.salairePresence = salairePresence;
    }

    public BigDecimal getIndemniteLicenciement() {
        return indemniteLicenciement;
    }

    public void setIndemniteLicenciement(BigDecimal indemniteLicenciement) {
        this.indemniteLicenciement = indemniteLicenciement;
    }

    public BigDecimal getIndemnitePreavis() {
        return indemnitePreavis;
    }

    public void setIndemnitePreavis(BigDecimal indemnitePreavis) {
        this.indemnitePreavis = indemnitePreavis;
    }

    public BigDecimal getIndemniteConge() {
        return indemniteConge;
    }

    public void setIndemniteConge(BigDecimal indemniteConge) {
        this.indemniteConge = indemniteConge;
    }

    public BigDecimal getGratification() {
        return gratification;
    }

    public void setGratification(BigDecimal gratification) {
        this.gratification = gratification;
    }

    public BigDecimal getSalaireMoyen() {
        return salaireMoyen;
    }

    public void setSalaireMoyen(BigDecimal salaireMoyen) {
        this.salaireMoyen = salaireMoyen;
    }
}
