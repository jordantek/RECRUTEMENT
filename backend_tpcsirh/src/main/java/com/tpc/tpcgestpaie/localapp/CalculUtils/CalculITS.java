package com.tpc.tpcgestpaie.localapp.CalculUtils;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

@Getter
@Setter
public class CalculITS {

    public static final int TYPE_PRIME_A = 1;
    public static final int TYPE_PRIME_B = 2;
    public static final int TYPE_PRIME_C = 3;

    // Tranches pour le barème
    private final BigDecimal trancheLimite1;
    private final BigDecimal trancheLimite2;
    private final BigDecimal trancheLimite3;
    private final BigDecimal trancheLimite4;

    // Taux
    private final BigDecimal rateTranche1;
    private final BigDecimal rateTranche2;
    private final BigDecimal rateTranche3;
    private final BigDecimal rateTranche4;
    private final BigDecimal rateTranche5;

    // Abattement
    private final BigDecimal rateAbatement;

    public CalculITS() {
        this.trancheLimite1 = BigDecimal.valueOf(60000);
        this.trancheLimite2 = BigDecimal.valueOf(150000);
        this.trancheLimite3 = BigDecimal.valueOf(250000);
        this.trancheLimite4 = BigDecimal.valueOf(500000);

        this.rateTranche1 = BigDecimal.ZERO;
        this.rateTranche2 = BigDecimal.valueOf(0.10);
        this.rateTranche3 = BigDecimal.valueOf(0.15);
        this.rateTranche4 = BigDecimal.valueOf(0.19);
        this.rateTranche5 = BigDecimal.valueOf(0.30);

        this.rateAbatement = BigDecimal.valueOf(0.25);
    }

    // Base imposable arrondie au millier inférieur
    public BigDecimal baseImposable(BigDecimal averageSalary) {
        return averageSalary
                .divide(BigDecimal.valueOf(1000), 0, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(1000));
    }

    // ITS moyen
    public BigDecimal determinationIts(BigDecimal taxableBase) {
        BigDecimal result;

        if (taxableBase.compareTo(trancheLimite1) <= 0) {
            result = taxableBase.multiply(rateTranche1);
        } else if (taxableBase.compareTo(trancheLimite2) <= 0) {
            result = taxableBase.subtract(trancheLimite1)
                    .multiply(rateTranche2);
        } else if (taxableBase.compareTo(trancheLimite3) <= 0) {
            result = trancheLimite2.subtract(trancheLimite1).multiply(rateTranche2)
                    .add(taxableBase.subtract(trancheLimite2).multiply(rateTranche3));
        } else if (taxableBase.compareTo(trancheLimite4) <= 0) {
            result = trancheLimite2.subtract(trancheLimite1).multiply(rateTranche2)
                    .add(trancheLimite3.subtract(trancheLimite2).multiply(rateTranche3))
                    .add(taxableBase.subtract(trancheLimite3).multiply(rateTranche4));
        } else {
            result = trancheLimite2.subtract(trancheLimite1).multiply(rateTranche2)
                    .add(trancheLimite3.subtract(trancheLimite2).multiply(rateTranche3))
                    .add(trancheLimite4.subtract(trancheLimite3).multiply(rateTranche4))
                    .add(taxableBase.subtract(trancheLimite4).multiply(rateTranche5));
        }

        return result.setScale(0, RoundingMode.HALF_UP);
    }


    // ITS du mois
    public BigDecimal itsDuMois(BigDecimal itsAverageSalary,
                                 BigDecimal salaryOfMonth,
                                 BigDecimal exceptionalPay,
                                 BigDecimal averageSalary,
                                 short type_prime) {

        if (type_prime == TYPE_PRIME_A) {
            exceptionalPay = exceptionalPay.subtract(
                    exceptionalPay.multiply(rateAbatement)
            );
        }

        return itsAverageSalary.multiply(salaryOfMonth.add(exceptionalPay))
                .divide(averageSalary, 0, RoundingMode.HALF_UP);
    }
}
