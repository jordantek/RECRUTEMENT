package com.tpc.tpcgestpaie.localapp.dto.accessoire;

import java.math.BigDecimal;

public class ProvisionCongeSimulationRequestDTO {
    private BigDecimal totalSalaireBrut12mois;
    private double totalTempsTravail;
    private long joursAProvisionner;

    // Getters et Setters
    public BigDecimal getTotalSalaireBrut12mois() {
        return totalSalaireBrut12mois;
    }

    public void setTotalSalaireBrut12mois(BigDecimal totalSalaireBrut12mois) {
        this.totalSalaireBrut12mois = totalSalaireBrut12mois;
    }

    public double getTotalTempsTravail() {
        return totalTempsTravail;
    }

    public void setTotalTempsTravail(double totalTempsTravail) {
        this.totalTempsTravail = totalTempsTravail;
    }

    public long getJoursAProvisionner() {
        return joursAProvisionner;
    }

    public void setJoursAProvisionner(long joursAProvisionner) {
        this.joursAProvisionner = joursAProvisionner;
    }
}
