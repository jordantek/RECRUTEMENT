package com.tpc.tpcgestpaie.localapp.dto.accessoire;


import java.math.BigDecimal;

public class BulletinResumeDTO {
    private String mois; // format "yyyy-MM"
    private double tempsTravail;
    private BigDecimal salaireBrut;

    public BulletinResumeDTO(String mois, double tempsTravail, BigDecimal salaireBrut) {
        this.mois = mois;
        this.tempsTravail = tempsTravail;
        this.salaireBrut = salaireBrut;
    }
    // Getters et setters

    public BulletinResumeDTO() {
        // constructeur vide obligatoire pour Jackson
    }
    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public double getTempsTravail() {
        return tempsTravail;
    }

    public void setTempsTravail(double tempsTravail) {
        this.tempsTravail = tempsTravail;
    }

    public BigDecimal getSalaireBrut() {
        return salaireBrut;
    }

    public void setSalaireBrut(BigDecimal salaireBrut) {
        this.salaireBrut = salaireBrut;
    }
}
