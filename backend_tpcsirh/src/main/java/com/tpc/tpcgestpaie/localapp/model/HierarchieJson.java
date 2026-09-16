package com.tpc.tpcgestpaie.localapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HierarchieJson implements Serializable {

    @JsonProperty("superieurs")
    private List<SuperieurHierarchique> superieurs = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuperieurHierarchique implements Serializable {

        @JsonProperty("contrat_superieur_id")
        private Long contratSuperieurId;

        @JsonProperty("employe_superieur_id")
        private Long employeSuperieurId;

        @JsonProperty("fonction")
        private String fonction;

        private String companyName;
        private String nomComplet;
        private String departement;

        @JsonProperty("ordre")
        private Integer ordre; // 1, 2, 3...
    }
}