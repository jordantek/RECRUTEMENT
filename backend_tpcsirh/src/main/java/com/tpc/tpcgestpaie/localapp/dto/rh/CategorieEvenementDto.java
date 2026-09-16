package com.tpc.tpcgestpaie.localapp.dto.rh;
public class CategorieEvenementDto {
    private Long id;
    private String libelle; // ou autre champ que tu veux exposer

    public CategorieEvenementDto() {}
    public CategorieEvenementDto(Long id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

       // getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String nom) { this.libelle = nom; }
}

