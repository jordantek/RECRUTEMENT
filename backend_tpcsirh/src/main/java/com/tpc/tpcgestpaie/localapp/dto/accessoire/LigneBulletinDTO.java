package com.tpc.tpcgestpaie.localapp.dto.accessoire;

import java.math.BigDecimal;

public class LigneBulletinDTO {
    private String mois;
    private double tempsTravail;
    private BigDecimal salaireBrut;

    // Constructeur
    public LigneBulletinDTO(String mois, double tempsTravail, BigDecimal salaireBrut) {
        this.mois = mois;
        this.tempsTravail = tempsTravail;
        this.salaireBrut = salaireBrut;
    }

    // Getters
    public String getMois() { return mois; }
    public double getTempsTravail() { return tempsTravail; }
    public BigDecimal getSalaireBrut() { return salaireBrut; }
}
