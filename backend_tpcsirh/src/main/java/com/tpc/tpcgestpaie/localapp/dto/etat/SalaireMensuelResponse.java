package com.tpc.tpcgestpaie.localapp.dto.etat;

import java.math.BigDecimal;
import java.util.List;

public class SalaireMensuelResponse {
    private List<SalaireMensuelDTO> salaires;
    private BigDecimal totalNetAPayer;

    // getters / setters


    public List<SalaireMensuelDTO> getSalaires() {
        return salaires;
    }

    public void setSalaires(List<SalaireMensuelDTO> salaires) {
        this.salaires = salaires;
    }

    public BigDecimal getTotalNetAPayer() {
        return totalNetAPayer;
    }

    public void setTotalNetAPayer(BigDecimal totalNetAPayer) {
        this.totalNetAPayer = totalNetAPayer;
    }
}
