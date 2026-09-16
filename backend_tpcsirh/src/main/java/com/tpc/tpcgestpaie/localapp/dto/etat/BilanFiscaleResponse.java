package com.tpc.tpcgestpaie.localapp.dto.etat;

import java.math.BigDecimal;
import java.util.List;

public class BilanFiscaleResponse {
    private List<BilanFiscaleDTO> bulletins;
    private BigDecimal totalSalaireBrut;
    private BigDecimal totalSalaireBrutArrondi;
    private BigDecimal totalIts;
    private BigDecimal totalVps;
    private BigDecimal totalGeneral; // totalITS + totalVPS

    // getters / setters
    public List<BilanFiscaleDTO> getBulletins() {
        return bulletins;
    }

    public void setBulletins(List<BilanFiscaleDTO> bulletins) {
        this.bulletins = bulletins;
    }

    public BigDecimal getTotalSalaireBrut() {
        return totalSalaireBrut;
    }

    public void setTotalSalaireBrut(BigDecimal totalSalaireBrut) {
        this.totalSalaireBrut = totalSalaireBrut;
    }

    public BigDecimal getTotalSalaireBrutArrondi() {
        return totalSalaireBrutArrondi;
    }

    public void setTotalSalaireBrutArrondi(BigDecimal totalSalaireBrutArrondi) {
        this.totalSalaireBrutArrondi = totalSalaireBrutArrondi;
    }

    public BigDecimal getTotalIts() {
        return totalIts;
    }

    public void setTotalIts(BigDecimal totalIts) {
        this.totalIts = totalIts;
    }

    public BigDecimal getTotalVps() {
        return totalVps;
    }

    public void setTotalVps(BigDecimal totalVps) {
        this.totalVps = totalVps;
    }

    public BigDecimal getTotalGeneral() {
        return totalGeneral;
    }

    public void setTotalGeneral(BigDecimal totalGeneral) {
        this.totalGeneral = totalGeneral;
    }
}
