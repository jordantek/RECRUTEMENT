package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Where(clause = "deleted_at is null")
@Table(name = "heure_supplementaire")
public class HeureSupplementaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String mois;

    private int salaireBaseContrat;

    private int salaireBrutContrat;

    private double heures12;

    private double heures35;

    private double heures50;

    private double heures100;

    private double totalHeures;

    private double majoration12;

    private double majoration35;

    private double majoration50;

    private double majoration100;

    private BigDecimal montant;

    private String observation;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_employe_id")
    private ContratEmploye contratEmploye;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id")
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_update_user_id")
    private User lastUpdateUser;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private LocalDateTime deleted_at;

    public void softDelete() {
        this.deleted_at = LocalDateTime.now();
    }

    public LocalDateTime getDeletedAt() {
        return deleted_at;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deleted_at = deletedAt;
    }
    public boolean isDeleted() {
        return deleted_at != null;
    }


    public double getHeures50() {
        return heures50;
    }

    public void setHeures50(double heures50) {
        this.heures50 = heures50;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public int getSalaireBaseContrat() {
        return salaireBaseContrat;
    }

    public void setSalaireBaseContrat(int salaireBaseContrat) {
        this.salaireBaseContrat = salaireBaseContrat;
    }

    public int getSalaireBrutContrat() {
        return salaireBrutContrat;
    }

    public void setSalaireBrutContrat(int salaireBrutContrat) {
        this.salaireBrutContrat = salaireBrutContrat;
    }

    public double getHeures12() {
        return heures12;
    }

    public void setHeures12(double heures12) {
        this.heures12 = heures12;
    }

    public double getHeures35() {
        return heures35;
    }

    public void setHeures35(double heures35) {
        this.heures35 = heures35;
    }

    public double getHeures100() {
        return heures100;
    }

    public void setHeures100(double heures100) {
        this.heures100 = heures100;
    }

    public double getTotalHeures() {
        return totalHeures;
    }

    public void setTotalHeures(double totalHeures) {
        this.totalHeures = totalHeures;
    }

    public double getMajoration12() {
        return majoration12;
    }

    public void setMajoration12(double majoration12) {
        this.majoration12 = majoration12;
    }

    public double getMajoration35() {
        return majoration35;
    }

    public void setMajoration35(double majoration35) {
        this.majoration35 = majoration35;
    }

    public double getMajoration50() {
        return majoration50;
    }

    public void setMajoration50(double majoration50) {
        this.majoration50 = majoration50;
    }

    public double getMajoration100() {
        return majoration100;
    }

    public void setMajoration100(double majoration100) {
        this.majoration100 = majoration100;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
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

    public User getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(User lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
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

    public LocalDateTime getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(LocalDateTime deleted_at) {
        this.deleted_at = deleted_at;
    }
}
