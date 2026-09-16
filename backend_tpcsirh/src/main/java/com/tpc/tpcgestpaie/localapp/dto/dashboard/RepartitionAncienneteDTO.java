package com.tpc.tpcgestpaie.localapp.dto.dashboard;

import java.util.Map;

public class RepartitionAncienneteDTO {
    private Map<String, Long> repartition; // "TrancheAnciennete" -> nombre d'employés

    public RepartitionAncienneteDTO() {}

    public RepartitionAncienneteDTO(Map<String, Long> repartition) {
        this.repartition = repartition;
    }

    public Map<String, Long> getRepartition() {
        return repartition;
    }

    public void setRepartition(Map<String, Long> repartition) {
        this.repartition = repartition;
    }
}
