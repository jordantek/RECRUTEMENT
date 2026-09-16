package com.tpc.tpcgestpaie.localapp.dto;

public class EmployeUserInfoDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String matricule;
    private String email;
    private String phone;// ou d'autres champs de l'employé que tu souhaites exposer

    public EmployeUserInfoDTO(Long id, String nom, String prenom, String matricule, String email, String phone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.email = email;
        this.phone = phone;
    }

    // getters et setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
