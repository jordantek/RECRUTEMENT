package com.tpc.tpcgestpaie.localapp.dto.etat;

import java.math.BigDecimal;

public class BilanChargeSocialeDTO {
    private BigDecimal salaireBrut;
    private BigDecimal salaireBrutArrondi;
    private BigDecimal montantIpts;
    private BigDecimal montantVps;
    private BigDecimal total; // ipts + vps

    public BilanChargeSocialeDTO(BigDecimal salaireBrut, BigDecimal salaireBrutArrondi, BigDecimal montantIpts, BigDecimal montantVps) {
        this.salaireBrut = salaireBrut;
        this.salaireBrutArrondi = salaireBrutArrondi;
        this.montantIpts = montantIpts;
        this.montantVps = montantVps;
        this.total = (montantIpts != null ? montantIpts : BigDecimal.ZERO)
                .add(montantVps != null ? montantVps : BigDecimal.ZERO);
    }

    // getters / setters
    public BigDecimal getSalaireBrut() {
        return salaireBrut;
    }

    public void setSalaireBrut(BigDecimal salaireBrut) {
        this.salaireBrut = salaireBrut;
    }

    public BigDecimal getSalaireBrutArrondi() {
        return salaireBrutArrondi;
    }

    public void setSalaireBrutArrondi(BigDecimal salaireBrutArrondi) {
        this.salaireBrutArrondi = salaireBrutArrondi;
    }

    public BigDecimal getMontantIpts() {
        return montantIpts;
    }

    public void setMontantIpts(BigDecimal montantIpts) {
        this.montantIpts = montantIpts;
    }

    public BigDecimal getMontantVps() {
        return montantVps;
    }

    public void setMontantVps(BigDecimal montantVps) {
        this.montantVps = montantVps;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
