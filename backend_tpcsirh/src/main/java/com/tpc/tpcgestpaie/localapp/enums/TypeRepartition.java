package com.tpc.tpcgestpaie.localapp.enums;

public enum TypeRepartition {
    NORMALE,              // Consommation standard liée à une demande
    HISTORIQUE_MIGRE,     // Consommation historique sans demande liée
    ANNULATION,           // Annulation de consommation
    REGULARISATION        // Ajustement manuel
}