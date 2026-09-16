package com.tpc.tpcgestpaie.localapp.dto.accessoire;

public class AllocationCongeSaveDTO {
    private ResultatCongeDTO resultatCongeDTO;
    private Long idContratEmploye;
    private Long companyId;

    private String moisCalculSalaire;

    // getters/setters
    public String getMoisCalculSalaire() {
        return moisCalculSalaire;
    }

    public void setMoisCalculSalaire(String moisCalculSalaire) {
        this.moisCalculSalaire = moisCalculSalaire;
    }

    public ResultatCongeDTO getResultatCongeDTO() {
        return resultatCongeDTO;
    }

    public void setResultatCongeDTO(ResultatCongeDTO resultatCongeDTO) {
        this.resultatCongeDTO = resultatCongeDTO;
    }

    public Long getIdContratEmploye() {
        return idContratEmploye;
    }

    public void setIdContratEmploye(Long idContratEmploye) {
        this.idContratEmploye = idContratEmploye;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
