package com.tpc.tpcgestpaie.localapp.util;

/**
 * Définition centralisée de tous les types d'événements RH.
 *
 * Cette classe permet :
 * - d’éviter les chaînes de caractères en dur
 * - de normaliser les types d’événements
 * - de faciliter les statistiques, alertes et automatisations
 *
 * 👉 Utilisée lors de la création automatique des HrEvent
 */
public class HrEventTypes {
    // =========================
    // 📄 CONTRAT
    // =========================
    public static final String CONTRACT_START = "DEBUT_DU_CONTRAT";                // Début du contrat
    public static final String CONTRACT_END = "FIN_DU_CONTRAT";                    // Fin du contrat
    public static final String CONTRACT_RENEWAL = "RENOUVELLEMENT_CONTRAT";            // Renouvellement du contrat
    public static final String CONTRACT_ANNIVERSARY = "ANNIVERSAIRE_CONTRAT";    // Anniversaire du contrat
    public static final String TRIAL_PERIOD_END = "FIN_PERIODE_DESSAI";            // Fin de la période d’essai

    // =========================
    // 👤 EMPLOYÉ
    // =========================
    public static final String BIRTHDAY_EMPLOYEE = "ANNIVERSAIRE_EMPLOYE";           // Anniversaire de l’employé
    public static final String EMPLOYEE_ONBOARDING = "ONBOARDING_EMPLOYE";       // Arrivée de l’employé
    public static final String EMPLOYEE_OFFBOARDING = "OFFBOARDING_EMPLOYE";     // Départ de l’employé

    // =========================
    // 🕒 ABSENCES & CONGÉS
    // =========================
    public static final String ABSENCE_START = "DEBUT_ABSENCE";                   // Début d’une absence
    public static final String ABSENCE_END = "FIN_ABSENCE";                       // Fin d’une absence
    public static final String LEAVE_START = "DEBUT_CONGE";                       // Début d’un congé
    public static final String LEAVE_END = "FIN_CONGE";                           // Fin d’un congé

    // =========================
    // 💰 PAIE & AVANTAGES
    // =========================
    public static final String SALARY_REVIEW = "REVISION_SALARIALE";                   // Révision salariale
    public static final String BONUS_PAYMENT = "VERSEMENT_PRIME";                   // Versement d’une prime

    // =========================
    // 🎓 FORMATION & ÉVALUATION
    // =========================
    public static final String TRAINING_START = "DEBUT_FORMATION";                 // Début d’une formation
    public static final String TRAINING_END = "FIN_FORMATION";                     // Fin d’une formation
    public static final String PERFORMANCE_REVIEW = "EVALUATION_PERFORMANCE";         // Évaluation de performance

    // =========================
    // ⚖️ ADMINISTRATIF
    // =========================
    public static final String DOCUMENT_EXPIRY = "EXPIRATION_DOCUMENT";               // Expiration d’un document
    public static final String MEDICAL_VISIT_DUE = "VISITE_MEDICALE";            // Visite médicale obligatoire
}


