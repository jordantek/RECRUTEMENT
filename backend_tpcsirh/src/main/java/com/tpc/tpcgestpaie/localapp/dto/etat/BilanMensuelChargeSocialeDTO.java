package com.tpc.tpcgestpaie.localapp.dto.etat;

import java.math.BigDecimal;

public class BilanMensuelChargeSocialeDTO {
    private String moisBulletinPaie;
    private String entreprise;
    private String employe;
    private BigDecimal salaireBrutBulletinPaie;
    private BigDecimal montantCnssBulletinPaie;
    private BigDecimal montantCnssEmployeurBulletinPaie;
    private BigDecimal totalChargeSociale;

    public BilanMensuelChargeSocialeDTO(String moisBulletinPaie, String entreprise, String employe,
                                        BigDecimal salaireBrutBulletinPaie,
                                        BigDecimal montantCnssBulletinPaie,
                                        BigDecimal montantCnssEmployeurBulletinPaie) {
        this.moisBulletinPaie = moisBulletinPaie;
        this.entreprise = entreprise;
        this.employe = employe;
        this.salaireBrutBulletinPaie = salaireBrutBulletinPaie;
        this.montantCnssBulletinPaie = montantCnssBulletinPaie;
        this.montantCnssEmployeurBulletinPaie = montantCnssEmployeurBulletinPaie;
        this.totalChargeSociale = (montantCnssBulletinPaie == null ? BigDecimal.ZERO : montantCnssBulletinPaie)
                .add(montantCnssEmployeurBulletinPaie == null ? BigDecimal.ZERO : montantCnssEmployeurBulletinPaie);
    }

    // getters & setters


    public String getMoisBulletinPaie() {
        return moisBulletinPaie;
    }

    public void setMoisBulletinPaie(String moisBulletinPaie) {
        this.moisBulletinPaie = moisBulletinPaie;
    }

    public String getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(String entreprise) {
        this.entreprise = entreprise;
    }

    public String getEmploye() {
        return employe;
    }

    public void setEmploye(String employe) {
        this.employe = employe;
    }

    public BigDecimal getSalaireBrutBulletinPaie() {
        return salaireBrutBulletinPaie;
    }

    public void setSalaireBrutBulletinPaie(BigDecimal salaireBrutBulletinPaie) {
        this.salaireBrutBulletinPaie = salaireBrutBulletinPaie;
    }

    public BigDecimal getMontantCnssBulletinPaie() {
        return montantCnssBulletinPaie;
    }

    public void setMontantCnssBulletinPaie(BigDecimal montantCnssBulletinPaie) {
        this.montantCnssBulletinPaie = montantCnssBulletinPaie;
    }

    public BigDecimal getMontantCnssEmployeurBulletinPaie() {
        return montantCnssEmployeurBulletinPaie;
    }

    public void setMontantCnssEmployeurBulletinPaie(BigDecimal montantCnssEmployeurBulletinPaie) {
        this.montantCnssEmployeurBulletinPaie = montantCnssEmployeurBulletinPaie;
    }

    public BigDecimal getTotalChargeSociale() {
        return totalChargeSociale;
    }

    public void setTotalChargeSociale(BigDecimal totalChargeSociale) {
        this.totalChargeSociale = totalChargeSociale;
    }
}
