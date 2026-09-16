package com.tpc.tpcgestpaie.localapp.CalculUtils;

import java.util.List;

public class SalaryCalculatorResult {

    // Salaire
    private double salaireBaseContrat;
    private double salaireBrutContrat;
    private double bonus;
    private double heuresSupplementaires;
    private double salaireBrut;
    private double salaireNet;

    // Charges salariales
    private double cnssSalariale;
    private double its;
    private double chargesSalariales;

    // Charges patronales
    private double vpsPatronales;
    private double cnssPatronales;
    private double chargesPatronales;

    // Taux utilisés (utile pour audit ou affichage détaillé)
    private double tauxCnssSalariale;
    private double tauxCnssPatronale;
    private double tauxVps;
    private double tauxRisquePro;
    private double tauxPrestationFamiliale;

    // Informations avancées optionnelles
    private int joursTravailles;
    private double salaireJournalier;
    private String employeNom;
    private Long employeId;

    // Historique ou logs (par exemple : pour frontend ou vérification)
    private List<String> logs;

    public SalaryCalculatorResult() {
    }

    // --- Getters et Setters ---

    public double getSalaireBaseContrat() {
        return salaireBaseContrat;
    }

    public void setSalaireBaseContrat(double salaireBaseContrat) {
        this.salaireBaseContrat = salaireBaseContrat;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public double getHeuresSupplementaires() {
        return heuresSupplementaires;
    }

    public void setHeuresSupplementaires(double heuresSupplementaires) {
        this.heuresSupplementaires = heuresSupplementaires;
    }

    public double getSalaireBrut() {
        return salaireBrut;
    }

    public void setSalaireBrut(double salaireBrut) {
        this.salaireBrut = salaireBrut;
    }

    public double getSalaireNet() {
        return salaireNet;
    }

    public void setSalaireNet(double salaireNet) {
        this.salaireNet = salaireNet;
    }

    public double getCnssSalariale() {
        return cnssSalariale;
    }

    public void setCnssSalariale(double cnssSalariale) {
        this.cnssSalariale = cnssSalariale;
    }

    public double getIts() {
        return its;
    }

    public void setIts(double its) {
        this.its = its;
    }

    public double getChargesSalariales() {
        return chargesSalariales;
    }

    public void setChargesSalariales(double chargesSalariales) {
        this.chargesSalariales = chargesSalariales;
    }

    public double getVpsPatronales() {
        return vpsPatronales;
    }

    public void setVpsPatronales(double vpsPatronales) {
        this.vpsPatronales = vpsPatronales;
    }

    public double getCnssPatronales() {
        return cnssPatronales;
    }

    public void setCnssPatronales(double cnssPatronales) {
        this.cnssPatronales = cnssPatronales;
    }

    public double getChargesPatronales() {
        return chargesPatronales;
    }

    public void setChargesPatronales(double chargesPatronales) {
        this.chargesPatronales = chargesPatronales;
    }

    public double getTauxCnssSalariale() {
        return tauxCnssSalariale;
    }

    public void setTauxCnssSalariale(double tauxCnssSalariale) {
        this.tauxCnssSalariale = tauxCnssSalariale;
    }

    public double getTauxCnssPatronale() {
        return tauxCnssPatronale;
    }

    public void setTauxCnssPatronale(double tauxCnssPatronale) {
        this.tauxCnssPatronale = tauxCnssPatronale;
    }

    public double getTauxVps() {
        return tauxVps;
    }

    public void setTauxVps(double tauxVps) {
        this.tauxVps = tauxVps;
    }

    public double getTauxRisquePro() {
        return tauxRisquePro;
    }

    public void setTauxRisquePro(double tauxRisquePro) {
        this.tauxRisquePro = tauxRisquePro;
    }

    public double getTauxPrestationFamiliale() {
        return tauxPrestationFamiliale;
    }

    public void setTauxPrestationFamiliale(double tauxPrestationFamiliale) {
        this.tauxPrestationFamiliale = tauxPrestationFamiliale;
    }

    public int getJoursTravailles() {
        return joursTravailles;
    }

    public void setJoursTravailles(int joursTravailles) {
        this.joursTravailles = joursTravailles;
    }

    public double getSalaireJournalier() {
        return salaireJournalier;
    }

    public void setSalaireJournalier(double salaireJournalier) {
        this.salaireJournalier = salaireJournalier;
    }

    public String getEmployeNom() {
        return employeNom;
    }

    public void setEmployeNom(String employeNom) {
        this.employeNom = employeNom;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public double getSalaireBrutContrat() {
        return salaireBrutContrat;
    }

    public void setSalaireBrutContrat(double salaireBrutContrat) {
        this.salaireBrutContrat = salaireBrutContrat;
    }

    public void showdata() {
        System.out.println("========== SALARY CALCULATOR RESULT ==========");

        // Infos employé
        System.out.println("Employé ID            : " + employeId);
        System.out.println("Employé Nom           : " + employeNom);

        // Temps de travail
        System.out.println("Jours travaillés      : " + joursTravailles);
        System.out.println("Salaire journalier    : " + salaireJournalier);

        System.out.println("------------- SALAIRES -------------");
        System.out.println("Salaire base contrat  : " + salaireBaseContrat);
        System.out.println("Salaire brut contrat  : " + salaireBrutContrat);
        System.out.println("Bonus                 : " + bonus);
        System.out.println("Heures supplémentaires: " + heuresSupplementaires);
        System.out.println("Salaire brut total    : " + salaireBrut);
        System.out.println("Salaire net           : " + salaireNet);

        System.out.println("--------- CHARGES SALARIALES ---------");
        System.out.println("CNSS salariale        : " + cnssSalariale);
        System.out.println("ITS                   : " + its);
        System.out.println("Total charges salariales : " + chargesSalariales);

        System.out.println("-------- CHARGES PATRONALES --------");
        System.out.println("VPS patronales        : " + vpsPatronales);
        System.out.println("CNSS patronales       : " + cnssPatronales);
        System.out.println("Total charges patronales : " + chargesPatronales);

        System.out.println("------------- TAUX UTILISÉS -------------");
        System.out.println("Taux CNSS salariale   : " + tauxCnssSalariale);
        System.out.println("Taux CNSS patronale   : " + tauxCnssPatronale);
        System.out.println("Taux VPS              : " + tauxVps);
        System.out.println("Taux risque pro       : " + tauxRisquePro);
        System.out.println("Taux prestation familiale : " + tauxPrestationFamiliale);

        System.out.println("------------- LOGS -------------");
        if (logs != null && !logs.isEmpty()) {
            logs.forEach(log -> System.out.println("- " + log));
        } else {
            System.out.println("Aucun log disponible");
        }

        System.out.println("===========================================");
    }

}
