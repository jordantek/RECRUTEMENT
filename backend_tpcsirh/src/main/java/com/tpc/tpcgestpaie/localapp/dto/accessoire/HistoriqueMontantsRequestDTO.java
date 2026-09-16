package com.tpc.tpcgestpaie.localapp.dto.accessoire;

public class HistoriqueMontantsRequestDTO {
    private Long idContratEmploye;
    private  Long idCompany;
    private String mois;
    // Format "yyyy-MM"

    private String typeLicenciement;

    // Getters et setters
    public Long getIdContratEmploye() {
        return idContratEmploye;
    }

    public void setIdContratEmploye(Long idContratEmploye) {
        this.idContratEmploye = idContratEmploye;
    }

    public Long getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(Long idCompany) {
        this.idCompany = idCompany;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public String getTypeLicenciement() {
        return typeLicenciement;
    }

    public void setTypeLicenciement(String typeLicenciement) {
        this.typeLicenciement = typeLicenciement;
    }
}
