package com.tpc.tpcgestpaie.localapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "contrat.alertes")
public class ContratConfig {

    private List<Integer> echeancesFinEssai = Arrays.asList(15, 7, 3, 1);
    private List<Integer> echeancesFinContrat = Arrays.asList(30, 15, 7, 1);
    private boolean enabled = true;
    private String timezone = "Europe/Paris";

    // Getters et setters
    public List<Integer> getEcheancesFinEssai() {
        return echeancesFinEssai;
    }

    public void setEcheancesFinEssai(List<Integer> echeancesFinEssai) {
        this.echeancesFinEssai = echeancesFinEssai;
    }

    public List<Integer> getEcheancesFinContrat() {
        return echeancesFinContrat;
    }

    public void setEcheancesFinContrat(List<Integer> echeancesFinContrat) {
        this.echeancesFinContrat = echeancesFinContrat;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}