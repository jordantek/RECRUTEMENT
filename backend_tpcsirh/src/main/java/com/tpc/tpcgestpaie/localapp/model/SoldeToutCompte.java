package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "solde_tout_comptes")
public class SoldeToutCompte {
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

    private String moisCalcul;

    // Valeurs d'entrée
    private BigDecimal salairePresence;
    private BigDecimal indemniteLicenciement;
    private BigDecimal indemnitePreavis;
    private BigDecimal indemniteConge;
    private BigDecimal gratification;
    private BigDecimal salaireMoyen;

    // Résultats calculés
    private BigDecimal brutMois; // A+B+C+D+E
    private BigDecimal itsMoyen;
    private BigDecimal itsMois;
    private BigDecimal cnssMois;
    private BigDecimal netMois;

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

    //Setters et Getters


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

    public String getMoisCalcul() {
        return moisCalcul;
    }

    public void setMoisCalcul(String moisCalcul) {
        this.moisCalcul = moisCalcul;
    }

    public BigDecimal getSalairePresence() {
        return salairePresence;
    }

    public void setSalairePresence(BigDecimal salairePresence) {
        this.salairePresence = salairePresence;
    }

    public BigDecimal getIndemniteLicenciement() {
        return indemniteLicenciement;
    }

    public void setIndemniteLicenciement(BigDecimal indemniteLicenciement) {
        this.indemniteLicenciement = indemniteLicenciement;
    }

    public BigDecimal getIndemnitePreavis() {
        return indemnitePreavis;
    }

    public void setIndemnitePreavis(BigDecimal indemnitePreavis) {
        this.indemnitePreavis = indemnitePreavis;
    }

    public BigDecimal getIndemniteConge() {
        return indemniteConge;
    }

    public void setIndemniteConge(BigDecimal indemniteConge) {
        this.indemniteConge = indemniteConge;
    }

    public BigDecimal getGratification() {
        return gratification;
    }

    public void setGratification(BigDecimal gratification) {
        this.gratification = gratification;
    }

    public BigDecimal getSalaireMoyen() {
        return salaireMoyen;
    }

    public void setSalaireMoyen(BigDecimal salaireMoyen) {
        this.salaireMoyen = salaireMoyen;
    }

    public BigDecimal getBrutMois() {
        return brutMois;
    }

    public void setBrutMois(BigDecimal brutMois) {
        this.brutMois = brutMois;
    }

    public BigDecimal getItsMoyen() {
        return itsMoyen;
    }

    public void setItsMoyen(BigDecimal itsMoyen) {
        this.itsMoyen = itsMoyen;
    }

    public BigDecimal getItsMois() {
        return itsMois;
    }

    public void setItsMois(BigDecimal itsMois) {
        this.itsMois = itsMois;
    }

    public BigDecimal getCnssMois() {
        return cnssMois;
    }

    public void setCnssMois(BigDecimal cnssMois) {
        this.cnssMois = cnssMois;
    }

    public BigDecimal getNetMois() {
        return netMois;
    }

    public void setNetMois(BigDecimal netMois) {
        this.netMois = netMois;
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
}