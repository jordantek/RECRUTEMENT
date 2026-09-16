package com.tpc.tpcgestpaie.localapp.CalculUtils;

import com.tpc.tpcgestpaie.localapp.service.ItsTrancheService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * * ITSCalculator.java
 * Classe de calcul de l'ITS (Impôt sur le revenu) pour les employés .
 * Elle permet de calculer la base imposable, l'ITS moyen et l'ITS du mois en fonction des salaires et primes.
 * * Elle utilise des tranches de revenus et des taux d'imposition définis pour effectuer les calculs.
 * * @author STDev
 * * @author Silas Dako
 * * @version 1.0
 * * Cette classe est utilisée pour calculer l'ITS en fonction des salaires mensuels et des primes.
 * * Elle prend en compte les tranches de revenus et les taux d'imposition pour déterminer le montant de l'ITS à payer.
 * * * Elle est conçue pour être utilisée dans le cadre d'une application de gestion des salaires et des impôts.
 *  * Elle fournit des fonctions pour calculer la base imposable, l'ITS moyen et l'ITS du mois en fonction des salaires et primes.
 *  * * Elle est utile pour les employeurs et les employés afin de comprendre et de gérer les obligations fiscales liées aux salaires.
 *  * * Elle peut être utilisée pour générer des rapports fiscaux et pour aider à la planification financière.
 *  * * Elle est conçue pour être facilement extensible et modifiable en fonction des besoins futurs.
 *  * * Elle est écrite en Java et utilise des fonctionnalités de la bibliothèque standard pour effectuer les calculs.
 *  * * Elle est conçue pour être utilisée dans un environnement de développement Java et peut être intégrée dans une application plus large.
 *  ** Elle est testée pour garantir son bon fonctionnement et sa précision dans les calculs d'ITS.
 *  ** Elle est conforme aux réglementations fiscales en vigueur et peut être mise à jour en fonction des changements législatifs.
 *  ** Elle est conçue pour être utilisée dans un environnement de production et peut gérer des volumes de données importants.
 */
@Component
@Getter
@Setter
public class ITSCalculator {


    private  ItsTrancheService itsTrancheService;

    static int TYPE_PRIME_A = 1;
    static int TYPE_PRIME_B = 2;
    static int TYPE_PRIME_C = 3;

    //tranche pour les barème
    private int trancheLimite1;
    private int trancheLimite2;
    private int trancheLimite3;
    private int trancheLimite4;

    //taux

    private double rateTranche1;
    private double rateTranche2;
    private double rateTranche3;
    private double rateTranche4;
    private double rateTranche5;

    //abbatement
    private double rateAbatement;

    //constructeur avec injection de dépendance
//    @Autowired
//    public ITSCalculator(ItsTrancheService itsTrancheService){
//        this.itsTrancheService = itsTrancheService;
//        initializeTranchesFromDatabase();
//    }



    /**
     * Initialise les tranches depuis la base de données
     */
//    private void initializeTranchesFromDatabase() {
//        ItsTrancheDTO itsTranche = itsTrancheService.getUnique();
//
//        if (itsTranche != null) {
//            // Récupération des limites de tranches
//            this.trancheLimite1 = itsTranche.getLimiteTranche1().intValue();
//            this.trancheLimite2 = itsTranche.getLimiteTranche2().intValue();
//            this.trancheLimite3 = itsTranche.getLimiteTranche3().intValue();
//            this.trancheLimite4 = itsTranche.getLimiteTranche4().intValue();
//
//            // Récupération des taux
//            this.rateTranche1 = itsTranche.getRateTranche1();
//            this.rateTranche2 = itsTranche.getRateTranche2();
//            this.rateTranche3 = itsTranche.getRateTranche3();
//            this.rateTranche4 = itsTranche.getRateTranche4();
//
//            // Récupération du taux d'abattement
//            this.rateAbatement = itsTranche.getRateAbattement();
//        } else {
//            // Valeurs par défaut si rien n'est trouvé en base
//            setDefaultValues();
//        }
//    }

//    private void setDefaultValues() {
//        this.trancheLimite1 = new BigDecimal("60000").intValue();
//        this.trancheLimite2 = new BigDecimal("150000").intValue();
//        this.trancheLimite3 = new BigDecimal("250000").intValue();
//        this.trancheLimite4 = new BigDecimal("500000").intValue();
//
//        this.rateTranche1 = 0.0;    // 0%
//        this.rateTranche2 = 0.036;  // 3,6%
//        this.rateTranche3 = 0.064;  // 6,4%
//        this.rateTranche4 = 0.10;   // 10%
//
//        this.rateAbatement = 0.20;  // 20%
//    }

