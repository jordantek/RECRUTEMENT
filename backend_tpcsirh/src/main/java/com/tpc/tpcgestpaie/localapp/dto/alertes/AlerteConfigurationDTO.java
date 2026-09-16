package com.tpc.tpcgestpaie.localapp.dto.alertes;

import com.tpc.tpcgestpaie.localapp.model.alertes.AlerteConfiguration;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AlerteConfigurationDTO {
    private Long id;
    private AlerteConfiguration.TypeAlerte typeAlerte;
    private String libelle;
    private String description;
    private String echeancesJours;
    private boolean enabled;
    private boolean notifierJourJ;
    private String motifsImportants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Méthode de conversion depuis l'entité
    public static AlerteConfigurationDTO fromEntity(AlerteConfiguration entity) {
        if (entity == null) {
            return null;
        }

        AlerteConfigurationDTO dto = new AlerteConfigurationDTO();
        dto.setId(entity.getId());
        dto.setTypeAlerte(entity.getTypeAlerte());
        dto.setLibelle(entity.getLibelle());
        dto.setDescription(entity.getDescription());
        dto.setEcheancesJours(entity.getEcheancesJours());
        dto.setEnabled(entity.isEnabled());
        dto.setNotifierJourJ(entity.isNotifierJourJ());
        dto.setMotifsImportants(entity.getMotifsImportants());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    // Méthode de conversion vers l'entité
    public AlerteConfiguration toEntity() {
        AlerteConfiguration entity = new AlerteConfiguration();
        entity.setId(this.id);
        entity.setTypeAlerte(this.typeAlerte);
        entity.setLibelle(this.libelle);
        entity.setDescription(this.description);
        entity.setEcheancesJours(this.echeancesJours);
        entity.setEnabled(this.enabled);
        entity.setNotifierJourJ(this.notifierJourJ);
        entity.setMotifsImportants(this.motifsImportants);
        return entity;
    }

    // Méthode pour convertir une liste
    public static List<AlerteConfigurationDTO> fromEntityList(List<AlerteConfiguration> entities) {
        return entities.stream()
                .map(AlerteConfigurationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}