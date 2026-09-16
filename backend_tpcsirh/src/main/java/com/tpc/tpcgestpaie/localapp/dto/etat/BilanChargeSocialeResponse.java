package com.tpc.tpcgestpaie.localapp.dto.etat;

import java.math.BigDecimal;
import java.util.List;

public class BilanChargeSocialeResponse {
    private List<BilanChargeSocialeDTO> bulletins;
    private BigDecimal totalSalaireBrut;
    private BigDecimal totalSalaireBrutArrondi;
    private BigDecimal totalIpts;
    private BigDecimal totalVps;
    private BigDecimal totalGeneral;

    // Info de période
    private String mois; // si mensuel
    private String periodeDebut; // si périodique
    private String periodeFin;   // si périodique

    // getters / setters


    public List<BilanChargeSocialeDTO> getBulletins() {
        return bulletins;
    }

    public void setBulletins(List<BilanChargeSocialeDTO> bulletins) {
        this.bulletins = bulletins;
    }

    public BigDecimal getTotalSalaireBrut() {
        return totalSalaireBrut;
    }

    public void setTotalSalaireBrut(BigDecimal totalSalaireBrut) {
        this.totalSalaireBrut = totalSalaireBrut;
    }

    public BigDecimal getTotalSalaireBrutArrondi() {
        return totalSalaireBrutArrondi;
    }

    public void setTotalSalaireBrutArrondi(BigDecimal totalSalaireBrutArrondi) {
        this.totalSalaireBrutArrondi = totalSalaireBrutArrondi;
    }

    public BigDecimal getTotalIpts() {
        return totalIpts;
    }

    public void setTotalIpts(BigDecimal totalIpts) {
        this.totalIpts = totalIpts;
    }

    public BigDecimal getTotalVps() {
        return totalVps;
    }

    public void setTotalVps(BigDecimal totalVps) {
        this.totalVps = totalVps;
    }

    public BigDecimal getTotalGeneral() {
        return totalGeneral;
    }

    public void setTotalGeneral(BigDecimal totalGeneral) {
        this.totalGeneral = totalGeneral;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public String getPeriodeDebut() {
        return periodeDebut;
    }

    public void setPeriodeDebut(String periodeDebut) {
        this.periodeDebut = periodeDebut;
    }

    public String getPeriodeFin() {
        return periodeFin;
    }

    public void setPeriodeFin(String periodeFin) {
        this.periodeFin = periodeFin;
    }
}
