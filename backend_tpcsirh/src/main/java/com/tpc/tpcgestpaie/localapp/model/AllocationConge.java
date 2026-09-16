package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
// Element calcul salaire
@Table(name = "allocation_conges")
public class AllocationConge {
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

    private String moisCalculSalaire;

    private double totalTemps;
    private BigDecimal totalSalaire;
    private BigDecimal salaireJournalierNormal;
    private long nbJours;
    private BigDecimal montantTotal;
    @Lob
    @Column(name = "bulletins_utilises", columnDefinition = "TEXT") // ou "JSON" si MySQL 8+
    private String bulletinsUtilisesJson;


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

    // Getter et Setter pour la liste DTO (transient)

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