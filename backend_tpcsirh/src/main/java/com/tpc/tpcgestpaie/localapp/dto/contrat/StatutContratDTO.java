package com.tpc.tpcgestpaie.localapp.dto.contrat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StatutContratDTO {

    private Long id;
    private Long contratEmployeId;
    private Long categorieEmployeId;
    private String typeContrat;
    private Long departementId;
    private Long posteId;
    private LocalDate dateFinContrat;
    private Double salaireBase;
    private Double salaireBrut;

    // ============================================
    // NOUVEAUX ATTRIBUTS POUR AVENANTS
    // ============================================

    // 📍 Lieu du contrat
    private String lieuContrat;

    // 🎓 Qualification professionnelle
    private String qualificationProfessionnelle;

    // 📋 Travail à faire
    private String travailAFaire;

    // ⏰ Horaire de travail
    private String horaireTravail;

    // 🏢 Nature juridique de l'employeur
    private String natureJuridiqueEmployeur;

    // 📝 Situations pour avenants
    private String ancienneSituation;
    private String nouvelleSituation;

    // 💬 Commentaire
    private String commentaire;

    // ENUM exposé sous forme de String
    private String typeModification;

    private String motif;
    private String preuve;

    private boolean actif;
    private boolean initialisation;

    private LocalDate dateEffet;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 🔥 Snapshot JSON pour consultation
    private String snapshotJson;

    // ============================================
    // GETTERS & SETTERS
    // ============================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getContratEmployeId() { return contratEmployeId; }
    public void setContratEmployeId(Long contratEmployeId) { this.contratEmployeId = contratEmployeId; }

    public Long getCategorieEmployeId() { return categorieEmployeId; }
    public void setCategorieEmployeId(Long categorieEmployeId) { this.categorieEmployeId = categorieEmployeId; }

    public String getTypeContrat() { return typeContrat; }
    public void setTypeContrat(String typeContrat) { this.typeContrat = typeContrat; }

    public Long getDepartementId() { return departementId; }
    public void setDepartementId(Long departementId) { this.departementId = departementId; }

    public Long getPosteId() { return posteId; }
    public void setPosteId(Long posteId) { this.posteId = posteId; }

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

    public String getTypeModification() { return typeModification; }
    public void setTypeModification(String typeModification) { this.typeModification = typeModification; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getSnapshotJson() { return snapshotJson; }
    public void setSnapshotJson(String snapshotJson) { this.snapshotJson = snapshotJson; }

    // ============================================
    // CLASSE INTERNE (conservée pour compatibilité)
    // ============================================

    public class StatutContratResponseDTO {
        private Long id;
        private Long contratEmployeId;
        private String typeModification;
        private String motif;
        private String preuve;
        private LocalDate dateEffet;
        private String snapshotJson;

        // Getters & Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getContratEmployeId() { return contratEmployeId; }
        public void setContratEmployeId(Long contratEmployeId) { this.contratEmployeId = contratEmployeId; }

        public String getTypeModification() { return typeModification; }
        public void setTypeModification(String typeModification) { this.typeModification = typeModification; }

        public String getMotif() { return motif; }
        public void setMotif(String motif) { this.motif = motif; }

        public String getPreuve() { return preuve; }
        public void setPreuve(String preuve) { this.preuve = preuve; }

        public LocalDate getDateEffet() { return dateEffet; }
        public void setDateEffet(LocalDate dateEffet) { this.dateEffet = dateEffet; }

        public String getSnapshotJson() { return snapshotJson; }
        public void setSnapshotJson(String snapshotJson) { this.snapshotJson = snapshotJson; }
    }
}