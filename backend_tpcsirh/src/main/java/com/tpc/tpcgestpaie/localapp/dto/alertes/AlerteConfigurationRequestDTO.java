package com.tpc.tpcgestpaie.localapp.dto.alertes;

import com.tpc.tpcgestpaie.localapp.model.alertes.AlerteConfiguration;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class AlerteConfigurationRequestDTO {

    @NotNull(message = "Le type d'alerte est obligatoire")
    private AlerteConfiguration.TypeAlerte typeAlerte;

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    private String description;

    @NotBlank(message = "Les échéances sont obligatoires")
    private String echeancesJours;

    private boolean enabled = true;
    private boolean notifierJourJ = true;
    private String motifsImportants;

    // Méthode de conversion vers l'entité
    public AlerteConfiguration toEntity() {
        AlerteConfiguration entity = new AlerteConfiguration();
        entity.setTypeAlerte(this.typeAlerte);
        entity.setLibelle(this.libelle);
        entity.setDescription(this.description);
        entity.setEcheancesJours(this.echeancesJours);
        entity.setEnabled(this.enabled);
        entity.setNotifierJourJ(this.notifierJourJ);
        entity.setMotifsImportants(this.motifsImportants);
        return entity;
    }

    public AlerteConfiguration.TypeAlerte getTypeAlerte() {
        return typeAlerte;
    }

    public void setTypeAlerte(AlerteConfiguration.TypeAlerte typeAlerte) {
        this.typeAlerte = typeAlerte;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEcheancesJours() {
        return echeancesJours;
    }

    public void setEcheancesJours(String echeancesJours) {
        this.echeancesJours = echeancesJours;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isNotifierJourJ() {
        return notifierJourJ;
    }

    public void setNotifierJourJ(boolean notifierJourJ) {
        this.notifierJourJ = notifierJourJ;
    }

    public String getMotifsImportants() {
        return motifsImportants;
    }

    public void setMotifsImportants(String motifsImportants) {
        this.motifsImportants = motifsImportants;
    }
}