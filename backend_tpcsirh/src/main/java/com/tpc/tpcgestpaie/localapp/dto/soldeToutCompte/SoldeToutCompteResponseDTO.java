package com.tpc.tpcgestpaie.localapp.dto.soldeToutCompte;
import java.math.BigDecimal;

public class SoldeToutCompteResponseDTO {
    private String moisCalcul;

    // Valeurs d'entrée
    private BigDecimal salairePresence;
    private BigDecimal indemniteLicenciement;
    private BigDecimal indemnitePreavis;
    private BigDecimal indemniteConge;
    private BigDecimal gratification;
    private BigDecimal salaireMoyen;

    // Résultats calculés
    private BigDecimal brutMois; // A+B+C+D+E
    private BigDecimal itsMoyen;
    private BigDecimal itsMois;
    private BigDecimal cnssMois;
    private BigDecimal netMois;

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


    public BigDecimal getBrutMois() {
        return brutMois;
    }

    public void setBrutMois(BigDecimal brutMois) {
        this.brutMois = brutMois;
    }

    public BigDecimal getItsMoyen() {
        return itsMoyen;
    }

    public void setItsMoyen(BigDecimal itsMoyen) {
        this.itsMoyen = itsMoyen;
    }

    public BigDecimal getItsMois() {
        return itsMois;
    }

    public void setItsMois(BigDecimal itsMois) {
        this.itsMois = itsMois;
    }

    public BigDecimal getCnssMois() {
        return cnssMois;
    }

    public void setCnssMois(BigDecimal cnssMois) {
        this.cnssMois = cnssMois;
    }

    public BigDecimal getNetMois() {
        return netMois;
    }

    public void setNetMois(BigDecimal netMois) {
        this.netMois = netMois;
    }

    public BigDecimal getSalaireMoyen() {
        return salaireMoyen;
    }

    public void setSalaireMoyen(BigDecimal salaireMoyen) {
        this.salaireMoyen = salaireMoyen;
    }
}
