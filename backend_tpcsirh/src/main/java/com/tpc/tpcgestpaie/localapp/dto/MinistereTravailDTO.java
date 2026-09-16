package com.tpc.tpcgestpaie.localapp.dto;

import java.time.LocalDateTime;

public class MinistereTravailDTO {
    private Long id;
    private String ministere;
    private String serviceSecuriteSociale;
    private String directionEnregistrementContrat;
    private String directeurDepartementalTravail;

    private String addedBy; // on expose juste le nom/username de l’utilisateur
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMinistere() {
        return ministere;
    }

    public void setMinistere(String ministere) {
        this.ministere = ministere;
    }

    public String getServiceSecuriteSociale() {
        return serviceSecuriteSociale;
    }

    public void setServiceSecuriteSociale(String serviceSecuriteSociale) {
        this.serviceSecuriteSociale = serviceSecuriteSociale;
    }

    public String getDirectionEnregistrementContrat() {
        return directionEnregistrementContrat;
    }

    public void setDirectionEnregistrementContrat(String directionEnregistrementContrat) {
        this.directionEnregistrementContrat = directionEnregistrementContrat;
    }

    public String getDirecteurDepartementalTravail() {
        return directeurDepartementalTravail;
    }

    public void setDirecteurDepartementalTravail(String directeurDepartementalTravail) {
        this.directeurDepartementalTravail = directeurDepartementalTravail;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
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
