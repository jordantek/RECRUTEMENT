package com.tpc.tpcgestpaie.localapp.CalculUtils;

public class SalaryCalculator {

    private final double vpsRate; //
    private final double risqueProRate; // risque prof
    private final double prestationFamillialeRate; //
    private final double CNSSPatronRate;
    private final int itsTranch1;
    private final int itsTranch2;
    private final int itsTranch3;
    private final int itsTranch4;
    private final double CNSSEmployeRate=0.036;
    private final int nbrWorkingDays;
    private final int NBR_WORKING_DAYS_DEFAULT = 30;

    private final SalaryCalculatorResult calculatorResult= new SalaryCalculatorResult();

    public SalaryCalculator(){
        this.vpsRate = 0.04;
        this.risqueProRate = 0.01;
        this.prestationFamillialeRate = 0.09;
        this.CNSSPatronRate =0.064;
        this.itsTranch1 = 60000;
        this.itsTranch2 = 150000;
        this.itsTranch3 = 250000;
        this.itsTranch4 = 500000;
        this.nbrWorkingDays = 30;
    }

    public SalaryCalculator(double vpsRate, double risqueProRate, double prestationFamillialeRate, double CNSSRate, int nbrWorkingDays) {
        this.vpsRate = vpsRate;
        this.risqueProRate = risqueProRate;
        this.prestationFamillialeRate = prestationFamillialeRate;
        this.CNSSPatronRate = CNSSRate;
        this.itsTranch1 = 60000;
        this.itsTranch2 = 150000;
        this.itsTranch3 = 250000;
        this.itsTranch4 = 500000;
        this.nbrWorkingDays = nbrWorkingDays;
    }

    public SalaryCalculator(double vpsRate, double risqueProRate, double prestationFamillialeRate, double CNSSPatronRate,int nbrWorkingDays,int itsTranch1, int itsTranch2, int itsTranch3, int itsTranch4){
        this.vpsRate = vpsRate;
        this.risqueProRate = risqueProRate;
        this.prestationFamillialeRate = prestationFamillialeRate;
        this.CNSSPatronRate = CNSSPatronRate;
        this.itsTranch1 = itsTranch1;
        this.itsTranch2 = itsTranch2;
        this.itsTranch3 = itsTranch3;
        this.itsTranch4 = itsTranch4;
        this.nbrWorkingDays = nbrWorkingDays;
    }

    private int arrondiSalary(double value){
        int entier = (int)(value/1000);
        return entier*1000;
    }

    private int itsCalculate(int tranch1, int tranch2, int tranch3, int tranch4, double salaryOfMonth) {
        double its = 0.0;
        salaryOfMonth = arrondiSalary(salaryOfMonth);

        if (salaryOfMonth <= tranch1) {
            its = 0;
        } else if (salaryOfMonth <= tranch2) {
            its = (salaryOfMonth - tranch1) * 0.10;
        } else if (salaryOfMonth <= tranch3) {
            its = (tranch2 - tranch1) * 0.10 +
                    (salaryOfMonth - tranch2) * 0.15;
        } else if (salaryOfMonth <= tranch4) {
            its = (tranch2 - tranch1) * 0.10 +
                    (tranch3 - tranch2) * 0.15 +
                    (salaryOfMonth - tranch3) * 0.19;
        } else {
            its = (tranch2 - tranch1) * 0.10 +
                    (tranch3 - tranch2) * 0.15 +
                    (tranch4 - tranch3) * 0.19 +
                    (salaryOfMonth - tranch4) * 0.30;
        }

        return (int) Math.round(Math.max(its, 0));
    }

    private int itsCalculate(double salaryOfMonth) {
        // Tranches fixes par défaut (conformes au barème Bénin)
        return itsCalculate(this.itsTranch1, this.itsTranch2, this.itsTranch3, this.itsTranch4, salaryOfMonth);
    }

    public double salaryBrut(double grossSalaryContract, double bonus,double overtime){
        calculatorResult.setSalaireBrutContrat(grossSalaryContract);
        return grossSalaryContract + bonus + overtime;
    }

    /** Calcule le salaire net*/
    public double salaryNet(double salaryBrut){
        double cnss = salaryBrut*this.CNSSEmployeRate;
        double its = itsCalculate(salaryBrut);
        return Math.round(salaryBrut - cnss - its);
    }

    /** Calcule le salaire net 13e mois*/
    public double salaryNet(double salaryBrut, double its){
        double cnss = salaryBrut*this.CNSSEmployeRate;
        return Math.round(salaryBrut - cnss - its);
    }

    /** Calcule vps patronales */
    public double calculVpsPatronales(double salaireBrut){
        double vps = salaireBrut * this.vpsRate;
        vps = Math.round(vps);
        calculatorResult.setVpsPatronales(vps);
        return vps;
    }

    /** Calcule cnss patronales */
    public double calculCnssPatronales(double salaireBrut){
        double cp = salaireBrut * (this.CNSSPatronRate + this.risqueProRate + this.prestationFamillialeRate);
        cp = Math.round(cp);
        calculatorResult.setCnssPatronales(cp);
        return cp;
    }

