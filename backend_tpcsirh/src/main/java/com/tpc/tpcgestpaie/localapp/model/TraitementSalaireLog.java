package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "traitement_salaire_logs")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraitementSalaireLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(nullable = true)
    private Long departementId;

    @Column(nullable = true)
    private String departementName;

    @Column(nullable = false, length = 7) // ex: "2025-07"
    private String mois;

    @Column(name = "date_traitement", nullable = true)
    private LocalDateTime dateTraitement;

    @Column(nullable = true)
    private String statut;

    @Column(columnDefinition="TEXT")
    private String message;

    @Column(name = "declencheur_id")
    private Long declencheurId;

    @Column(name = "verificateur_id")
    private Long verificateurId;

    @Column(name = "date_verification")
    private LocalDateTime dateVerification;

    @Column(name = "validateur_id")
    private Long validateurId;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "rejet_id")
    private Long rejetParId;

    @Column(name = "date_rejet")
    private LocalDateTime dateRejet;

    @Column(name = "motif_rejet", length = 255)
    private String motifRejet;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Long departementId) {
        this.departementId = departementId;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDeclencheurId() {
        return declencheurId;
    }

    public void setDeclencheurId(Long declencheurId) {
        this.declencheurId = declencheurId;
    }

    public Long getVerificateurId() {
        return verificateurId;
    }

    public void setVerificateurId(Long verificateurId) {
        this.verificateurId = verificateurId;
    }

    public LocalDateTime getDateVerification() {
        return dateVerification;
    }

    public void setDateVerification(LocalDateTime dateVerification) {
        this.dateVerification = dateVerification;
    }

    public Long getValidateurId() {
        return validateurId;
    }

    public void setValidateurId(Long validateurId) {
        this.validateurId = validateurId;
    }

    public LocalDateTime getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(LocalDateTime dateValidation) {
        this.dateValidation = dateValidation;
    }

    public Long getRejetParId() {
        return rejetParId;
    }

    public void setRejetParId(Long rejetParId) {
        this.rejetParId = rejetParId;
    }

    public LocalDateTime getDateRejet() {
        return dateRejet;
    }

    public void setDateRejet(LocalDateTime dateRejet) {
        this.dateRejet = dateRejet;
    }

    public String getMotifRejet() {
        return motifRejet;
    }

    public void setMotifRejet(String motifRejet) {
        this.motifRejet = motifRejet;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDepartementName() {
        return departementName;
    }

    public void setDepartementName(String departementName) {
        this.departementName = departementName;
    }
}
