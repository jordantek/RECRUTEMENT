package com.tpc.tpcgestpaie.localapp.enums;

public enum TypeProvision {
    NORMALE,                    // Provision mensuelle générée automatiquement
    SYNTHESE_INITIALE,          // Provision unique de migration (solde restant)
    SYNTHESE_AVEC_HISTORIQUE,   // Provision initiale avec détail des congés pris
    HISTORIQUE_MIGRE,           // Mois individuel reconstruit (optionnel)
    REGULARISATION              // Ajustement manuel post-initialisation
}