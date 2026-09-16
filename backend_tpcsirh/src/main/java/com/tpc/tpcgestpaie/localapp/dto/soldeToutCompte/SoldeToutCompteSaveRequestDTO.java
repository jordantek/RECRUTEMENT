package com.tpc.tpcgestpaie.localapp.dto.soldeToutCompte;


import lombok.Data;

import java.math.BigDecimal;

public class SoldeToutCompteSaveRequestDTO {
    private Long contratEmployeId;
    private Long companyId;
    private String moisCalcul;

    // Valeurs d'entrée
    private BigDecimal salairePresence;
    private BigDecimal indemniteLicenciement;
    private BigDecimal indemnitePreavis;
    private BigDecimal indemniteConge;
    private BigDecimal gratification;
    private BigDecimal salaireMoyen;

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

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
