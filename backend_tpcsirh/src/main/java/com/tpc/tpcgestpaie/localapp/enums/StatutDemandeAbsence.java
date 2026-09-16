package com.tpc.tpcgestpaie.localapp.enums;

public enum StatutDemandeAbsence {
    // Phase de validation
    EN_ATTENTE_VALIDATION,  // En attente du premier validateur
    EN_COURS_VALIDATION,    // Validation en cours (passé niveau 1)
    APPROUVE_FINAL,         // Tous les niveaux ont approuvé
    REJETE,                 // Rejeté par un validateur
    ANNULE,                 // Annulée par l'employé

    // Phase de suivi (NOUVEAU)
    CONFIRMEE,              // Approuvée et en attente du départ
    EN_COURS,               // Employé actuellement en absence
    DEPART_CONFIRME,        // Départ confirmé par l'employé
    RETOUR_CONFIRME,        // Retour confirmé par l'employé
    TERMINEE,               // Absence terminée et validée

    // Report
    REPORTEE                // Demande reportée
}