package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "parametre_paie")
public class ParametrePaie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double prestationsfamiliales;
    private Double pensionsEntreprise;
    private Double pensionsEmploye;

    private Double nbrAnneVpsEntreprise;
    private Double nbrAnneVpsEmploye;

    @Column(name = "unique_key", unique = true, nullable = false)
    private String uniqueKey = "PARAM_UNIQUE";

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

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrestationsfamiliales() {
        return prestationsfamiliales;
    }

    public void setPrestationsfamiliales(Double prestationsfamiliales) {
        this.prestationsfamiliales = prestationsfamiliales;
    }

    public Double getPensionsEntreprise() {
        return pensionsEntreprise;
    }

    public void setPensionsEntreprise(Double pensionsEntreprise) {
        this.pensionsEntreprise = pensionsEntreprise;
    }

    public Double getPensionsEmploye() {
        return pensionsEmploye;
    }

    public void setPensionsEmploye(Double pensionsEmploye) {
        this.pensionsEmploye = pensionsEmploye;
    }

    public Double getNbrAnneVpsEntreprise() {
        return nbrAnneVpsEntreprise;
    }

    public void setNbrAnneVpsEntreprise(Double nbrAnneVpsEntreprise) {
        this.nbrAnneVpsEntreprise = nbrAnneVpsEntreprise;
    }

    public Double getNbrAnneVpsEmploye() {
        return nbrAnneVpsEmploye;
    }

    public void setNbrAnneVpsEmploye(Double nbrAnneVpsEmploye) {
        this.nbrAnneVpsEmploye = nbrAnneVpsEmploye;
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