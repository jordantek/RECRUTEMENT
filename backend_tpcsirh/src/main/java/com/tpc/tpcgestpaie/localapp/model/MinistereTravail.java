package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ministere_travail")
public class MinistereTravail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ministere;
    private String serviceSecuriteSociale;

    private String directionEnregistrementContrat;

    private String directeurDepartementalTravail;


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

    public String getMinistere() {
        return ministere;
    }

    public void setMinistere(String ministere) {
        this.ministere = ministere;
    }

    public String getServiceSecuriteSociale() {
        return serviceSecuriteSociale;
    }

    public void setServiceSecuriteSociale(String serviceSecuriteSociale) {
        this.serviceSecuriteSociale = serviceSecuriteSociale;
    }

    public String getDirectionEnregistrementContrat() {
        return directionEnregistrementContrat;
    }

    public void setDirectionEnregistrementContrat(String directionEnregistrementContrat) {
        this.directionEnregistrementContrat = directionEnregistrementContrat;
    }

    public String getDirecteurDepartementalTravail() {
        return directeurDepartementalTravail;
    }

    public void setDirecteurDepartementalTravail(String directeurDepartementalTravail) {
        this.directeurDepartementalTravail = directeurDepartementalTravail;
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