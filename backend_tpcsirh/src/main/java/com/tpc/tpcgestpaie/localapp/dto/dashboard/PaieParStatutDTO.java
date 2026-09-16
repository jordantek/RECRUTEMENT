package com.tpc.tpcgestpaie.localapp.dto.dashboard;

import java.math.BigDecimal;

public class PaieParStatutDTO {

    private BigDecimal net;             // Net à payer
    private BigDecimal totalPrimes;     // Autres avantages / primes
    private BigDecimal totalRetenues;   // Retenues diverses
    private BigDecimal totalCnss;       // CNSS
    private BigDecimal totalIpts;       // IPTS
    private BigDecimal totalAib;        // AIB

    // Constructeurs
    public PaieParStatutDTO() {}

    public PaieParStatutDTO(BigDecimal net, BigDecimal totalPrimes, BigDecimal totalRetenues,
                            BigDecimal totalCnss, BigDecimal totalIpts, BigDecimal totalAib) {
        this.net = net;
        this.totalPrimes = totalPrimes;
        this.totalRetenues = totalRetenues;
        this.totalCnss = totalCnss;
        this.totalIpts = totalIpts;
        this.totalAib = totalAib;
    }

    // Getters / Setters
    public BigDecimal getNet() { return net; }
    public void setNet(BigDecimal net) { this.net = net; }

    public BigDecimal getTotalPrimes() { return totalPrimes; }
    public void setTotalPrimes(BigDecimal totalPrimes) { this.totalPrimes = totalPrimes; }

    public BigDecimal getTotalRetenues() { return totalRetenues; }
    public void setTotalRetenues(BigDecimal totalRetenues) { this.totalRetenues = totalRetenues; }

    public BigDecimal getTotalCnss() { return totalCnss; }
    public void setTotalCnss(BigDecimal totalCnss) { this.totalCnss = totalCnss; }

    public BigDecimal getTotalIpts() { return totalIpts; }
    public void setTotalIpts(BigDecimal totalIpts) { this.totalIpts = totalIpts; }

    public BigDecimal getTotalAib() { return totalAib; }
    public void setTotalAib(BigDecimal totalAib) { this.totalAib = totalAib; }
}
