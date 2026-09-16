package com.tpc.tpcgestpaie.localapp.dto.etat;

import java.math.BigDecimal;

public class SalaireMensuelDTO {
    private String employe;
    private String entreprise;
    private String departement;
    private String domiciliation;
    private String numeroCompte;
    private double tempsTravail;
    private BigDecimal netAPayer;

    public SalaireMensuelDTO(String employe, String entreprise, String departement,
                             String domiciliation, String numeroCompte, double tempsTravail, BigDecimal netAPayer) {
        this.employe = employe;
        this.entreprise = entreprise;
        this.departement = departement;
        this.domiciliation = domiciliation;
        this.numeroCompte = numeroCompte;
        this.tempsTravail = tempsTravail;
        this.netAPayer = netAPayer;
    }

    // getters / setters


    public String getEmploye() {
        return employe;
    }

    public void setEmploye(String employe) {
        this.employe = employe;
    }

    public String getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(String entreprise) {
        this.entreprise = entreprise;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getDomiciliation() {
        return domiciliation;
    }

    public void setDomiciliation(String domiciliation) {
        this.domiciliation = domiciliation;
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }

    public void setNumeroCompte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }

    public double getTempsTravail() {
        return tempsTravail;
    }

    public void setTempsTravail(double tempsTravail) {
        this.tempsTravail = tempsTravail;
    }

    public BigDecimal getNetAPayer() {
        return netAPayer;
    }

    public void setNetAPayer(BigDecimal netAPayer) {
        this.netAPayer = netAPayer;
    }
}
