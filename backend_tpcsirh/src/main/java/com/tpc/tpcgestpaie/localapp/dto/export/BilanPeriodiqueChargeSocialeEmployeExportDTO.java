package com.tpc.tpcgestpaie.localapp.dto.export;

import com.tpc.tpcgestpaie.localapp.dto.etat.BilanPeriodiqueChargeSocialeDTO;

import java.math.BigDecimal;

public class BilanPeriodiqueChargeSocialeEmployeExportDTO {
    private String debut;
    private String fin;
    private String employe;
    private String numeroCnssEmploye;
    private BigDecimal salaireBrut;
    private BigDecimal cnssEmploye;
    private BigDecimal cnssEmployeur;
    private BigDecimal totalChargeSociale;
    private String entreprise;
    private String numeroCnssEntreprise;

    // CORRECTION dans BilanPeriodiqueChargeSocialeEmployeExportDTO :
    public BilanPeriodiqueChargeSocialeEmployeExportDTO(BilanPeriodiqueChargeSocialeDTO dto, String entrepriseNom) {
        this.debut = dto.getDebut();
        this.fin = dto.getFin();
        this.employe = dto.getEmploye();
        this.numeroCnssEmploye = dto.getNumeroCnssEmploye();
        this.salaireBrut = dto.getSalaireBrut();
        this.cnssEmploye = dto.getCnssEmploye();
        this.cnssEmployeur = dto.getCnssEmployeur();
        this.totalChargeSociale = dto.getTotalChargeSociale();
        this.entreprise = entrepriseNom;
        this.numeroCnssEntreprise = dto.getNumeroCnssEntreprise();
    }
    // Constructeur complet
    public BilanPeriodiqueChargeSocialeEmployeExportDTO(
            String debut,
            String fin,
            String employe,
            String numeroCnssEmploye,
            BigDecimal salaireBrut,
            BigDecimal cnssEmploye,
            BigDecimal cnssEmployeur,
            BigDecimal totalChargeSociale,
            String entreprise,
            String numeroCnssEntreprise
    ) {
        this.debut = debut;
        this.fin = fin;
        this.employe = employe;
        this.numeroCnssEmploye = numeroCnssEmploye;
        this.salaireBrut = salaireBrut;
        this.cnssEmploye = cnssEmploye;
        this.cnssEmployeur = cnssEmployeur;
        this.totalChargeSociale = totalChargeSociale;
        this.entreprise = entreprise;
        this.numeroCnssEntreprise = numeroCnssEntreprise;
    }

    // Getters et Setters
    public String getDebut() { return debut; }
    public void setDebut(String debut) { this.debut = debut; }

    public String getFin() { return fin; }
    public void setFin(String fin) { this.fin = fin; }

    public String getEmploye() { return employe; }
    public void setEmploye(String employe) { this.employe = employe; }

    public String getNumeroCnssEmploye() { return numeroCnssEmploye; }
    public void setNumeroCnssEmploye(String numeroCnssEmploye) { this.numeroCnssEmploye = numeroCnssEmploye; }

    public BigDecimal getSalaireBrut() { return salaireBrut; }
    public void setSalaireBrut(BigDecimal salaireBrut) { this.salaireBrut = salaireBrut; }

    public BigDecimal getCnssEmploye() { return cnssEmploye; }
    public void setCnssEmploye(BigDecimal cnssEmploye) { this.cnssEmploye = cnssEmploye; }

    public BigDecimal getCnssEmployeur() { return cnssEmployeur; }
    public void setCnssEmployeur(BigDecimal cnssEmployeur) { this.cnssEmployeur = cnssEmployeur; }

    public BigDecimal getTotalChargeSociale() { return totalChargeSociale; }
    public void setTotalChargeSociale(BigDecimal totalChargeSociale) { this.totalChargeSociale = totalChargeSociale; }

    public String getEntreprise() { return entreprise; }
    public void setEntreprise(String entreprise) { this.entreprise = entreprise; }

    public String getNumeroCnssEntreprise() { return numeroCnssEntreprise; }
    public void setNumeroCnssEntreprise(String numeroCnssEntreprise) { this.numeroCnssEntreprise = numeroCnssEntreprise; }
}