package com.tpc.tpcgestpaie.localapp.dto;

import java.time.LocalDateTime;

public class ParametrePaieDTO {
    private Long id;
    private Double prestationsfamiliales;
    private Double pensionsEntreprise;
    private Double pensionsEmploye;
    private Double nbrAnneVpsEntreprise;
    private Double nbrAnneVpsEmploye;
    private Long addedById; // juste l’ID de l’utilisateur
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Double getPrestationsfamiliales() {
        return prestationsfamiliales;
    }
    public void setPrestationsfamiliales(Double prestationsfamiliales) {
        this.prestationsfamiliales = prestationsfamiliales;
    }
    public Double getPensionsEntreprise() {
        return pensionsEntreprise;
    }
    public void setPensionsEntreprise(Double pensionsEntreprise) {
        this.pensionsEntreprise = pensionsEntreprise;
    }
    public Double getPensionsEmploye() {
        return pensionsEmploye;
    }
    public void setPensionsEmploye(Double pensionsEmploye) {
        this.pensionsEmploye = pensionsEmploye;
    }
    public Double getNbrAnneVpsEntreprise() {
        return nbrAnneVpsEntreprise;
    }
    public void setNbrAnneVpsEntreprise(Double nbrAnneVpsEntreprise) {
        this.nbrAnneVpsEntreprise = nbrAnneVpsEntreprise;
    }
    public Double getNbrAnneVpsEmploye() {
        return nbrAnneVpsEmploye;
    }
    public void setNbrAnneVpsEmploye(Double nbrAnneVpsEmploye) {
        this.nbrAnneVpsEmploye = nbrAnneVpsEmploye;
    }
    public Long getAddedById() {
        return addedById;
    }
    public void setAddedById(Long addedById) {
        this.addedById = addedById;
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
