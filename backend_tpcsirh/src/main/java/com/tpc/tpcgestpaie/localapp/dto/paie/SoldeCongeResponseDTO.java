package com.tpc.tpcgestpaie.localapp.dto.paie;

import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;

public class SoldeCongeResponseDTO {
    private String dateReferenceCreditConge;
    private int creditConge;
    private int nombreTotalJourPris;
    private int soldeConge;
    private EmployeDTO employe;

    // getters & setters


    public String getDateReferenceCreditConge() {
        return dateReferenceCreditConge;
    }

    public void setDateReferenceCreditConge(String dateReferenceCreditConge) {
        this.dateReferenceCreditConge = dateReferenceCreditConge;
    }

    public int getCreditConge() {
        return creditConge;
    }

    public void setCreditConge(int creditConge) {
        this.creditConge = creditConge;
    }

    public int getNombreTotalJourPris() {
        return nombreTotalJourPris;
    }

    public void setNombreTotalJourPris(int nombreTotalJourPris) {
        this.nombreTotalJourPris = nombreTotalJourPris;
    }

    public int getSoldeConge() {
        return soldeConge;
    }

    public void setSoldeConge(int soldeConge) {
        this.soldeConge = soldeConge;
    }

    public EmployeDTO getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeDTO employe) {
        this.employe = employe;
    }
}
