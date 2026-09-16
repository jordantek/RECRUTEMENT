package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
// Element calcul salaire
@Table(name = "rubriques")

public class Rubrique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle;

    private String nature;

    private String rubriqueImposable;

    @ManyToOne
    @JoinColumn(name = "niveau_affichage_id")
    private NiveauAffichage niveauAffichage;

    @ManyToOne
    @JoinColumn(name = "colonne_affichage_id")
    private ColonneAffichage colonneAffichage;

    private boolean calculeAuProrataTempsTravail;

    private String calculeAPartirCoefficient;

    private String calculeAPartirSalaireBrut;

    private double coefficient;

    private String partPatronale;

    private String rubriqueSysteme;

    private int numeroOrdre;

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

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getRubriqueImposable() {
        return rubriqueImposable;
    }

    public void setRubriqueImposable(String rubriqueImposable) {
        this.rubriqueImposable = rubriqueImposable;
    }

    public NiveauAffichage getNiveauAffichage() {
        return niveauAffichage;
    }

    public void setNiveauAffichage(NiveauAffichage niveauAffichage) {
        this.niveauAffichage = niveauAffichage;
    }

    public ColonneAffichage getColonneAffichage() {
        return colonneAffichage;
    }

    public void setColonneAffichage(ColonneAffichage colonneAffichage) {
        this.colonneAffichage = colonneAffichage;
    }

    public boolean isCalculeAuProrataTempsTravail() {
        return calculeAuProrataTempsTravail;
    }

    public void setCalculeAuProrataTempsTravail(boolean calculeAuProrataTempsTravail) {
        this.calculeAuProrataTempsTravail = calculeAuProrataTempsTravail;
    }

    public String getCalculeAPartirCoefficient() {
        return calculeAPartirCoefficient;
    }

    public void setCalculeAPartirCoefficient(String calculeAPartirCoefficient) {
        this.calculeAPartirCoefficient = calculeAPartirCoefficient;
    }

    public String getCalculeAPartirSalaireBrut() {
        return calculeAPartirSalaireBrut;
    }

    public void setCalculeAPartirSalaireBrut(String calculeAPartirSalaireBrut) {
        this.calculeAPartirSalaireBrut = calculeAPartirSalaireBrut;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public String getPartPatronale() {
        return partPatronale;
    }

    public void setPartPatronale(String partPatronale) {
        this.partPatronale = partPatronale;
    }

    public String getRubriqueSysteme() {
        return rubriqueSysteme;
    }

    public void setRubriqueSysteme(String rubriqueSysteme) {
        this.rubriqueSysteme = rubriqueSysteme;
    }

    public int getNumeroOrdre() {
        return numeroOrdre;
    }

    public void setNumeroOrdre(int numeroOrdre) {
        this.numeroOrdre = numeroOrdre;
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