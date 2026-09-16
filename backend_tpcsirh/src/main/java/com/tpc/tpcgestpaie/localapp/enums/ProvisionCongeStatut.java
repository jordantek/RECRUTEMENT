package com.tpc.tpcgestpaie.localapp.enums;

public enum ProvisionCongeStatut {
    ACTIF,                    // Jamais consommé
    PARTIELLEMENT_CONSOMME,   // Une partie utilisée, reste disponible
    CLOTURE,                  // Tout consommé (0 jours restant)
    ANNULE                    // Provision erronée, ne pas utiliser
}