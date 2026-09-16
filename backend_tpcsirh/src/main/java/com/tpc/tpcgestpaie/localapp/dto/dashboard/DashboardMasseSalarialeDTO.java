package com.tpc.tpcgestpaie.localapp.dto.dashboard;

import java.math.BigDecimal;

public class DashboardMasseSalarialeDTO {
    private BigDecimal masseSalariale;
    private Long entrepriseId;

    public DashboardMasseSalarialeDTO() {}

    public DashboardMasseSalarialeDTO(BigDecimal masseSalariale,Long entrepriseId) {
        this.masseSalariale = masseSalariale;
    }

    public Long getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(Long entrepriseId) {
        this.entrepriseId = entrepriseId;
    }

    public BigDecimal getMasseSalariale() {
        return masseSalariale;
    }

    public void setMasseSalariale(BigDecimal masseSalariale) {
        this.masseSalariale = masseSalariale;
    }
}
