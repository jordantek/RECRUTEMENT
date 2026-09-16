package com.tpc.tpcgestpaie.localapp.model.anciennete;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anciennete_settings")
public class AncienneteSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "nombre_mois_ecart", nullable = false)
    private Integer nombreMoisEcart;

    @Column(name = "paye_anciennete", nullable = false)
    private Boolean payeAnciennete = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by")
    private User added_by;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Column(name = "deleted_at")
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

    // Constructeurs
    public AncienneteSetting() {}

    public AncienneteSetting(Company company, Integer nombreMoisEcart, Boolean payeAnciennete) {
        this.company = company;
        this.nombreMoisEcart = nombreMoisEcart;
        this.payeAnciennete = payeAnciennete;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Integer getNombreMoisEcart() {
        return nombreMoisEcart;
    }

    public void setNombreMoisEcart(Integer nombreMoisEcart) {
        this.nombreMoisEcart = nombreMoisEcart;
    }

    public Boolean getPayeAnciennete() {
        return payeAnciennete;
    }

    public void setPayeAnciennete(Boolean payeAnciennete) {
        this.payeAnciennete = payeAnciennete;
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