package com.tpc.tpcgestpaie.localapp.dto.export;

import java.math.BigDecimal;

public class ApercuApresExportDTO {

    // Informations employé
    private String employeNomPrenom;
    private String departement;
    private String banque;
    private String numeroCompteEmploye;

    // Éléments de salaire
    private double treiziemeMois;
    private double primesExceptionnelles;
    private BigDecimal brutM;

    // Retenues
    private BigDecimal its;
    private BigDecimal cnssSalarie;
    private BigDecimal retenueLegale;

    // Charges patronales
    private BigDecimal vps;
    private BigDecimal cnssEmployeur;
    private BigDecimal chargesPatronales;

    // Nets et retenues
    private BigDecimal salaireNet;
    private BigDecimal mensualite;
    private BigDecimal avance;
    private BigDecimal acompte;
    private BigDecimal retenuesNet;
    private BigDecimal netAPayer;

    private String statut;

    // Getters et Setters

    public String getEmployeNomPrenom() {
        return employeNomPrenom;
    }

    public void setEmployeNomPrenom(String employeNomPrenom) {
        this.employeNomPrenom = employeNomPrenom;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getBanque() {
        return banque;
    }

    public void setBanque(String banque) {
        this.banque = banque;
    }

    public String getNumeroCompteEmploye() {
        return numeroCompteEmploye;
    }

    public void setNumeroCompteEmploye(String numeroCompteEmploye) {
        this.numeroCompteEmploye = numeroCompteEmploye;
    }

    public double getTreiziemeMois() {
        return treiziemeMois;
    }

    public void setTreiziemeMois(double treiziemeMois) {
        this.treiziemeMois = treiziemeMois;
    }

    public double getPrimesExceptionnelles() {
        return primesExceptionnelles;
    }

    public void setPrimesExceptionnelles(double primesExceptionnelles) {
        this.primesExceptionnelles = primesExceptionnelles;
    }

    public BigDecimal getBrutM() {
        return brutM;
    }

    public void setBrutM(BigDecimal brutM) {
        this.brutM = brutM;
    }

    public BigDecimal getIts() {
        return its;
    }

    public void setIts(BigDecimal its) {
        this.its = its;
    }

    public BigDecimal getCnssSalarie() {
        return cnssSalarie;
    }

    public void setCnssSalarie(BigDecimal cnssSalarie) {
        this.cnssSalarie = cnssSalarie;
    }

    public BigDecimal getRetenueLegale() {
        return retenueLegale;
    }

    public void setRetenueLegale(BigDecimal retenueLegale) {
        this.retenueLegale = retenueLegale;
    }

    public BigDecimal getVps() {
        return vps;
    }

    public void setVps(BigDecimal vps) {
        this.vps = vps;
    }

    public BigDecimal getCnssEmployeur() {
        return cnssEmployeur;
    }

    public void setCnssEmployeur(BigDecimal cnssEmployeur) {
        this.cnssEmployeur = cnssEmployeur;
    }

    public BigDecimal getChargesPatronales() {
        return chargesPatronales;
    }

    public void setChargesPatronales(BigDecimal chargesPatronales) {
        this.chargesPatronales = chargesPatronales;
    }

    public BigDecimal getSalaireNet() {
        return salaireNet;
    }

    public void setSalaireNet(BigDecimal salaireNet) {
        this.salaireNet = salaireNet;
    }

    public BigDecimal getMensualite() {
        return mensualite;
    }

    public void setMensualite(BigDecimal mensualite) {
        this.mensualite = mensualite;
    }

    public BigDecimal getAvance() {
        return avance;
    }

    public void setAvance(BigDecimal avance) {
        this.avance = avance;
    }

    public BigDecimal getAcompte() {
        return acompte;
    }

    public void setAcompte(BigDecimal acompte) {
        this.acompte = acompte;
    }

    public BigDecimal getRetenuesNet() {
        return retenuesNet;
    }

    public void setRetenuesNet(BigDecimal retenuesNet) {
        this.retenuesNet = retenuesNet;
    }

    public BigDecimal getNetAPayer() {
        return netAPayer;
    }

    public void setNetAPayer(BigDecimal netAPayer) {
        this.netAPayer = netAPayer;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}