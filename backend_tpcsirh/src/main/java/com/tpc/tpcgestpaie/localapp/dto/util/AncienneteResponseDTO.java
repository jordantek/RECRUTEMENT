package com.tpc.tpcgestpaie.localapp.dto.util;
import java.time.LocalDate;
import java.util.List;

public class AncienneteResponseDTO {
    private LocalDate dateDebutContrat;
    private LocalDate dateFinContrat;
    private double anciennete;
    private List<String> mois;

    // Constructeur
    public AncienneteResponseDTO(LocalDate dateDebutContrat, LocalDate dateFinContrat, double anciennete, List<String> mois) {
        this.dateDebutContrat = dateDebutContrat;
        this.dateFinContrat = dateFinContrat;
        this.anciennete = anciennete;
        this.mois = mois;
    }

    // Getters et setters
    public LocalDate getDateDebutContrat() {
        return dateDebutContrat;
    }

    public void setDateDebutContrat(LocalDate dateDebutContrat) {
        this.dateDebutContrat = dateDebutContrat;
    }

    public LocalDate getDateFinContrat() {
        return dateFinContrat;
    }

    public void setDateFinContrat(LocalDate dateFinContrat) {
        this.dateFinContrat = dateFinContrat;
    }

    public double getAnciennete() {
        return anciennete;
    }

    public void setAnciennete(double anciennete) {
        this.anciennete = anciennete;
    }

    public List<String> getMois() {
        return mois;
    }

    public void setMois(List<String> mois) {
        this.mois = mois;
    }
}