    /** Calcule des cotisations patronales */
    public double calculChargesPatronales(double salaireBrut) {
        double chp = this.calculVpsPatronales(salaireBrut) + this.calculCnssPatronales(salaireBrut);
        chp = Math.round(chp);
        calculatorResult.setChargesPatronales(chp);
        return  chp;
    }

    /** Calcule des cotisations salariales */
    public double calculChargesSalariales(double salaryBrut) {
        double cnss = salaryBrut*this.CNSSEmployeRate;
        cnss=Math.round(cnss);
        calculatorResult.setCnssSalariale(cnss);
        double its = itsCalculate(salaryBrut);
        calculatorResult.setIts(its);
        return cnss + its;
    }


    /* Calcule de salaire*/
    /** Calcule de salaire sans heure sup*/
    public SalaryCalculatorResult salaryCalculate(double salaireBrutContrat,double primes){
        return this.salaryCalculateWithHeuresSup(salaireBrutContrat,primes,0.0);
    }

    /** Calcule de salaire avec heure sup*/
    public SalaryCalculatorResult salaryCalculateWithHeuresSup(double salaireBrutContrat,double primes,double heuresSup)
    {
        double salaireBrut = this.salaryBrut(salaireBrutContrat,primes,heuresSup);
        double chargeSalarial = this.calculChargesSalariales(salaireBrut);
        double chargePatronal = this.calculChargesPatronales(salaireBrut);
        double salaireNet = this.salaryNet(salaireBrut);
        calculatorResult.setSalaireBrut(salaireBrut);
        calculatorResult.setSalaireNet(salaireNet);
        calculatorResult.setChargesSalariales(chargeSalarial);
        calculatorResult.setChargesPatronales(chargePatronal);
        return calculatorResult;
    }

    /** Calcule de salaire avec heure sup 13e mois */
    public SalaryCalculatorResult salaryCalculateWithHeuresSup
    (double salaireBrutContrat,double primes,double heuresSup,double its)

    {
        double salaireBrut = this.salaryBrut(salaireBrutContrat,primes,heuresSup);
        double chargeSalarial = this.calculChargesSalariales(salaireBrut,its);
        double chargePatronal = this.calculChargesPatronales(salaireBrut);
        double salaireNet = this.salaryNet(salaireBrut,its);
        calculatorResult.setSalaireBrut(salaireBrut);
        calculatorResult.setSalaireNet(salaireNet);
        calculatorResult.setChargesSalariales(chargeSalarial);
        calculatorResult.setChargesPatronales(chargePatronal);
        return calculatorResult;
    }

    /** Calcule temps de salaire avec heures suplémentaire et jour de travail*/
    public SalaryCalculatorResult salaryCalculateWithHeuresSupAndWorkingDays(double salaireBrutProrater,double primes,double heuresSup,int nbrWorkingDays){
        double nwd = (double) (nbrWorkingDays * this.NBR_WORKING_DAYS_DEFAULT) /this.nbrWorkingDays;
        double sbpByDay = salaireBrutProrater/this.NBR_WORKING_DAYS_DEFAULT;
        double sbp = sbpByDay * nwd;
        calculatorResult.setJoursTravailles(nbrWorkingDays);
        return this.salaryCalculateWithHeuresSup(sbp,primes,heuresSup);
    }

    /** Calcule temps de salaire avec heures suplémentaire et jour de travail et allocation congé*/
    public SalaryCalculatorResult salaryCalculateWithHeuresSupAndWorkingDays(double salaireBrutProrater,double primes,double heuresSup,int nbrWorkingDays,double allocConge){
        double nwd = (double) (nbrWorkingDays * this.NBR_WORKING_DAYS_DEFAULT) /this.nbrWorkingDays;
        double sbpByDay = salaireBrutProrater/this.NBR_WORKING_DAYS_DEFAULT;
        double sbp = sbpByDay * nwd;
        sbp = sbp+allocConge;
        calculatorResult.setJoursTravailles(nbrWorkingDays);
        return this.salaryCalculateWithHeuresSup(sbp,primes,heuresSup);
    }

    public SalaryCalculatorResult salaryCalculateWithHeuresSupAndWorkingDays13eMois
            (double salaireBrutProrater,double primes,double heuresSup,int nbrWorkingDays,
             double allocConge, double its)
    {
        double nwd = (double) (nbrWorkingDays * this.NBR_WORKING_DAYS_DEFAULT) /this.nbrWorkingDays;
        double sbpByDay = salaireBrutProrater/this.NBR_WORKING_DAYS_DEFAULT;
        double sbp = sbpByDay * nwd;
        sbp = sbp+allocConge;
        calculatorResult.setJoursTravailles(nbrWorkingDays);
        return this.salaryCalculateWithHeuresSup(sbp,primes,heuresSup,its);
    }



    /** Calcule des cotisations salariales pour le 13e mois  */
    public double calculChargesSalariales(double salaryBrut, double its) {
        double cnss = salaryBrut*this.CNSSEmployeRate;
        cnss=Math.round(cnss);
        calculatorResult.setCnssSalariale(cnss);
        calculatorResult.setIts(its);
        return cnss + its;
    }

}
