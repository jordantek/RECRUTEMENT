package com.tpc.tpcgestpaie.localapp.enums;

public enum StatutJourFerie {
    ACTIF("Actif"),
    INACTIF("Inactif");

    private final String libelle;

    StatutJourFerie(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}