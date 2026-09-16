package com.tpc.tpcgestpaie.localapp.CalculUtils;

import java.math.BigDecimal;

public class OvertimeCalculator {
    final double RATE_12_PERCENT = 1.12;
    final double RATE_35_PERCENT = 1.35;
    final double RATE_50_PERCENT = 1.50;
    final double RATE_100_PERCENT = 2.00;
    final double MAX_RATE = 173.33;//heures max par mois

    private double dayHours41To48; //Heures Jours 41 à 48
    private double dayHoursAbove48; //Heures jour > 48
    private double dayHoursSundayAndHoliday; //Heures Jour Dim et Jr Férié
    private double nightHoursSundayAndHoliday;//Heures Nuit Dim et jr Férié
    private double grossSalary; //Salaire brut

    public OvertimeCalculator(double dayHours41To48, double dayHoursAbove48, double dayHoursSundayAndHoliday, double nightHoursSundayAndHoliday, double grossSalary) {
        this.dayHours41To48 = dayHours41To48;
        this.dayHoursAbove48 = dayHoursAbove48;
        this.dayHoursSundayAndHoliday = dayHoursSundayAndHoliday;
        this.nightHoursSundayAndHoliday = nightHoursSundayAndHoliday;
        this.grossSalary = grossSalary;
    }

    public OvertimeCalculatorResultat calculateOvertime() {
        double rate12Percent = roundToNearestUnit((this.grossSalary / MAX_RATE) * this.dayHours41To48 * RATE_12_PERCENT);
        double rate35Percent = roundToNearestUnit((this.grossSalary / MAX_RATE) * this.dayHoursAbove48 * RATE_35_PERCENT);
        double rate50Percent = roundToNearestUnit((this.grossSalary / MAX_RATE) * this.dayHoursSundayAndHoliday * RATE_50_PERCENT);
        double rate100Percent = roundToNearestUnit((this.grossSalary / MAX_RATE) * this.nightHoursSundayAndHoliday * RATE_100_PERCENT);
        return new OvertimeCalculatorResultat(rate12Percent, rate35Percent, rate50Percent, rate100Percent);
    }

    private double roundToNearestTen(double value) {
        return Math.round(value / 10.0) * 10;
    }
    private double roundToNearestUnit(double value) {
        return Math.round(value);
    }
}
