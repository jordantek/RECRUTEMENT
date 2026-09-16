package com.tpc.tpcgestpaie.localapp.dto.accessoire;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

public class AllocationCongeDetailDTO {

    private Long id;

    // Infos employé
    private Long employeId;
    private String nom;
    private String prenom;

    // Infos contrat
    private Long contratEmployeId;
    private String contratReference; // par ex. un champ de contrat

    // Infos société
    private Long companyId;
    private String companyName;

    // Infos allocation
    private String moisCalculSalaire;
    private double totalTemps;
    private BigDecimal totalSalaire;
    private BigDecimal salaireJournalierNormal;
    private long nbJours;
    private BigDecimal montantTotal;
    private String bulletinsUtilisesJson;

    // Audit
    private Long addedByUserId;
    private String addedByUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Constructeur complet (ou builder, ou setters)

    public AllocationCongeDetailDTO(Long id, Long employeId, String nom, String prenom,

                                    Long companyId, String companyName,
                                    String moisCalculSalaire, double totalTemps, BigDecimal totalSalaire,
                                    BigDecimal salaireJournalierNormal, long nbJours, BigDecimal montantTotal,
                                    String bulletinsUtilisesJson,
                                    Long addedByUserId, String addedByUserName,
                                    LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.employeId = employeId;
        this.nom = nom;
        this.prenom = prenom;
        this.contratEmployeId = contratEmployeId;
        this.contratReference = contratReference;
        this.companyId = companyId;
        this.companyName = companyName;
        this.moisCalculSalaire = moisCalculSalaire;
        this.totalTemps = totalTemps;
        this.totalSalaire = totalSalaire;
        this.salaireJournalierNormal = salaireJournalierNormal;
        this.nbJours = nbJours;
        this.montantTotal = montantTotal;
        this.bulletinsUtilisesJson = bulletinsUtilisesJson;
        this.addedByUserId = addedByUserId;
        this.addedByUserName = addedByUserName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // Getters et setters ici...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public String getContratReference() {
        return contratReference;
    }

    public void setContratReference(String contratReference) {
        this.contratReference = contratReference;
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

    public String getMoisCalculSalaire() {
        return moisCalculSalaire;
    }

    public void setMoisCalculSalaire(String moisCalculSalaire) {
        this.moisCalculSalaire = moisCalculSalaire;
    }

    public double getTotalTemps() {
        return totalTemps;
    }

    public void setTotalTemps(double totalTemps) {
        this.totalTemps = totalTemps;
    }

    public BigDecimal getTotalSalaire() {
        return totalSalaire;
    }

    public void setTotalSalaire(BigDecimal totalSalaire) {
        this.totalSalaire = totalSalaire;
    }

    public BigDecimal getSalaireJournalierNormal() {
        return salaireJournalierNormal;
    }

    public void setSalaireJournalierNormal(BigDecimal salaireJournalierNormal) {
        this.salaireJournalierNormal = salaireJournalierNormal;
    }

    public long getNbJours() {
        return nbJours;
    }

    public void setNbJours(long nbJours) {
        this.nbJours = nbJours;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getBulletinsUtilisesJson() {
        return bulletinsUtilisesJson;
    }

    public void setBulletinsUtilisesJson(String bulletinsUtilisesJson) {
        this.bulletinsUtilisesJson = bulletinsUtilisesJson;
    }

    public Long getAddedByUserId() {
        return addedByUserId;
    }

    public void setAddedByUserId(Long addedByUserId) {
        this.addedByUserId = addedByUserId;
    }

    public String getAddedByUserName() {
        return addedByUserName;
    }

    public void setAddedByUserName(String addedByUserName) {
        this.addedByUserName = addedByUserName;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
