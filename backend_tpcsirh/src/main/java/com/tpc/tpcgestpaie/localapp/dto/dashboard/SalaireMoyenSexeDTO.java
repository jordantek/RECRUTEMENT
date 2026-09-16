package com.tpc.tpcgestpaie.localapp.dto.dashboard;

import java.math.BigDecimal;

public class SalaireMoyenSexeDTO {
    private BigDecimal salaireMoyenHomme;
    private BigDecimal salaireMoyenFemme;

    public SalaireMoyenSexeDTO() {}

    public SalaireMoyenSexeDTO(BigDecimal salaireMoyenHomme, BigDecimal salaireMoyenFemme) {
        this.salaireMoyenHomme = salaireMoyenHomme;
        this.salaireMoyenFemme = salaireMoyenFemme;
    }

    public BigDecimal getSalaireMoyenHomme() {
        return salaireMoyenHomme;
    }

    public void setSalaireMoyenHomme(BigDecimal salaireMoyenHomme) {
        this.salaireMoyenHomme = salaireMoyenHomme;
    }

    public BigDecimal getSalaireMoyenFemme() {
        return salaireMoyenFemme;
    }

    public void setSalaireMoyenFemme(BigDecimal salaireMoyenFemme) {
        this.salaireMoyenFemme = salaireMoyenFemme;
    }
}
