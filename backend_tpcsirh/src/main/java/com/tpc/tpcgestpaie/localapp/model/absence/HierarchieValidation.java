package com.tpc.tpcgestpaie.localapp.model.absence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HierarchieValidation implements Serializable {

    @JsonProperty("niveaux")
    private List<NiveauValidateur> niveaux = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NiveauValidateur implements Serializable {

        @JsonProperty("ordre")
        private Integer ordre; // 1, 2, 3...

        @JsonProperty("contrat_superieur_id")
        private Long contratSuperieurId;

        @JsonProperty("employe_superieur_id")
        private Long employeSuperieurId;

        @JsonProperty("fonction")
        private String fonction;

        @JsonProperty("nomComplet")
        private String nomComplet;

        @JsonProperty("statut")
        private String statut; // "EN_ATTENTE", "APPROUVE", "REJETE"

        @JsonProperty("date_action")
        private String dateAction; // LocalDateTime en String

        @JsonProperty("commentaire")
        private String commentaire;

        @JsonProperty("raison_rejet")
        private String raisonRejet;

 private GlobalEnums.ConditionAcceptationConge conditionAcceptationConge;

    }
}