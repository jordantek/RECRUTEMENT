package com.tpc.tpcgestpaie.localapp.dto.export;

import com.tpc.tpcgestpaie.localapp.dto.etat.BilanFiscaleDTO;

import java.math.BigDecimal;

public class BilanFiscaleExportDTO {
    private String mois;
    private String periodeDebut;
    private String periodeFin;
    private String employe;
    private String entreprise;
    private BigDecimal salaireBrut;
    private BigDecimal salaireBrutArrondi;
    private BigDecimal its;
    private BigDecimal vps;
    private BigDecimal total;

    public BilanFiscaleExportDTO(BilanFiscaleDTO dto) {
        this.mois = dto.getMois();
        this.periodeDebut = dto.getPeriodeDebut();
        this.periodeFin = dto.getPeriodeFin();
        this.employe = dto.getEmploye();
        this.entreprise = dto.getEntreprise();
        this.salaireBrut = dto.getSalaireBrut();
        this.salaireBrutArrondi = dto.getSalaireBrutArrondi();
        this.its = dto.getIts();
        this.vps = dto.getVps();
        this.total = dto.getTotal();
    }

    // Constructeur complet
    public BilanFiscaleExportDTO(String mois, String periodeDebut, String periodeFin, String employe,
                                 String entreprise, BigDecimal salaireBrut, BigDecimal salaireBrutArrondi,
                                 BigDecimal its, BigDecimal vps, BigDecimal total) {
        this.mois = mois;
        this.periodeDebut = periodeDebut;
        this.periodeFin = periodeFin;
        this.employe = employe;
        this.entreprise = entreprise;
        this.salaireBrut = salaireBrut;
        this.salaireBrutArrondi = salaireBrutArrondi;
        this.its = its;
        this.vps = vps;
        this.total = total;
    }

    // Getters et Setters
    public String getMois() { return mois; }
    public void setMois(String mois) { this.mois = mois; }

    public String getPeriodeDebut() { return periodeDebut; }
    public void setPeriodeDebut(String periodeDebut) { this.periodeDebut = periodeDebut; }

    public String getPeriodeFin() { return periodeFin; }
    public void setPeriodeFin(String periodeFin) { this.periodeFin = periodeFin; }

    public String getEmploye() { return employe; }
    public void setEmploye(String employe) { this.employe = employe; }

    public String getEntreprise() { return entreprise; }
    public void setEntreprise(String entreprise) { this.entreprise = entreprise; }

    public BigDecimal getSalaireBrut() { return salaireBrut; }
    public void setSalaireBrut(BigDecimal salaireBrut) { this.salaireBrut = salaireBrut; }

    public BigDecimal getSalaireBrutArrondi() { return salaireBrutArrondi; }
    public void setSalaireBrutArrondi(BigDecimal salaireBrutArrondi) { this.salaireBrutArrondi = salaireBrutArrondi; }

    public BigDecimal getIts() { return its; }
    public void setIts(BigDecimal its) { this.its = its; }

    public BigDecimal getVps() { return vps; }
    public void setVps(BigDecimal vps) { this.vps = vps; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}