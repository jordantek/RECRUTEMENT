package com.tpc.tpcgestpaie.localapp.dto.export;

import java.math.BigDecimal;

public class BilanChargeSocialeExportDTO {
    private String mois;
    private String periodeDebut;
    private String periodeFin;
    private BigDecimal salaireBrut;
    private BigDecimal salaireBrutArrondi;
    private BigDecimal montantIpts;
    private BigDecimal montantVps;
    private BigDecimal total;

    public BilanChargeSocialeExportDTO() {}

    public BilanChargeSocialeExportDTO(String mois, String periodeDebut, String periodeFin,
                                       BigDecimal salaireBrut, BigDecimal salaireBrutArrondi,
                                       BigDecimal montantIpts, BigDecimal montantVps, BigDecimal total) {
        this.mois = mois;
        this.periodeDebut = periodeDebut;
        this.periodeFin = periodeFin;
        this.salaireBrut = salaireBrut;
        this.salaireBrutArrondi = salaireBrutArrondi;
        this.montantIpts = montantIpts;
        this.montantVps = montantVps;
        this.total = total;
    }

    // Getters et Setters
    public String getMois() { return mois; }
    public void setMois(String mois) { this.mois = mois; }

    public String getPeriodeDebut() { return periodeDebut; }
    public void setPeriodeDebut(String periodeDebut) { this.periodeDebut = periodeDebut; }

    public String getPeriodeFin() { return periodeFin; }
    public void setPeriodeFin(String periodeFin) { this.periodeFin = periodeFin; }

    public BigDecimal getSalaireBrut() { return salaireBrut; }
    public void setSalaireBrut(BigDecimal salaireBrut) { this.salaireBrut = salaireBrut; }

    public BigDecimal getSalaireBrutArrondi() { return salaireBrutArrondi; }
    public void setSalaireBrutArrondi(BigDecimal salaireBrutArrondi) { this.salaireBrutArrondi = salaireBrutArrondi; }

    public BigDecimal getMontantIpts() { return montantIpts; }
    public void setMontantIpts(BigDecimal montantIpts) { this.montantIpts = montantIpts; }

    public BigDecimal getMontantVps() { return montantVps; }
    public void setMontantVps(BigDecimal montantVps) { this.montantVps = montantVps; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}