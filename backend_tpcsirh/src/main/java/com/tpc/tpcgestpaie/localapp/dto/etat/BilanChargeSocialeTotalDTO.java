package com.tpc.tpcgestpaie.localapp.dto.etat;

import java.math.BigDecimal;

public class BilanChargeSocialeTotalDTO {
    private BigDecimal totalSalaireBrut;
    private BigDecimal totalSalaireBrutArrondi;
    private BigDecimal totalIpts;
    private BigDecimal totalVps;
    private BigDecimal totalGeneral;

    public BilanChargeSocialeTotalDTO(BigDecimal totalSalaireBrut, BigDecimal totalSalaireBrutArrondi,
                                      BigDecimal totalIpts, BigDecimal totalVps, BigDecimal totalGeneral) {
        this.totalSalaireBrut = totalSalaireBrut;
        this.totalSalaireBrutArrondi = totalSalaireBrutArrondi;
        this.totalIpts = totalIpts;
        this.totalVps = totalVps;
        this.totalGeneral = totalGeneral;
    }

    // Getters et Setters
    public BigDecimal getTotalSalaireBrut() { return totalSalaireBrut; }
    public void setTotalSalaireBrut(BigDecimal totalSalaireBrut) { this.totalSalaireBrut = totalSalaireBrut; }

    public BigDecimal getTotalSalaireBrutArrondi() { return totalSalaireBrutArrondi; }
    public void setTotalSalaireBrutArrondi(BigDecimal totalSalaireBrutArrondi) { this.totalSalaireBrutArrondi = totalSalaireBrutArrondi; }

    public BigDecimal getTotalIpts() { return totalIpts; }
    public void setTotalIpts(BigDecimal totalIpts) { this.totalIpts = totalIpts; }

    public BigDecimal getTotalVps() { return totalVps; }
    public void setTotalVps(BigDecimal totalVps) { this.totalVps = totalVps; }

    public BigDecimal getTotalGeneral() { return totalGeneral; }
    public void setTotalGeneral(BigDecimal totalGeneral) { this.totalGeneral = totalGeneral; }
}