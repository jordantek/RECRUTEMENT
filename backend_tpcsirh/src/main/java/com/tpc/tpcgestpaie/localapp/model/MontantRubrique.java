package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "montant_rubriques")
public class MontantRubrique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateRubrique;

    private String moisRubrique;

    @ManyToOne
    @JoinColumn(name = "contrat_employe_id")
    private ContratEmploye contratEmploye;

    @ManyToOne
    @JoinColumn(name = "employe_id")
    private Employe employe;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "rubrique_id")
    private Rubrique rubrique;

    private BigDecimal montantRubrique;

    private LocalDate debutPeriodeReference;

    private LocalDate finPeriodeReference;

    private BigDecimal montantBrutJournalier;

    private double nombreJourPaye;

  /*  public String getName_prime() {
        return name_prime;
    }

    public void setName_prime(String name_prime) {
        this.name_prime = name_prime;
    }

    private String name_prime;*/

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

    public LocalDate getDateRubrique() {
        return dateRubrique;
    }

    public void setDateRubrique(LocalDate dateRubrique) {
        this.dateRubrique = dateRubrique;
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

    public Rubrique getRubrique() {
        return rubrique;
    }

    public void setRubrique(Rubrique rubrique) {
        this.rubrique = rubrique;
    }

    public BigDecimal getMontantRubrique() {
        return montantRubrique;
    }

    public void setMontantRubrique(BigDecimal montantRubrique) {
        this.montantRubrique = montantRubrique;
    }

    public LocalDate getDebutPeriodeReference() {
        return debutPeriodeReference;
    }

    public void setDebutPeriodeReference(LocalDate debutPeriodeReference) {
        this.debutPeriodeReference = debutPeriodeReference;
    }

    public LocalDate getFinPeriodeReference() {
        return finPeriodeReference;
    }

    public void setFinPeriodeReference(LocalDate finPeriodeReference) {
        this.finPeriodeReference = finPeriodeReference;
    }

    public BigDecimal getMontantBrutJournalier() {
        return montantBrutJournalier;
    }

    public void setMontantBrutJournalier(BigDecimal montantBrutJournalier) {
        this.montantBrutJournalier = montantBrutJournalier;
    }

    public double getNombreJourPaye() {
        return nombreJourPaye;
    }

    public void setNombreJourPaye(double nombreJourPaye) {
        this.nombreJourPaye = nombreJourPaye;
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

    public String getMoisRubrique() {
        return moisRubrique;
    }

    public void setMoisRubrique(String moisRubrique) {
        this.moisRubrique = moisRubrique;
    }
}