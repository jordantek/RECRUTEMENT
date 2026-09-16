package com.tpc.tpcgestpaie.localapp.model;

import com.tpc.tpcgestpaie.localapp.config.YearMonthConverter;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "indemnite_licenciements")
public class IndemniteLicenciement {
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

    @Convert(converter = YearMonthConverter.class)
    @DateTimeFormat(pattern = "yyyy-MM")
    private YearMonth moisCalculSalaire;

    private String typeLicencement;
    private BigDecimal indemniteSelonAnciennete;
    private double anciennete;
    private BigDecimal montantMoyen;
    private BigDecimal montantTotal;

    // ✅ Champ JSON directement stocké en base
    @Column(columnDefinition = "JSON")
    private String montantsMensuels;

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

    // ----- GETTERS & SETTERS -----

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

    public YearMonth getMoisCalculSalaire() {
        return moisCalculSalaire;
    }

    public void setMoisCalculSalaire(YearMonth moisCalculSalaire) {
        this.moisCalculSalaire = moisCalculSalaire;
    }

    public String getTypeLicencement() {
        return typeLicencement;
    }

    public void setTypeLicencement(String typeLicencement) {
        this.typeLicencement = typeLicencement;
    }

    public BigDecimal getIndemniteSelonAnciennete() {
        return indemniteSelonAnciennete;
    }

    public void setIndemniteSelonAnciennete(BigDecimal indemniteSelonAnciennete) {
        this.indemniteSelonAnciennete = indemniteSelonAnciennete;
    }

    public double getAnciennete() {
        return anciennete;
    }

    public void setAnciennete(double anciennete) {
        this.anciennete = anciennete;
    }

    public BigDecimal getMontantMoyen() {
        return montantMoyen;
    }

    public void setMontantMoyen(BigDecimal montantMoyen) {
        this.montantMoyen = montantMoyen;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getMontantsMensuels() {
        return montantsMensuels;
    }

    public void setMontantsMensuels(String montantsMensuels) {
        this.montantsMensuels = montantsMensuels;
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
