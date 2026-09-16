package com.tpc.tpcgestpaie.localapp.enums;

public enum ActionHistorique {
    CREATION_CONFIG,      // Création d'une config
    MODIFICATION_CONFIG,  // Modification d'une config
    SUPPRESSION_CONFIG,   // Suppression d'une config
    GENERATION_ANNEE,     // Génération des jours pour une année
    ACTIVATION,           // Activation d'un jour férié (consommation)
    DESACTIVATION,        // Désactivation d'un jour férié
    AJOUT_PONCTUEL,       // Ajout d'un jour férié ponctuel
    MODIFICATION,         // Modification d'un jour appliqué
    SUPPRESSION,          // Suppression d'un jour appliqué
    REPORT               // Report d'un jour férié
}