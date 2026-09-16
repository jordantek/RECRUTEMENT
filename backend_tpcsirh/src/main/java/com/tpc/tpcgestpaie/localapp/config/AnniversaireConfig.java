package com.tpc.tpcgestpaie.localapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "anniversaire.alertes")
public class AnniversaireConfig {

    private List<Integer> echeances = Arrays.asList(7, 2, 1);
    private String timezone = "Europe/Paris";
    private boolean enabled = true;

    // Getters et setters
    public List<Integer> getEcheances() {
        return echeances;
    }

    public void setEcheances(List<Integer> echeances) {
        this.echeances = echeances;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}