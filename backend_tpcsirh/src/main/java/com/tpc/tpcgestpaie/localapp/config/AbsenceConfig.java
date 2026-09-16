package com.tpc.tpcgestpaie.localapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "absence.alertes")
public class AbsenceConfig {

    private List<Integer> echeancesDebut = Arrays.asList(3, 1); // 3 jour avant et jour J
    private List<Integer> echeancesFin = Arrays.asList(3, 1); // 3 jour avant et jour J
    private boolean enabled = true;
    private String timezone = "Europe/Paris";
    private List<String> motifsImportants = Arrays.asList("MALADIE", "MARIAGE", "SABBATIQUE", "FORMATION","DECES","MATERNITE","PATERNITE","CONGE ADMINISTRATIF","LEGAL");

    // Getters et setters
    public List<Integer> getEcheancesDebut() {
        return echeancesDebut;
    }

    public void setEcheancesDebut(List<Integer> echeancesDebut) {
        this.echeancesDebut = echeancesDebut;
    }
    public List<Integer> getEcheancesFin() {
        return echeancesFin;
    }
    public void setEcheancesFin(List<Integer> echeancesFin) {
        this.echeancesFin = echeancesFin;
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

    public List<String> getMotifsImportants() {
        return motifsImportants;
    }

    public void setMotifsImportants(List<String> motifsImportants) {
        this.motifsImportants = motifsImportants;
    }
}