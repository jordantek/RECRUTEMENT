package com.tpc.tpcgestpaie.localapp.dto;

public class PersonneAPrevenirDTO {
    private Long id;
    private String nomPrenom;
    private String telephone;
    private String adresse;
    private String email;
    private String lienParenteLibelle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomPrenom() {
        return nomPrenom;
    }

    public void setNomPrenom(String nomPrenom) {
        this.nomPrenom = nomPrenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLienParenteLibelle() {
        return lienParenteLibelle;
    }

    public void setLienParenteLibelle(String lienParenteLibelle) {
        this.lienParenteLibelle = lienParenteLibelle;
    }
}
