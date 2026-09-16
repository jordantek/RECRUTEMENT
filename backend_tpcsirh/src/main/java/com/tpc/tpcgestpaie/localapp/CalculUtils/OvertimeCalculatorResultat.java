package com.tpc.tpcgestpaie.localapp.CalculUtils;

public class OvertimeCalculatorResultat {
    private final double rate12Percent;
    private final double rate35Percent;
    private final double rate50Percent;
    private final double rate100Percent;
    private final double total;

    public OvertimeCalculatorResultat(double rate12Percent, double rate35Percent, double rate50Percent, double rate100Percent) {
        this.rate12Percent = rate12Percent;
        this.rate35Percent = rate35Percent;
        this.rate50Percent = rate50Percent;
        this.rate100Percent = rate100Percent;
        this.total = rate12Percent + rate35Percent + rate50Percent + rate100Percent;
    }


    public double getTotal() {
        return total;
    }

    public double getRate12Percent() {
        return rate12Percent;
    }

    public double getRate35Percent() {
        return rate35Percent;
    }

    public double getRate50Percent() {
        return rate50Percent;
    }

    public double getRate100Percent() {
        return rate100Percent;
    }
}
