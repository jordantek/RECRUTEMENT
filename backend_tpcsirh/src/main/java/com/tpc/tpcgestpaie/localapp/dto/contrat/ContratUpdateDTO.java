package com.tpc.tpcgestpaie.localapp.dto.contrat;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class ContratUpdateDTO {

    private Long contratEmployeId;
    private Long categorieEmployeId;
    private String typeContrat;
    private Long departementId;
    private Long posteId;
    private LocalDate dateFinContrat;
    private Double salaireBase;
    private Double salaireBrut;
    private String typeModification;
    private String motif;
    private MultipartFile preuve;
    private LocalDate dateEffet;

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

    // 💬 Commentaire libre
    private String commentaire;

    // ============================================
    // GETTERS & SETTERS EXISTANTS
    // ============================================

    public MultipartFile getPreuve() {
        return preuve;
    }

    public void setPreuve(MultipartFile preuve) {
        this.preuve = preuve;
    }

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public Long getCategorieEmployeId() {
        return categorieEmployeId;
    }

    public void setCategorieEmployeId(Long categorieEmployeId) {
        this.categorieEmployeId = categorieEmployeId;
    }

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }

    public Long getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Long departementId) {
        this.departementId = departementId;
    }

    public Long getPosteId() {
        return posteId;
    }

    public void setPosteId(Long posteId) {
        this.posteId = posteId;
    }

    public LocalDate getDateFinContrat() {
        return dateFinContrat;
    }

    public void setDateFinContrat(LocalDate dateFinContrat) {
        this.dateFinContrat = dateFinContrat;
    }

    public Double getSalaireBase() {
        return salaireBase;
    }

    public void setSalaireBase(Double salaireBase) {
        this.salaireBase = salaireBase;
    }

    public Double getSalaireBrut() {
        return salaireBrut;
    }

    public void setSalaireBrut(Double salaireBrut) {
        this.salaireBrut = salaireBrut;
    }

    public String getTypeModification() {
        return typeModification;
    }

    public void setTypeModification(String typeModification) {
        this.typeModification = typeModification;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDate getDateEffet() {
        return dateEffet;
    }

    public void setDateEffet(LocalDate dateEffet) {
        this.dateEffet = dateEffet;
    }

    // ============================================
    // GETTERS & SETTERS NOUVEAUX
    // ============================================

    public String getLieuContrat() {
        return lieuContrat;
    }

    public void setLieuContrat(String lieuContrat) {
        this.lieuContrat = lieuContrat;
    }

    public String getQualificationProfessionnelle() {
        return qualificationProfessionnelle;
    }

    public void setQualificationProfessionnelle(String qualificationProfessionnelle) {
        this.qualificationProfessionnelle = qualificationProfessionnelle;
    }

    public String getTravailAFaire() {
        return travailAFaire;
    }

    public void setTravailAFaire(String travailAFaire) {
        this.travailAFaire = travailAFaire;
    }

    public String getHoraireTravail() {
        return horaireTravail;
    }

    public void setHoraireTravail(String horaireTravail) {
        this.horaireTravail = horaireTravail;
    }

    public String getNatureJuridiqueEmployeur() {
        return natureJuridiqueEmployeur;
    }

    public void setNatureJuridiqueEmployeur(String natureJuridiqueEmployeur) {
        this.natureJuridiqueEmployeur = natureJuridiqueEmployeur;
    }

    public String getAncienneSituation() {
        return ancienneSituation;
    }

    public void setAncienneSituation(String ancienneSituation) {
        this.ancienneSituation = ancienneSituation;
    }

    public String getNouvelleSituation() {
        return nouvelleSituation;
    }

    public void setNouvelleSituation(String nouvelleSituation) {
        this.nouvelleSituation = nouvelleSituation;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}