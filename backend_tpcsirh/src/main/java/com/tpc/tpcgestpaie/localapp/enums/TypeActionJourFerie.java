package com.tpc.tpcgestpaie.localapp.enums;

/**
 * Types d'actions possibles
 */
public enum TypeActionJourFerie {
    CREATION("Création d'un jour férié"),
    MODIFICATION("Modification d'un jour férié"),
    SUPPRESSION("Suppression d'un jour férié"),
    ACTIVATION("Activation du jour férié"),
    DESACTIVATION("Désactivation du jour férié"),
    REPORT("Report du jour férié"),
    GENERATION_AUTO("Génération automatique des jours fériés");

    private final String libelle;

    TypeActionJourFerie(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}