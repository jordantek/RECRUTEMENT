package com.tpc.tpcgestpaie.localapp.dto.absence;

import com.tpc.tpcgestpaie.localapp.enums.StatutDemandeAbsence;
import com.tpc.tpcgestpaie.localapp.enums.UniteAbsence;
import com.tpc.tpcgestpaie.localapp.model.absence.DemandeAbsence;
import com.tpc.tpcgestpaie.localapp.model.absence.HierarchieValidation;
import com.tpc.tpcgestpaie.localapp.model.absence.SuiviAbsence;
import com.tpc.tpcgestpaie.localapp.model.absence.ValidationNiveau;
import com.tpc.tpcgestpaie.localapp.service.absence.CalculJoursAbsenceService;
import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class DemandeAbsenceResponseDTO {

    private Long id;
    private Long employeId;
    private String employeNom;
    private String employePrenom;
    private String employeMatricule;

    private String typeAbsence;
    private String justificatif;
    private String motifDemande;

    // Dates prévues
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer nombreJours;

    private UniteAbsence uniteAbsence;

    // Heures prévues (optionnel si unité = HEURE)
    private LocalTime heureDepart;
    private LocalTime heureArrivee;
    private Integer nombreHeures;

    private LocalTime heureDepartEffective;
    private LocalTime heureArriveeEffective;
    private Integer nombreHeuresEffectives;

    // Dates effectives
    private LocalDate dateDebutEffective;
    private LocalDate dateFinEffective;
    private Integer nombreJoursEffectifs;

    // Workflow
    private StatutDemandeAbsence statut;
    private Integer niveauValidationEnCours;
    private List<NiveauValidationDTO> hierarchieValidation;

    // Report
    private Boolean estReporte;
    private String motifReport;

    // Métadonnées
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 🆕 Nouveaux champs pour le calcul détaillé des jours
    private Integer joursCalendaires;           // Total jours calendaires
    private Integer joursTravailles;            // Jours travaillés selon config entreprise
    private Integer joursFeriesDansPeriode;     // Total jours fériés dans la période
    private Integer joursFeriesTravailles;      // Jours fériés tombant sur des jours travaillés
    private Integer joursFeriesExclus;          // Alias de joursFeriesTravailles (pour compatibilité)
    private List<String> datesJoursFeries;      // Liste des dates des jours fériés (format ISO)
    private String detailCalcul;                // Description textuelle du calcul

    // 🆕 Informations de suivi
    private SuiviAbsenceDTO suivi;

    // ========================================
    // CONSTRUCTEURS
    // ========================================

    /**
     * Constructeur de base (sans enrichissement)
     */
    public DemandeAbsenceResponseDTO(DemandeAbsence demande) {
        this(demande, null, null, null);
    }

    /**
     * Constructeur avec validations et suivi
     */
    public DemandeAbsenceResponseDTO(DemandeAbsence demande,
                                     List<ValidationNiveau> validations,
                                     SuiviAbsence suivi) {
        this(demande, validations, suivi, null);
    }

    /**
     * Constructeur complet avec calcul des jours
     */
    public DemandeAbsenceResponseDTO(DemandeAbsence demande,
                                     List<ValidationNiveau> validations,
                                     SuiviAbsence suivi,
                                     CalculJoursAbsenceService.CalculJoursAbsenceResultat calcul) {
        mapDemandeBase(demande);

        if (validations != null) {
            // Les validations peuvent être intégrées ici si nécessaire
            // Par exemple dans une liste séparée ou dans la hiérarchie
        }

        if (suivi != null) {
            this.suivi = new SuiviAbsenceDTO(suivi);
        }

        if (calcul != null) {
            mapCalculJours(calcul);
        }
    }

    // ========================================
    // MÉTHODES DE MAPPING PRIVÉES
    // ========================================

    private void mapDemandeBase(DemandeAbsence demande) {
        this.id = demande.getId();

        // Employé
        if (demande.getEmployeDemandeur() != null) {
            this.employeId = demande.getEmployeDemandeur().getId();
            this.employeNom = demande.getEmployeDemandeur().getNom();
            this.employePrenom = demande.getEmployeDemandeur().getPrenom();
            this.employeMatricule = demande.getEmployeDemandeur().getMatricule();
        }

        // Type et motif
        this.typeAbsence = demande.getTypeAbsence() != null ?
                demande.getTypeAbsence().getLibelle() : null;
        this.motifDemande = demande.getMotifDemande();

        // Unité et heures
        this.uniteAbsence = demande.getUniteAbsence();
        this.heureDepart = demande.getHeureDepart();
        this.heureArrivee = demande.getHeureArrivee();
        this.nombreHeures = demande.getNombreHeures();
        this.heureDepartEffective = demande.getHeureDepartEffective();
        this.heureArriveeEffective = demande.getHeureArriveeEffective();
        this.nombreHeuresEffectives = demande.getNombreHeuresEffectives();

        // Dates
        this.dateDebut = demande.getDateDebut();
        this.dateFin = demande.getDateFin();
        this.nombreJours = demande.getNombreJours();

        this.dateDebutEffective = demande.getDateDebutEffective();
        this.dateFinEffective = demande.getDateFinEffective();
        this.nombreJoursEffectifs = demande.getNombreJoursEffectifs();

        // Workflow
        this.statut = demande.getStatut();
        this.niveauValidationEnCours = demande.getNiveauValidationEnCours();

        // Hiérarchie de validation
        if (demande.getHierarchieValidation() != null) {
            this.hierarchieValidation = demande.getHierarchieValidation().getNiveaux()
                    .stream()
                    .map(NiveauValidationDTO::new)
                    .collect(Collectors.toList());
        }

        // Report
        this.estReporte = demande.getEstReporte();
        this.motifReport = demande.getMotifReport();

        // Métadonnées
        this.createdAt = demande.getCreatedAt();
        this.updatedAt = demande.getUpdatedAt();
    }

    private void mapCalculJours(CalculJoursAbsenceService.CalculJoursAbsenceResultat calcul) {
        this.joursCalendaires = calcul.getJoursCalendaires();
        this.joursTravailles = calcul.getJoursTravailles();
        this.joursFeriesDansPeriode = calcul.getJoursFeriesDansPeriode();
        this.joursFeriesTravailles = calcul.getJoursFeriesTravailles();
        this.joursFeriesExclus = calcul.getJoursFeriesTravailles(); // Alias pour compatibilité

        // Conversion des dates en String ISO (YYYY-MM-DD)
        if (calcul.getDatesJoursFeries() != null) {
            this.datesJoursFeries = calcul.getDatesJoursFeries()
                    .stream()
                    .map(LocalDate::toString)
                    .collect(Collectors.toList());
        }

        // Détail du calcul
        this.detailCalcul = String.format(
                "%d jours calendaires → %d jours travaillés - %d jours fériés = %d jours d'absence réels",
                calcul.getJoursCalendaires(),
                calcul.getJoursTravailles(),
                calcul.getJoursFeriesTravailles(),
                calcul.getJoursAbsenceReels()
        );
    }

    // ========================================
    // CLASSES INTERNES DTO
    // ========================================

    @Data
    public static class NiveauValidationDTO {
        private Integer ordre;
        private Long employeSuperieurId;
        private String fonction;
        private String nomComplet;
        private String statut;
        private String dateAction;
        private String commentaire;
        private String raisonRejet;
        private GlobalEnums.ConditionAcceptationConge conditionAcceptation;

        public NiveauValidationDTO(HierarchieValidation.NiveauValidateur niveau) {
            this.ordre = niveau.getOrdre();
            this.employeSuperieurId = niveau.getEmployeSuperieurId();
            this.fonction = niveau.getFonction();
            this.nomComplet = niveau.getNomComplet();
            this.statut = niveau.getStatut();
            this.dateAction = niveau.getDateAction();
            this.commentaire = niveau.getCommentaire();
            this.raisonRejet = niveau.getRaisonRejet();
            this.conditionAcceptation = niveau.getConditionAcceptationConge();
        }
    }

    @Data
    public static class SuiviAbsenceDTO {
        private Long id;
        private LocalDate dateDepartPrevue;
        private LocalDate dateDepartEffective;
        private LocalDate dateRetourPrevue;
        private LocalDate dateRetourEffective;
        private Integer joursPrevu;
        private Integer joursEffectifs;
        private Integer ecartJours;
        private Boolean departConfirme;
        private Boolean retourConfirme;
        private LocalDateTime dateConfirmationDepart;
        private LocalDateTime dateConfirmationRetour;

        public SuiviAbsenceDTO(SuiviAbsence suivi) {
            this.id = suivi.getId();
            this.dateDepartPrevue = suivi.getDateDepartPrevue();
            this.dateDepartEffective = suivi.getDateDepartEffective();
            this.dateRetourPrevue = suivi.getDateRetourPrevue();
            this.dateRetourEffective = suivi.getDateRetourEffective();
            this.joursPrevu = suivi.getJoursPrevu();
            this.joursEffectifs = suivi.getJoursEffectifs();
            this.ecartJours = suivi.getEcartJours();
            this.departConfirme = suivi.getDepartConfirme();
            this.retourConfirme = suivi.getRetourConfirme();
            this.dateConfirmationDepart = suivi.getDateConfirmationDepart();
            this.dateConfirmationRetour = suivi.getDateConfirmationRetour();
        }
    }
}