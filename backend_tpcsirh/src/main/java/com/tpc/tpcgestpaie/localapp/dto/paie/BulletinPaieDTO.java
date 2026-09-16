package com.tpc.tpcgestpaie.localapp.dto.paie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.dto.TraitementSalaire.ApercuSalaireDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.Banque;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BulletinPaieDTO {
    private Long id;

    private LocalDate dateCalculSalaire;
    private String mois;

    private Long contratEmployeId;
    private Long employeId;
    private EmployeDTO employe;
    private Long companyId;
    private Company company;
    private Long banqueId;
    private Banque banque;

    private Long addedById;

    private double tempsTravail;

    private BigDecimal salaireBrut;
    private BigDecimal salaireBrutArrondi;
    private BigDecimal montantCnss;
    private int nombreEnfant;
    private BigDecimal montantIpts;
    private BigDecimal montantAib;
    private BigDecimal totalRetenue;
    private BigDecimal salaireNet;
    private BigDecimal autreAvantage;
    private BigDecimal autreRetenue;
    private BigDecimal montantCnssEmployeur;
    private BigDecimal montantVps;
    private BigDecimal totalChargePatronale;
    private BigDecimal netAPayer;

    private String numeroCompteEmploye;

    private double congePris;
    private double soldeConge;

    private String signataire;
    private String statut;

    private LocalDateTime validatedAt;
    private Long validatedBy;
    private User validatedByUser;

    private String description;
    private ApercuSalaireDTO jsonDescription;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;


    private Double taxeRadiophonique;
    private Double taxeTelevisuel;
    private String departement;


    private double primesExceptionnelles;
    private double treiziemeMois;
    private double salaireBrutMoisPasse;
    private double allocationConge;
    private double primeAnciennete;


    public Double getTaxeRadiophonique() {
        return taxeRadiophonique;
    }

    public void setTaxeRadiophonique(Double taxeRadiophonique) {
        this.taxeRadiophonique = taxeRadiophonique;
    }

    public Double getTaxeTelevisuel() {
        return taxeTelevisuel;
    }

    public void setTaxeTelevisuel(Double taxeTelevisuel) {
        this.taxeTelevisuel = taxeTelevisuel;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public double getPrimesExceptionnelles() {
        return primesExceptionnelles;
    }

    public void setPrimesExceptionnelles(double primesExceptionnelles) {
        this.primesExceptionnelles = primesExceptionnelles;
    }

    public double getTreiziemeMois() {
        return treiziemeMois;
    }

    public void setTreiziemeMois(double treiziemeMois) {
        this.treiziemeMois = treiziemeMois;
    }

    public double getSalaireBrutMoisPasse() {
        return salaireBrutMoisPasse;
    }

    public void setSalaireBrutMoisPasse(double salaireBrutMoisPasse) {
        this.salaireBrutMoisPasse = salaireBrutMoisPasse;
    }

    public double getAllocationConge() {
        return allocationConge;
    }

    public void setAllocationConge(double allocationConge) {
        this.allocationConge = allocationConge;
    }

    public double getPrimeAnciennete() {
        return primeAnciennete;
    }

    public void setPrimeAnciennete(double primeAnciennete) {
        this.primeAnciennete = primeAnciennete;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateCalculSalaire() {
        return dateCalculSalaire;
    }

    public void setDateCalculSalaire(LocalDate dateCalculSalaire) {
        this.dateCalculSalaire = dateCalculSalaire;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getBanqueId() {
        return banqueId;
    }

    public void setBanqueId(Long banqueId) {
        this.banqueId = banqueId;
    }

    public Long getAddedById() {
        return addedById;
    }

    public void setAddedById(Long addedById) {
        this.addedById = addedById;
    }

    public double getTempsTravail() {
        return tempsTravail;
    }

    public void setTempsTravail(double tempsTravail) {
        this.tempsTravail = tempsTravail;
    }

    public BigDecimal getSalaireBrut() {
        return salaireBrut;
    }

    public void setSalaireBrut(BigDecimal salaireBrut) {
        this.salaireBrut = salaireBrut;
    }

    public BigDecimal getSalaireBrutArrondi() {
        return salaireBrutArrondi;
    }

    public void setSalaireBrutArrondi(BigDecimal salaireBrutArrondi) {
        this.salaireBrutArrondi = salaireBrutArrondi;
    }

    public BigDecimal getMontantCnss() {
        return montantCnss;
    }

    public void setMontantCnss(BigDecimal montantCnss) {
        this.montantCnss = montantCnss;
    }

    public int getNombreEnfant() {
        return nombreEnfant;
    }

    public void setNombreEnfant(int nombreEnfant) {
        this.nombreEnfant = nombreEnfant;
    }

    public BigDecimal getMontantIpts() {
        return montantIpts;
    }

    public void setMontantIpts(BigDecimal montantIpts) {
        this.montantIpts = montantIpts;
    }

    public BigDecimal getMontantAib() {
        return montantAib;
    }

    public void setMontantAib(BigDecimal montantAib) {
        this.montantAib = montantAib;
    }

    public BigDecimal getTotalRetenue() {
        return totalRetenue;
    }

    public void setTotalRetenue(BigDecimal totalRetenue) {
        this.totalRetenue = totalRetenue;
    }

    public BigDecimal getSalaireNet() {
        return salaireNet;
    }

    public void setSalaireNet(BigDecimal salaireNet) {
        this.salaireNet = salaireNet;
    }

    public BigDecimal getAutreAvantage() {
        return autreAvantage;
    }

    public void setAutreAvantage(BigDecimal autreAvantage) {
        this.autreAvantage = autreAvantage;
    }

    public BigDecimal getAutreRetenue() {
        return autreRetenue;
    }

    public void setAutreRetenue(BigDecimal autreRetenue) {
        this.autreRetenue = autreRetenue;
    }

    public BigDecimal getMontantCnssEmployeur() {
        return montantCnssEmployeur;
    }

    public void setMontantCnssEmployeur(BigDecimal montantCnssEmployeur) {
        this.montantCnssEmployeur = montantCnssEmployeur;
    }

    public BigDecimal getMontantVps() {
        return montantVps;
    }

    public void setMontantVps(BigDecimal montantVps) {
        this.montantVps = montantVps;
    }

    public BigDecimal getTotalChargePatronale() {
        return totalChargePatronale;
    }

    public void setTotalChargePatronale(BigDecimal totalChargePatronale) {
        this.totalChargePatronale = totalChargePatronale;
    }

    public BigDecimal getNetAPayer() {
        return netAPayer;
    }

    public void setNetAPayer(BigDecimal netAPayer) {
        this.netAPayer = netAPayer;
    }

    public String getNumeroCompteEmploye() {
        return numeroCompteEmploye;
    }

    public void setNumeroCompteEmploye(String numeroCompteEmploye) {
        this.numeroCompteEmploye = numeroCompteEmploye;
    }

    public double getCongePris() {
        return congePris;
    }

    public void setCongePris(double congePris) {
        this.congePris = congePris;
    }

    public double getSoldeConge() {
        return soldeConge;
    }

    public void setSoldeConge(double soldeConge) {
        this.soldeConge = soldeConge;
    }

    public String getSignataire() {
        return signataire;
    }

    public void setSignataire(String signataire) {
        this.signataire = signataire;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public Long getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(Long validatedBy) {
        this.validatedBy = validatedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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



    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Banque getBanque() {
        return banque;
    }

    public void setBanque(Banque banque) {
        this.banque = banque;
    }

    public User getValidatedByUser() {
        return validatedByUser;
    }

    public void setValidatedByUser(User validatedByUser) {
        this.validatedByUser = validatedByUser;
    }

    public EmployeDTO getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeDTO employe) {
        this.employe = employe;
    }

    public ApercuSalaireDTO getJsonDescription() {
        return jsonDescription;
    }

    public void setJsonDescription(String descriptionJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ApercuSalaireDTO apercu = objectMapper.readValue(descriptionJson, ApercuSalaireDTO.class);
            this.jsonDescription = apercu; // ou stocker ce DTO dans une propriété du DTO principal
        } catch (Exception e) {
            System.err.println("Erreur de parsing du champ description : " + e.getMessage());
        }
    }

    public void setJsonDescription(ApercuSalaireDTO jsonDescription) {
        this.jsonDescription = jsonDescription;
    }
}
