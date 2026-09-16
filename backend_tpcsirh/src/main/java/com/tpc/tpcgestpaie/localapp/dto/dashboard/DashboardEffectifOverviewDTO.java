package com.tpc.tpcgestpaie.localapp.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardEffectifOverviewDTO {
    private Long totalActifs;
    private Map<String, Long> repartitionGenre;
    private Map<String, Long> repartitionCategorie;
    private Map<String, Long> repartitionTrancheAge;

    public Long getTotalActifs() {
        return totalActifs;
    }

    public void setTotalActifs(Long totalActifs) {
        this.totalActifs = totalActifs;
    }

    public Map<String, Long> getRepartitionGenre() {
        return repartitionGenre;
    }

    public void setRepartitionGenre(Map<String, Long> repartitionGenre) {
        this.repartitionGenre = repartitionGenre;
    }

    public Map<String, Long> getRepartitionCategorie() {
        return repartitionCategorie;
    }

    public void setRepartitionCategorie(Map<String, Long> repartitionCategorie) {
        this.repartitionCategorie = repartitionCategorie;
    }

    public Map<String, Long> getRepartitionTrancheAge() {
        return repartitionTrancheAge;
    }

    public void setRepartitionTrancheAge(Map<String, Long> repartitionTrancheAge) {
        this.repartitionTrancheAge = repartitionTrancheAge;
    }
}
