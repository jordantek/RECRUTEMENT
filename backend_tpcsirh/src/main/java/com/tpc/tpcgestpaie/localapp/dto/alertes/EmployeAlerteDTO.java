package com.tpc.tpcgestpaie.localapp.dto.alertes;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EmployeAlerteDTO {
    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private LocalDate prochainAnniversaire;
    private long joursRestants;
    private int ageActuel;
    private String companyName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public LocalDate getProchainAnniversaire() {
        return prochainAnniversaire;
    }

    public void setProchainAnniversaire(LocalDate prochainAnniversaire) {
        this.prochainAnniversaire = prochainAnniversaire;
    }

    public long getJoursRestants() {
        return joursRestants;
    }

    public void setJoursRestants(long joursRestants) {
        this.joursRestants = joursRestants;
    }

    public int getAgeActuel() {
        return ageActuel;
    }

    public void setAgeActuel(int ageActuel) {
        this.ageActuel = ageActuel;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}