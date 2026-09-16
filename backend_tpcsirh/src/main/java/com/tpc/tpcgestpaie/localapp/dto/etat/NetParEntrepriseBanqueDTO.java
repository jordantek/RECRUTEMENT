package com.tpc.tpcgestpaie.localapp.dto.etat;

import java.math.BigDecimal;

public class NetParEntrepriseBanqueDTO {
    private String moisCalcul;
    private Long banqueId;
    private String banque;
    private String entreprise;
    private BigDecimal netApayer;

    public NetParEntrepriseBanqueDTO(String moisCalcul, Long banqueId, String banque, String entreprise, BigDecimal netApayer) {
        this.moisCalcul = moisCalcul;
        this.banqueId = banqueId;
        this.banque = banque;
        this.entreprise = entreprise;
        this.netApayer = netApayer;
    }

    public String getMoisCalcul() { return moisCalcul; }
    public Long getBanqueId() { return banqueId; }
    public String getBanque() { return banque; }
    public String getEntreprise() { return entreprise; }
    public BigDecimal getNetApayer() { return netApayer; }
}
