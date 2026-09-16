package com.tpc.tpcgestpaie.localapp.dto.accessoire;

public class IndemniteCongeDTO {
    private Long idContratEmploye;
    private String typeOperation;
    private String mois;// "ALLOCATION" ou "INDEMNITE"

    public Long getIdContratEmploye() {
        return idContratEmploye;
    }

    public void setIdContratEmploye(Long idContratEmploye) {
        this.idContratEmploye = idContratEmploye;
    }

    public String getTypeOperation() {
        return typeOperation;
    }

    public void setTypeOperation(String typeOperation) {
        this.typeOperation = typeOperation;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }
}
