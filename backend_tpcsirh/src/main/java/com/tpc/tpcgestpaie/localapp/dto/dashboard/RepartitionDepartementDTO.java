package com.tpc.tpcgestpaie.localapp.dto.dashboard;

import java.util.Map;

public class RepartitionDepartementDTO {
    private Map<String, Long> repartition; // "NomDépartement" -> nombre d'employés

    public RepartitionDepartementDTO() {}

    public RepartitionDepartementDTO(Map<String, Long> repartition) {
        this.repartition = repartition;
    }

    public Map<String, Long> getRepartition() {
        return repartition;
    }

    public void setRepartition(Map<String, Long> repartition) {
        this.repartition = repartition;
    }
}
