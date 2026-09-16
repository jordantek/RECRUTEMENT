package com.tpc.tpcgestpaie.localapp.enums;


public enum TypeJourFerie {
    NATIONAL("Jour férié national"),
    MOBILE("Jour férié mobile"),
    PERSONNALISE("Jour férié personnalisé");

    private final String libelle;

    TypeJourFerie(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}