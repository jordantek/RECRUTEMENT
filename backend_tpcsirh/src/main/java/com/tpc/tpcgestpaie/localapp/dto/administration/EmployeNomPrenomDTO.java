package com.tpc.tpcgestpaie.localapp.dto.administration;

import com.tpc.tpcgestpaie.localapp.model.Employe;

public class EmployeNomPrenomDTO {
    private Long id;
    private String prenom;
    private String nom;

    public EmployeNomPrenomDTO() {
    }

    public EmployeNomPrenomDTO(Long id, String prenom, String nom) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
    }

    public EmployeNomPrenomDTO(Employe employe) {
        this.id = employe.getId();
        this.prenom = employe.getPrenom();
        this.nom = employe.getNom();
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}
