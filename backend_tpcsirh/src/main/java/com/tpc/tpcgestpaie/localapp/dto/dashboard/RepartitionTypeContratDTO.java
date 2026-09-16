package com.tpc.tpcgestpaie.localapp.dto.dashboard;

import java.util.Map;

public class RepartitionTypeContratDTO {
    private Map<String, Long> repartition; // "TypeContrat" -> nombre de contrats

    public RepartitionTypeContratDTO() {}

    public RepartitionTypeContratDTO(Map<String, Long> repartition) {
        this.repartition = repartition;
    }

    public Map<String, Long> getRepartition() {
        return repartition;
    }

    public void setRepartition(Map<String, Long> repartition) {
        this.repartition = repartition;
    }
}
