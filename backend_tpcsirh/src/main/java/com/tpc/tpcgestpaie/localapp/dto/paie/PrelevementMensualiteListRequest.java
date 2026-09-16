package com.tpc.tpcgestpaie.localapp.dto.paie;

public class PrelevementMensualiteListRequest {
    private String mois;
    private Long institutionId;
    private Long companyId;

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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
