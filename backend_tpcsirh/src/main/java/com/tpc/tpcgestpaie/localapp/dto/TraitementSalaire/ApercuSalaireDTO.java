package com.tpc.tpcgestpaie.localapp.dto.TraitementSalaire;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApercuSalaireDTO {

    private Long contratEmployeId;
    private String nomPrenomEmploye;
    private String Departement;
    private String Bank;
    private String numeroCompte;
    private String mois;
    private double salaireBaseContrat;
    private double heureSup;
    private double tempsTravail;
    private double salaireBrut;
    private double autrePrime;
    private double montantIpts;
    private double montantCnss;
    private double montantTTRetenue;
    private double montantVps;
    private double montantCnssEmployeur;
    private double totalChargePatronale;
    private double salaireNet;
    private double mensualite;
    private double avance;
    private double acompte;
    private double totalRetenueNet;
    private double totalRetenue;

    private Double taxeRadiophonique;
    private Double taxeTelevisuel;

    private double netAPayer;

    private double primesExceptionnelles;
    private double treiziemeMois;
    private double salaireBrutMoisPasse;
    private double primesAnciennete;
    private double allocationConge;
    private double primeAnciennete;

    // ✅ AJOUTS POUR LES LIBELLÉS
    private Map<String, Double> detailsSalaire;
    private Map<String, Double> detailsPrimes;

    // ===== GETTERS & SETTERS =====


    public double getTotalRetenueNet() {
        return totalRetenueNet;
    }

    public void setTotalRetenueNet(double totalRetenueNet) {
        this.totalRetenueNet = totalRetenueNet;
    }

    public double getAvance() {
        return avance;
    }

    public void setAvance(double avance) {
        this.avance = avance;
    }

    public double getAcompte() {
        return acompte;
    }

    public void setAcompte(double acompte) {
        this.acompte = acompte;
    }

    public Long getContratEmployeId() {
        return contratEmployeId;
    }

    public void setContratEmployeId(Long contratEmployeId) {
        this.contratEmployeId = contratEmployeId;
    }

    public String getNomPrenomEmploye() {
        return nomPrenomEmploye;
    }

    public void setNomPrenomEmploye(String nomPrenomEmploye) {
        this.nomPrenomEmploye = nomPrenomEmploye;
    }

    public String getDepartement() {
        return Departement;
    }

    public void setDepartement(String departement) {
        Departement = departement;
    }

    public String getBank() {
        return Bank;
    }

    public void setBank(String bank) {
        Bank = bank;
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }

    public void setNumeroCompte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public double getSalaireBaseContrat() {
        return salaireBaseContrat;
    }

    public void setSalaireBaseContrat(double salaireBaseContrat) {
        this.salaireBaseContrat = salaireBaseContrat;
    }

    public double getHeureSup() {
        return heureSup;
    }

    public void setHeureSup(double heureSup) {
        this.heureSup = heureSup;
    }

    public double getTempsTravail() {
        return tempsTravail;
    }

    public void setTempsTravail(double tempsTravail) {
        this.tempsTravail = tempsTravail;
    }

    public double getSalaireBrut() {
        return salaireBrut;
    }

    public void setSalaireBrut(double salaireBrut) {
        this.salaireBrut = salaireBrut;
    }

    public double getAutrePrime() {
        return autrePrime;
    }

    public void setAutrePrime(double autrePrime) {
        this.autrePrime = autrePrime;
    }

    public double getMontantIpts() {
        return montantIpts;
    }

    public void setMontantIpts(double montantIpts) {
        this.montantIpts = montantIpts;
    }

    public double getMontantCnss() {
        return montantCnss;
    }

    public void setMontantCnss(double montantCnss) {
        this.montantCnss = montantCnss;
    }

    public double getMontantTTRetenue() {
        return montantTTRetenue;
    }

    public void setMontantTTRetenue(double montantTTRetenue) {
        this.montantTTRetenue = montantTTRetenue;
    }

    public double getMontantVps() {
        return montantVps;
    }

    public void setMontantVps(double montantVps) {
        this.montantVps = montantVps;
    }

    public double getMontantCnssEmployeur() {
        return montantCnssEmployeur;
    }

    public void setMontantCnssEmployeur(double montantCnssEmployeur) {
        this.montantCnssEmployeur = montantCnssEmployeur;
    }

    public double getTotalChargePatronale() {
        return totalChargePatronale;
    }

    public void setTotalChargePatronale(double totalChargePatronale) {
        this.totalChargePatronale = totalChargePatronale;
    }

    public double getSalaireNet() {
        return salaireNet;
    }

    public void setSalaireNet(double salaireNet) {
        this.salaireNet = salaireNet;
    }

    public double getMensualite() {
        return mensualite;
    }

    public void setMensualite(double mensualite) {
        this.mensualite = mensualite;
    }

    public double getTotalRetenue() {
        return totalRetenue;
    }

    public void setTotalRetenue(double totalRetenue) {
        this.totalRetenue = totalRetenue;
    }

    public Double getTaxeRadiophonique() {
        return taxeRadiophonique;
    }

    public void setTaxeRadiophonique(Double taxeRadiophonique) {
        this.taxeRadiophonique = taxeRadiophonique;
    }

    public Double getTaxeTelevisuel() {
        return taxeTelevisuel;
    }

    public void setTaxeTelevisuel(Double taxeTelevisuel) {
        this.taxeTelevisuel = taxeTelevisuel;
    }

    public double getNetAPayer() {
        return netAPayer;
    }

    public void setNetAPayer(double netAPayer) {
        this.netAPayer = netAPayer;
    }

    public double getPrimesExceptionnelles() {
        return primesExceptionnelles;
    }

    public void setPrimesExceptionnelles(double primesExceptionnelles) {
        this.primesExceptionnelles = primesExceptionnelles;
    }

    public double getTreiziemeMois() {
        return treiziemeMois;
    }

    public void setTreiziemeMois(double treiziemeMois) {
        this.treiziemeMois = treiziemeMois;
    }

    public double getSalaireBrutMoisPasse() {
        return salaireBrutMoisPasse;
    }

    public void setSalaireBrutMoisPasse(double salaireBrutMoisPasse) {
        this.salaireBrutMoisPasse = salaireBrutMoisPasse;
    }

    public double getPrimesAnciennete() {
        return primesAnciennete;
    }

    public void setPrimesAnciennete(double primesAnciennete) {
        this.primesAnciennete = primesAnciennete;
    }

    public double getAllocationConge() {
        return allocationConge;
    }

    public void setAllocationConge(double allocationConge) {
        this.allocationConge = allocationConge;
    }

    public double getPrimeAnciennete() {
        return primeAnciennete;
    }

    public void setPrimeAnciennete(double primeAnciennete) {
        this.primeAnciennete = primeAnciennete;
    }

    // ✅ GETTERS & SETTERS POUR LES MAPS

    public Map<String, Double> getDetailsSalaire() {
        return detailsSalaire;
    }

    public void setDetailsSalaire(Map<String, Double> detailsSalaire) {
        this.detailsSalaire = detailsSalaire;
    }

    public Map<String, Double> getDetailsPrimes() {
        return detailsPrimes;
    }

    public void setDetailsPrimes(Map<String, Double> detailsPrimes) {
        this.detailsPrimes = detailsPrimes;
    }
}
