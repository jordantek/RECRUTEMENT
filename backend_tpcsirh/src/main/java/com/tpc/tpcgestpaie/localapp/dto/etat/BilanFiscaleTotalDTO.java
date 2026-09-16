package com.tpc.tpcgestpaie.localapp.dto.etat;

import java.math.BigDecimal;

public class BilanFiscaleTotalDTO {
    private BigDecimal totalSalaireBrut;
    private BigDecimal totalSalaireBrutArrondi;
    private BigDecimal totalIts;
    private BigDecimal totalVps;
    private BigDecimal totalGeneral;

    public BilanFiscaleTotalDTO(BigDecimal totalSalaireBrut, BigDecimal totalSalaireBrutArrondi,
                                BigDecimal totalIts, BigDecimal totalVps, BigDecimal totalGeneral) {
        this.totalSalaireBrut = totalSalaireBrut;
        this.totalSalaireBrutArrondi = totalSalaireBrutArrondi;
        this.totalIts = totalIts;
        this.totalVps = totalVps;
        this.totalGeneral = totalGeneral;
    }

    // Getters et Setters
    public BigDecimal getTotalSalaireBrut() { return totalSalaireBrut; }
    public void setTotalSalaireBrut(BigDecimal totalSalaireBrut) { this.totalSalaireBrut = totalSalaireBrut; }

    public BigDecimal getTotalSalaireBrutArrondi() { return totalSalaireBrutArrondi; }
    public void setTotalSalaireBrutArrondi(BigDecimal totalSalaireBrutArrondi) { this.totalSalaireBrutArrondi = totalSalaireBrutArrondi; }

    public BigDecimal getTotalIts() { return totalIts; }
    public void setTotalIts(BigDecimal totalIts) { this.totalIts = totalIts; }

    public BigDecimal getTotalVps() { return totalVps; }
    public void setTotalVps(BigDecimal totalVps) { this.totalVps = totalVps; }

    public BigDecimal getTotalGeneral() { return totalGeneral; }
    public void setTotalGeneral(BigDecimal totalGeneral) { this.totalGeneral = totalGeneral; }
}