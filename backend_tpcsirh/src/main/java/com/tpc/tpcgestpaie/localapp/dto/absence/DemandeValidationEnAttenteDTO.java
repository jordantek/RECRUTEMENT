package com.tpc.tpcgestpaie.localapp.dto.absence;

import com.tpc.tpcgestpaie.localapp.enums.StatutDemandeAbsence;
import com.tpc.tpcgestpaie.localapp.enums.UniteAbsence;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class DemandeValidationEnAttenteDTO {

    // Informations de base de la demande
    private Long demandeId;
    private String nomDemandeur;
    private String prenomDemandeur;
    private String matriculeDemandeur;
    private String fonctionDemandeur;

    // Détails de l'absence
    private String typeAbsence;
    private String motifDemande;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private UniteAbsence uniteAbsence;

    private Integer nombreJours;
    private Integer nombreHeures;

    // Statut et dates
    private StatutDemandeAbsence statutDemande;


    // 🔴 INFORMATIONS CRUCIALES SUR LA VALIDATION
    private Integer niveauValidationActuel;           // Niveau en cours (1, 2, 3...)
    private Integer totalNiveaux;                      // Nombre total de niveaux
    private String statutNiveauActuel;                 // EN_ATTENTE, APPROUVE, etc.


    // Information sur le validateur actuel (celui qui doit valider maintenant)
    private String nomValidateurActuel;
    private String fonctionValidateurActuel;
    private Long idValidateurActuel;

    // 🟢 INFORMATION SPÉCIALE POUR L'UTILISATEUR CONNECTÉ
    private boolean estMonTourDeValider;              // TRUE si c'est lui le prochain validateur
    private String messageAction;                      // Message personnalisé ("C'est votre tour", "En attente de M. X", etc.)

    // Historique des validations déjà effectuées
    private List<HistoriqueValidationDTO> historiqueValidations;

    // Prochains validateurs (pour voir la suite du workflow)
    private List<ProchainValidateurDTO> prochainsValidateurs;

    // Date de création de la validation (pour le tri)
    private LocalDateTime dateCreationValidation;

    // Date de création de la demande (pour info)
    private LocalDateTime dateCreationDemande;

    // Indicateur visuel pour l'UI
    private boolean estRecent;

    // 🆕 NOUVEAUX CHAMPS pour l'historique personnel
    private boolean aDejaValide;           // true si l'utilisateur a déjà approuvé/rejeté cette demande
    private String monStatutValidation;    // "APPROUVE", "REJETE", ou null si pas encore validé
    private LocalDateTime maDateValidation; // quand il a validé
    private String monCommentaire;         // son commentaire de validation

    // 🆕 Indicateurs pour filtrage facile
    private boolean estEnAttenteDeMoi;     // true si c'est mon tour ET pas encore validé
    private boolean estTermineePourMoi;    // true si j'ai déjà traité cette demande

    // 🆕 Pour regroupement frontend
    private String categorie;// "A_VALIDER", "VALIDE_PAR_MOI", "EN_ATTENTE_AUTRE", "TERMINEE"

    // 🆕 NOUVEAU : Validateurs détaillés
    private List<ValidateurInfoDTO> tousLesValidateurs;  // Toute la chaîne avec statut

    // 🆕 Condition d'acceptation (visible uniquement si approuvé finalement)
    private String conditionAcceptation;  // "A_DEDUIRE_DES_CONGES", etc.
    private String conditionAcceptationLibelle;  // "À déduire des congés"

    // 🆕 Indicateur pour le frontend
    private boolean estValidationFinale;  // true si c'était le dernier niveau

    @Data
    @Builder
    public static class ValidateurInfoDTO {
        private Integer ordre;
        private String nom;
        private String fonction;
        private String statut;           // EN_ATTENTE, APPROUVE, REJETE
        private boolean cestMonTour;     // true si c'est le niveau actuel
        private boolean cestMoi;         // true si c'est l'utilisateur connecté
        private LocalDateTime dateValidation;
        private String commentaire;
    }

    @Data
    @Builder
    public static class HistoriqueValidationDTO {
        private Integer ordre;
        private String nomValidateur;
        private String fonctionValidateur;
        private String statut; // APPROUVE
        private LocalDateTime dateValidation;
        private String commentaire;
        private String raisonRejet;
    }

    @Data
    @Builder
    public static class ProchainValidateurDTO {
        private Integer ordre;
        private String nomValidateur;
        private String fonctionValidateur;
        private String statut; // EN_ATTENTE
    }
}