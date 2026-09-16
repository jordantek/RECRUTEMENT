package com.tpc.tpcgestpaie.localapp.model.absence;

import com.tpc.tpcgestpaie.localapp.enums.StatutDemandeAbsence;
import com.tpc.tpcgestpaie.localapp.enums.UniteAbsence;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.model.conge.RepartitionConsommation;
import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "demande_absences")
@Data
public class DemandeAbsence {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 // === INFORMATIONS DE BASE ===
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "employe_demandeur_id", nullable = false)
 private Employe employeDemandeur;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "contrat_employe_id")
 private ContratEmploye contratEmploye;

 @Column(name = "company_id")
 private Long companyId;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "type_absence_id")
 private TypeAbsence typeAbsence;

 @Lob
 @Column(name = "motif_demande")
 private String motifDemande;

 @Column(name = "piece_justificative_url")
 private String pieceJustificatif;

 // === DATES PRÉVUES ===
 @Column(name = "date_debut_prevue")
 private LocalDate dateDebut;

 @Column(name = "date_fin_prevue")
 private LocalDate dateFin;

 @Column(name = "nombre_jours_prevu")
 private Integer nombreJours;

 // === UNITE DE L'ABSENCE ===
 @Enumerated(EnumType.STRING)
 @Column(name = "unite_absence", nullable = false)
 private UniteAbsence uniteAbsence;

 // === HEURES PRÉVUES (POUR ABSENCE HORAIRE) ===
 @Column(name = "heure_depart")
 private LocalTime heureDepart;

 @Column(name = "heure_arrivee")
 private LocalTime heureArrivee;

 @Column(name = "nombre_heures_prevu")
 private Integer nombreHeures;

 // === HEURES EFFECTIVES ===
 @Column(name = "heure_depart_effective")
 private LocalTime heureDepartEffective;

 @Column(name = "heure_arrivee_effective")
 private LocalTime heureArriveeEffective;

 @Column(name = "nombre_heures_effectives")
 private Integer nombreHeuresEffectives;

 // === DATES EFFECTIVES (NOUVEAU) ===
 @Column(name = "date_debut_effective")
 private LocalDate dateDebutEffective;

 @Column(name = "date_fin_effective")
 private LocalDate dateFinEffective;

 @Column(name = "nombre_jours_effectifs")
 private Integer nombreJoursEffectifs;

 // === HIÉRARCHIE DE VALIDATION (JSON DEPUIS employe_superieur) ===
 @Type(JsonType.class)
 @Column(name = "hierarchie_validation", columnDefinition = "json")
 private HierarchieValidation hierarchieValidation;

 // === WORKFLOW DE VALIDATION ===
 @Column(name = "niveau_validation_en_cours")
 private Integer niveauValidationEnCours = 1; // Commence au niveau 1

 @Enumerated(EnumType.STRING)
 @Column(nullable = false)
 private StatutDemandeAbsence statut = StatutDemandeAbsence.EN_ATTENTE_VALIDATION;

 // === SUIVI DES VALIDATIONS ===
 @OneToMany(mappedBy = "demandeAbsence", cascade = CascadeType.ALL)
 private List<ValidationNiveau> validations;

 // === NOTIFICATIONS ===
 @Column(name = "date_notification_depart")
 private LocalDateTime dateNotificationDepart;

 @Column(name = "date_notification_retour")
 private LocalDateTime dateNotificationRetour;

 // === REPORT ===
 @Column(name = "est_reporte")
 private Boolean estReporte = false;

 private String motifReport;
 private LocalDateTime dateReport;

 // === ANNULATION ===
 private LocalDateTime dateAnnulation;
 private String motifAnnulation;

 // === MÉTADONNÉES ===
 @Column(name = "created_at", updatable = false)
 private LocalDateTime createdAt;

 private LocalDateTime updatedAt;

 // 🆕 NOUVEAUX CHAMPS pour gestion solde
 @Column(name = "attente_deduction_solde")
 private Boolean attenteDeductionSolde = false;

 @Enumerated(EnumType.STRING)
 @Column(name = "mode_jouissance_effectif")
 private GlobalEnums.ModeJouissanceConge modeJouissanceEffectif;

 @Column(name = "jours_effectifs_deduits", precision = 10, scale = 2)
 private BigDecimal joursEffectifsDeduits;

 @Column(name = "date_deduction_solde")
 private LocalDate dateDeductionSolde;

 @PrePersist
 protected void onCreate() {
  createdAt = LocalDateTime.now();
  updatedAt = createdAt;
 }

 @PreUpdate
 protected void onUpdate() {
  updatedAt = LocalDateTime.now();
 }

 /**
  * Montant total alloué pour ce congé (calculé lors confirmation départ)
  * C'est la somme des montants consommés sur les différents mois
  */
 @Column(name = "montant_allocation", precision = 15, scale = 2)
 private BigDecimal montantAllocation;

 /**
  * Date/heure de la confirmation de départ effective
  * C'est à ce moment que les provisions sont consommées
  */
 @Column(name = "date_confirmation_depart_effective")
 private LocalDateTime dateConfirmationDepartEffective;

 /**
  * Flag pour éviter la double consommation
  */
 @Column(name = "solde_deduit")
 private Boolean soldeDeduit = false;

 // ====== LIEN VERS RÉPARTITIONS ======

 /**
  * Liste des consommations de provisions liées à cette demande
  * Permet de savoir exactement quels mois ont été utilisés
  */
 @OneToMany(mappedBy = "demandeAbsence", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
 private List<RepartitionConsommation> repartitions;

 // ====== CHAMPS EXISTANTS (conservés) ======
 // ... attenteDeductionSolde, modeJouissanceEffectif, etc. ...

 // ====== MÉTHODES UTILITAIRES ======

 /**
  * Vérifie si les provisions ont déjà été consommées pour cette demande
  */
 public boolean isSoldeDejaDeduit() {
  return Boolean.TRUE.equals(soldeDeduit);
 }

 /**
  * Marque la demande comme ayant consommé des provisions
  */
 public void marquerSoldeDeduit(BigDecimal montant) {
  this.soldeDeduit = true;
  this.montantAllocation = montant;
  this.dateDeductionSolde = LocalDate.now();
  this.dateConfirmationDepartEffective = LocalDateTime.now();
 }

 /**
  * Annule la consommation de provisions (restauration)
  */
 public void annulerDeductionSolde() {
  this.soldeDeduit = false;
  this.montantAllocation = null;
  this.dateDeductionSolde = null;
  // dateConfirmationDepartEffective conservée pour historique
 }
}