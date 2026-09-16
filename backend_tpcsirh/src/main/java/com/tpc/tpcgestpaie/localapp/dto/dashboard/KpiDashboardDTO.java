package com.tpc.tpcgestpaie.localapp.dto.dashboard;

import java.math.BigDecimal;

public class KpiDashboardDTO {

    private double tauxTurnover;        // en %
    private double tauxAbsentisme;      // en %
    private BigDecimal coutMoyenEmploye;
    private BigDecimal ratioMasseSalarialeCa;

    // Getters & Setters
    public double getTauxTurnover() {
        return tauxTurnover;
    }

    public void setTauxTurnover(double tauxTurnover) {
        this.tauxTurnover = tauxTurnover;
    }

    public double getTauxAbsentisme() {
        return tauxAbsentisme;
    }

    public void setTauxAbsentisme(double tauxAbsentisme) {
        this.tauxAbsentisme = tauxAbsentisme;
    }

    public BigDecimal getCoutMoyenEmploye() {
        return coutMoyenEmploye;
    }

    public void setCoutMoyenEmploye(BigDecimal coutMoyenEmploye) {
        this.coutMoyenEmploye = coutMoyenEmploye;
    }

    public BigDecimal getRatioMasseSalarialeCa() {
        return ratioMasseSalarialeCa;
    }

    public void setRatioMasseSalarialeCa(BigDecimal ratioMasseSalarialeCa) {
        this.ratioMasseSalarialeCa = ratioMasseSalarialeCa;
    }
}
