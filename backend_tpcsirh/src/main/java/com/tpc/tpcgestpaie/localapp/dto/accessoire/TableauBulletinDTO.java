package com.tpc.tpcgestpaie.localapp.dto.accessoire;

import java.math.BigDecimal;
import java.util.List;

public class TableauBulletinDTO {
    private List<LigneBulletinDTO> bulletins;
    private double totalTempsTravail;
    private BigDecimal totalSalaireBrut;
    private double congePrisMoisEnCours; // ➕

    public TableauBulletinDTO(List<LigneBulletinDTO> bulletins,
                              double totalTempsTravail,
                              BigDecimal totalSalaireBrut,
                              double congePrisMoisEnCours) {
        this.bulletins = bulletins;
        this.totalTempsTravail = totalTempsTravail;
        this.totalSalaireBrut = totalSalaireBrut;
        this.congePrisMoisEnCours = congePrisMoisEnCours;
    }

    public List<LigneBulletinDTO> getBulletins() {
        return bulletins;
    }

    public void setBulletins(List<LigneBulletinDTO> bulletins) {
        this.bulletins = bulletins;
    }

    public double getTotalTempsTravail() {
        return totalTempsTravail;
    }

    public void setTotalTempsTravail(double totalTempsTravail) {
        this.totalTempsTravail = totalTempsTravail;
    }

    public BigDecimal getTotalSalaireBrut() {
        return totalSalaireBrut;
    }

    public void setTotalSalaireBrut(BigDecimal totalSalaireBrut) {
        this.totalSalaireBrut = totalSalaireBrut;
    }

    public double getCongePrisMoisEnCours() {
        return congePrisMoisEnCours;
    }

    public void setCongePrisMoisEnCours(double congePrisMoisEnCours) {
        this.congePrisMoisEnCours = congePrisMoisEnCours;
    }
}

