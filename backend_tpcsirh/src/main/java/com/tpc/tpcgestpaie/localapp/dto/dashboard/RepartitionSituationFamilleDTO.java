package com.tpc.tpcgestpaie.localapp.dto.dashboard;


import java.util.Map;

public class RepartitionSituationFamilleDTO {
    private Map<String, Long> repartition; // "SituationFamille" -> nombre d'employés

    public RepartitionSituationFamilleDTO() {}

    public RepartitionSituationFamilleDTO(Map<String, Long> repartition) {
        this.repartition = repartition;
    }

    public Map<String, Long> getRepartition() {
        return repartition;
    }

    public void setRepartition(Map<String, Long> repartition) {
        this.repartition = repartition;
    }
}
