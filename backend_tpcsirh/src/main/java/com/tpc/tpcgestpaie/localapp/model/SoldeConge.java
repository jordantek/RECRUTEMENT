package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "solde_conge")
public class SoldeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @JoinColumn(name = "departement_id")
    private Departement departement;

    private BigDecimal indemniteConge;

    private double congePris;

    private double soldeConge;

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

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public BigDecimal getIndemniteConge() {
        return indemniteConge;
    }

    public void setIndemniteConge(BigDecimal indemniteConge) {
        this.indemniteConge = indemniteConge;
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
}
