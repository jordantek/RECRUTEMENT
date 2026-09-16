package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "statut_contrats")
public class StatutContrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Contrat de base
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_employe_id", nullable = false)
    private ContratEmploye contratEmploye;

    // ============================================
    // CHAMPS SUSCEPTIBLES DE CHANGER (existants)
    // ============================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_employe_id")
    private CategorieEmploye categorieEmploye;

    private String typeContrat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id")
    private Departement departement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poste_id")
    private Poste poste;

    private LocalDate dateFinContrat;

    private Double salaireBase;
    private Double salaireBrut;

    // ============================================
    // NOUVEAUX CHAMPS POUR AVENANTS (tout ce qui peut changer)
    // ============================================

    // 📍 Lieu du contrat
    @Column(name = "lieu_contrat", length = 255)
    private String lieuContrat;

    // 🎓 Qualification professionnelle
    @Column(name = "qualification_professionnelle", length = 255)
    private String qualificationProfessionnelle;

    // 📋 Travail à faire (missions)
    @Lob
    @Column(name = "travail_a_faire", columnDefinition = "TEXT")
    private String travailAFaire;

    // ⏰ Horaire de travail
    @Lob
    @Column(name = "horaire_travail", columnDefinition = "TEXT")
    private String horaireTravail;

    // 🏢 Nature juridique de l'employeur
    @Column(name = "nature_juridique_employeur", length = 100)
    private String natureJuridiqueEmployeur;

    // 📝 Situations pour traçabilité
    @Lob
    @Column(name = "ancienne_situation", columnDefinition = "TEXT")
    private String ancienneSituation;

    @Lob
    @Column(name = "nouvelle_situation", columnDefinition = "TEXT")
    private String nouvelleSituation;

    // 💬 Commentaire
    @Lob
    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    // ============================================
    // INFOS AVENANT (existants)
    // ============================================

    @Enumerated(EnumType.STRING)
    private TypeModification typeModification;

    private String motif;
    private String preuve; // fichier justificatif

    private boolean actif;          // un seul actif à la fois
    private boolean initialisation; // true uniquement pour la première version

    // Date de prise d'effet
    private LocalDate dateEffet;

    // 🔥 Snapshot complet du contrat (JSON de secours)
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String snapshotJson;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ============================================
    // ENUM TYPE MODIFICATION (complété)
    // ============================================

    public enum TypeModification {
        // Existants
        CATEGORIE_EMPLOYE,
        TYPE_CONTRAT,
        DEPARTEMENT_POSTE,
        DATE_FIN_CONTRAT,
        AUGMENTATION,

        // Nouveaux types avenants
        LIEU_TRAVAIL,                 // 📍 Changement de lieu
        QUALIFICATION_PROFESSIONNELLE, // 🎓 Nouvelle qualification
        TRAVAIL_A_FAIRE,              // 📋 Nouvelles missions
        HORAIRE_TRAVAIL,              // ⏰ Modification horaires
        NATURE_JURIDIQUE_EMPLOYEUR,   // 🏢 Changement structure juridique
        SALAIRE_AVANTAGES,            // 💰 Changement salaire/bénéfices
        DUREE_CONTRAT,                // 📅 Renouvellement, transformation CDI
        SUSPENSION,                   // ⏸️ Arrêt temporaire
        REPRISE,                      // ▶️ Fin de suspension
        AUTRE                         // 📝 Autre modification
    }

    // ============================================
    // GETTERS & SETTERS (tous)
    // ============================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ContratEmploye getContratEmploye() { return contratEmploye; }
    public void setContratEmploye(ContratEmploye contratEmploye) { this.contratEmploye = contratEmploye; }

    public CategorieEmploye getCategorieEmploye() { return categorieEmploye; }
    public void setCategorieEmploye(CategorieEmploye categorieEmploye) { this.categorieEmploye = categorieEmploye; }

    public String getTypeContrat() { return typeContrat; }
    public void setTypeContrat(String typeContrat) { this.typeContrat = typeContrat; }

    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement departement) { this.departement = departement; }

    public Poste getPoste() { return poste; }
    public void setPoste(Poste poste) { this.poste = poste; }

    public LocalDate getDateFinContrat() { return dateFinContrat; }
    public void setDateFinContrat(LocalDate dateFinContrat) { this.dateFinContrat = dateFinContrat; }

    public Double getSalaireBase() { return salaireBase; }
    public void setSalaireBase(Double salaireBase) { this.salaireBase = salaireBase; }

    public Double getSalaireBrut() { return salaireBrut; }
    public void setSalaireBrut(Double salaireBrut) { this.salaireBrut = salaireBrut; }

    // --- NOUVEAUX GETTERS/SETTERS ---
    public String getLieuContrat() { return lieuContrat; }
    public void setLieuContrat(String lieuContrat) { this.lieuContrat = lieuContrat; }

    public String getQualificationProfessionnelle() { return qualificationProfessionnelle; }
    public void setQualificationProfessionnelle(String qualificationProfessionnelle) { this.qualificationProfessionnelle = qualificationProfessionnelle; }

    public String getTravailAFaire() { return travailAFaire; }
    public void setTravailAFaire(String travailAFaire) { this.travailAFaire = travailAFaire; }

    public String getHoraireTravail() { return horaireTravail; }
    public void setHoraireTravail(String horaireTravail) { this.horaireTravail = horaireTravail; }

    public String getNatureJuridiqueEmployeur() { return natureJuridiqueEmployeur; }
    public void setNatureJuridiqueEmployeur(String natureJuridiqueEmployeur) { this.natureJuridiqueEmployeur = natureJuridiqueEmployeur; }

    public String getAncienneSituation() { return ancienneSituation; }
    public void setAncienneSituation(String ancienneSituation) { this.ancienneSituation = ancienneSituation; }

    public String getNouvelleSituation() { return nouvelleSituation; }
    public void setNouvelleSituation(String nouvelleSituation) { this.nouvelleSituation = nouvelleSituation; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    // --- EXISTANTS ---
    public TypeModification getTypeModification() { return typeModification; }
    public void setTypeModification(TypeModification typeModification) { this.typeModification = typeModification; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getPreuve() { return preuve; }
    public void setPreuve(String preuve) { this.preuve = preuve; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public boolean isInitialisation() { return initialisation; }
    public void setInitialisation(boolean initialisation) { this.initialisation = initialisation; }

    public LocalDate getDateEffet() { return dateEffet; }
    public void setDateEffet(LocalDate dateEffet) { this.dateEffet = dateEffet; }

    public String getSnapshotJson() { return snapshotJson; }
    public void setSnapshotJson(String snapshotJson) { this.snapshotJson = snapshotJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}