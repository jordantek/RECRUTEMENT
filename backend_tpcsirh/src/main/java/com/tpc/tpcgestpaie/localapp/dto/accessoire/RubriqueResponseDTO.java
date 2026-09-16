package com.tpc.tpcgestpaie.localapp.dto.accessoire;


public class RubriqueResponseDTO {
    private Long id;
    private String libelle;

    public RubriqueResponseDTO(Long id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}