    //constructeur
    public ITSCalculator(){
        this.trancheLimite1 = 60000;
        this.trancheLimite2 = 150000;
        this.trancheLimite3 = 250000;
        this.trancheLimite4 = 500000;

        this.rateTranche1 = 0.0;//0%
        this.rateTranche2 = 0.1;//10%
        this.rateTranche3 = 0.15;//15%
        this.rateTranche4 = 0.19;//19%
        this.rateTranche5 = 0.3;//30%

        this.rateAbatement = 0.25;//25%
    }


    /**Calcul de la somme des douze derniers mois*/
    public double sumOfLastTwelveMonths(double []salary){
        return Arrays.stream(salary).sum();
    }

    /**Calcul de la somme des douze derniers mois avec un salaire fixe*/
    public double sumOfLastTwelveMonths(double salary){
        return salary * 12.0;
    }

    /**Calcul de la moyenne des douze derniers mois*/
    public double calculateAverageSalary(double sumSalary){
        return sumSalary / 12.0;
    }

    /** Calcul de la base imposable
     * On arrondi à la tranche de 1000 la plus proche
     * */
    public int taxableBase(double averageSalary){
        return (int)(((int) (averageSalary/1000.0))*1000);
    }

    /**Calcul de l'ITS moyen
     * On arrondi à la tranche de 1000 la plus proche
     * */
    public int itsAverageSalary(int taxableBase){
        double result=0.0;
        if (taxableBase <=this.trancheLimite1){
            result= taxableBase;
        }
        else if (taxableBase <=this.trancheLimite2){
            result =  ((taxableBase - this.trancheLimite1) * this.rateTranche2);
        }
        else if (taxableBase <=this.trancheLimite3){
            int result_ = (int)((this.trancheLimite2 - this.trancheLimite1) * this.rateTranche2);
            result =  result_ + (int)((taxableBase - this.trancheLimite2) * this.rateTranche3);
        }
        else if (taxableBase <=this.trancheLimite4){
            int result_ = (int)((this.trancheLimite2 - this.trancheLimite1) * this.rateTranche2);
            int result2 = (int)((this.trancheLimite3 - this.trancheLimite2) * this.rateTranche3);
            result = result_ + result2 + (int)((taxableBase - this.trancheLimite3) * this.rateTranche4);
        }
        else{
            int result_ = (int)((this.trancheLimite2 - this.trancheLimite1) * this.rateTranche2);
            int result2 = (int)((this.trancheLimite3 - this.trancheLimite2) * this.rateTranche3);
            int result3 = (int)((this.trancheLimite4 - this.trancheLimite3) * this.rateTranche4);
            result = result_ + result2 + result3 + (int)((taxableBase - this.trancheLimite4) * this.rateTranche5);
        }
        return  (int) Math.round(result);
    }

    /**Calcul de l'ITS du mois
     * On arrondi à la tranche de 10 la plus proche
     * */
    public int itsOfMonth(int itsAverageSalary,double salaryOfMonth,double exceptionalPay,double averageSalary,short type_prime){
        if (type_prime==TYPE_PRIME_A){
            exceptionalPay = exceptionalPay-(exceptionalPay*rateAbatement);
        }
        return (int) Math.round((itsAverageSalary * ((salaryOfMonth + exceptionalPay) / averageSalary)));
        //return (int)(Math.round(((itsAverageSalary * (salaryOfMonth + exceptionalPay)) / averageSalary)/10.0)*10);
    }

    /**Calcul de l'ITS du mois
     *
     * @param salaireMoyen //salaire moyen
     * @param salaireDuMois //salaire du mois
     * @param prime //prime
     * @param type_prime  //type de prime
     * @return //int
     */

    public int itsOfMonth(double salaireMoyen,double salaireDuMois,double prime,short type_prime){
        int bi = this.taxableBase(salaireMoyen);
        int itsmoyen = this.itsAverageSalary(bi);


        return this.itsOfMonth(itsmoyen,salaireDuMois,prime,salaireMoyen,type_prime);
    }

}
