package com.tpc.tpcgestpaie.localapp.dto.paie;

import java.util.List;

public class PrelevementMensualiteListResponse {
    private String mois;
    private Long institutionId;
    private Long companyId;
    private List<PrelevementMensualiteDTO> prelevements;
    private double total;

    // getters et setters
    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public List<PrelevementMensualiteDTO> getPrelevements() {
        return prelevements;
    }

    public void setPrelevements(List<PrelevementMensualiteDTO> prelevements) {
        this.prelevements = prelevements;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
