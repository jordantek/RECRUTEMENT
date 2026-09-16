package com.tpc.tpcgestpaie.localapp.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "augmentation_rubriques")
public class AugmentationRubrique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "augmentation_id")
    private AugmentationSalariale augmentation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubrique_id")
    private Rubrique rubrique;

    private String libelle;

    private BigDecimal ancienMontant; // null si nouvelle rubrique
    private BigDecimal nouveauMontant;

    private BigDecimal montantAugmentation; // = nouveau - ancien ou = nouveau si nouvelle rubrique

    private boolean estNouvelleRubrique;

    @PrePersist
    @PreUpdate
    public void calculerMontantAugmentation() {
        if (nouveauMontant == null) {
            montantAugmentation = BigDecimal.ZERO;
            return;
        }

        if (ancienMontant == null || ancienMontant.compareTo(BigDecimal.ZERO) == 0) {
            montantAugmentation = nouveauMontant;
            estNouvelleRubrique = true;
        } else {
            montantAugmentation = nouveauMontant.subtract(ancienMontant);
            estNouvelleRubrique = false;
        }
    }

    // Getters / Setters ici (générés ou à la main)


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AugmentationSalariale getAugmentation() {
        return augmentation;
    }

    public void setAugmentation(AugmentationSalariale augmentation) {
        this.augmentation = augmentation;
    }

    public Rubrique getRubrique() {
        return rubrique;
    }

    public void setRubrique(Rubrique rubrique) {
        this.rubrique = rubrique;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public BigDecimal getAncienMontant() {
        return ancienMontant;
    }

    public void setAncienMontant(BigDecimal ancienMontant) {
        this.ancienMontant = ancienMontant;
    }

    public BigDecimal getNouveauMontant() {
        return nouveauMontant;
    }

    public void setNouveauMontant(BigDecimal nouveauMontant) {
        this.nouveauMontant = nouveauMontant;
    }

    public BigDecimal getMontantAugmentation() {
        return montantAugmentation;
    }

    public void setMontantAugmentation(BigDecimal montantAugmentation) {
        this.montantAugmentation = montantAugmentation;
    }

    public boolean isEstNouvelleRubrique() {
        return estNouvelleRubrique;
    }

    public void setEstNouvelleRubrique(boolean estNouvelleRubrique) {
        this.estNouvelleRubrique = estNouvelleRubrique;
    }
}
