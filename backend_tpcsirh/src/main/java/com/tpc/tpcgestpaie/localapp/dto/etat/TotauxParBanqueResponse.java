package com.tpc.tpcgestpaie.localapp.dto.etat;

import java.math.BigDecimal;
import java.util.List;

public class TotauxParBanqueResponse {
    private String moisCalcul;
    private Long banqueId;
    private String banque;
    private List<NetParEntrepriseBanqueDTO> entreprises;
    private BigDecimal totalGlobal;

    public TotauxParBanqueResponse(String moisCalcul, Long banqueId, String banque, List<NetParEntrepriseBanqueDTO> entreprises) {
        this.moisCalcul = moisCalcul;
        this.banqueId = banqueId;
        this.banque = banque;
        this.entreprises = entreprises;
        this.totalGlobal = entreprises.stream()
                .map(NetParEntrepriseBanqueDTO::getNetApayer)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public TotauxParBanqueResponse() {

    }

    public String getMoisCalcul() { return moisCalcul; }
    public Long getBanqueId() { return banqueId; }
    public String getBanque() { return banque; }
    public List<NetParEntrepriseBanqueDTO> getEntreprises() { return entreprises; }
    public BigDecimal getTotalGlobal() { return totalGlobal; }

    public void setMoisCalcul(String moisCalcul) {
        this.moisCalcul = moisCalcul;
    }

    public void setBanqueId(Long banqueId) {
        this.banqueId = banqueId;
    }

    public void setBanque(String banque) {
        this.banque = banque;
    }

    public void setEntreprises(List<NetParEntrepriseBanqueDTO> entreprises) {
        this.entreprises = entreprises;
    }

    public void setTotalGlobal(BigDecimal totalGlobal) {
        this.totalGlobal = totalGlobal;
    }
}
