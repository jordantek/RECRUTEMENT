package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bulletins_paies")
public class BulletinPaie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate  dateCalculSalaire;

    private String  mois;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_employe_id")
    private ContratEmploye contratEmploye;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id")
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private double tempsTravail;
    private Double taxeRadiophonique;
    private Double taxeTelevisuel;
    private String departement;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banque_id")
    private Banque domiciliationBancaireEmploye;

    private double congePris;
    private double soldeConge;
    private String signataire;


    private double primesExceptionnelles;
    private double treiziemeMois;
    private double salaireBrutMoisPasse;

    private double allocationConge;
    private double primeAnciennete;

    @Column(name = "statut", nullable = true)
    private String statut;

    @Column(name = "validated_at", nullable = true)
    private LocalDateTime validated_at;

    @Column(name = "validated_by", nullable = true)
    private Long validated_by;

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by")
    private User added_by;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    private LocalDateTime deleted_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        updated_at = created_at;
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate  getDateCalculSalaire() {
        return dateCalculSalaire;
    }

    public void setDateCalculSalaire(LocalDate  dateCalculSalaire) {
        this.dateCalculSalaire = dateCalculSalaire;
    }

    public String  getMois() {
        return mois;
    }

    public void setMois(String  mois) {
        this.mois = mois;
    }

    public ContratEmploye getContratEmploye() {
        return contratEmploye;
    }

    public void setContratEmploye(ContratEmploye contratEmploye) {
        this.contratEmploye = contratEmploye;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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

    public Banque getDomiciliationBancaireEmploye() {
        return domiciliationBancaireEmploye;
    }

    public void setDomiciliationBancaireEmploye(Banque domiciliationBancaireEmploye) {
        this.domiciliationBancaireEmploye = domiciliationBancaireEmploye;
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

    public User getAdded_by() {
        return added_by;
    }

    public void setAdded_by(User added_by) {
        this.added_by = added_by;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public LocalDateTime getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(LocalDateTime deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getValidated_at() {
        return validated_at;
    }

    public void setValidated_at(LocalDateTime validated_at) {
        this.validated_at = validated_at;
    }

    public Long getValidated_by() {
        return validated_by;
    }

    public void setValidated_by(Long validated_by) {
        this.validated_by = validated_by;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
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
}