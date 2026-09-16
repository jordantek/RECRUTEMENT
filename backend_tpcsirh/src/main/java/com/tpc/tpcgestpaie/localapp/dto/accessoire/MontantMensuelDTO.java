package com.tpc.tpcgestpaie.localapp.dto.accessoire;

import java.math.BigDecimal;

public class MontantMensuelDTO {
    private String mois;               // Exemple : "2025-08"
    private BigDecimal montant;       // Exemple : 200000.00

    public MontantMensuelDTO(String mois, BigDecimal montant) {
        this.mois = mois;
        this.montant = montant;
    }

    // Constructeurs, getters, setters


    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
}
