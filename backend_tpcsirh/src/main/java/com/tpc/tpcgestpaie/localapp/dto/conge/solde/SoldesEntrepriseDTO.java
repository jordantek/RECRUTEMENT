package com.tpc.tpcgestpaie.localapp.dto.conge.solde;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoldesEntrepriseDTO {

    // Info entreprise
    private Long companyId;
    private String nomEntreprise;

    // Synthèse
    private Integer totalEmployes;
    private BigDecimal totalJoursDisponibles;
    private BigDecimal totalValeurEstimee;
    private BigDecimal moyenneJoursParEmploye;

    // Liste des soldes individuels (même structure que SoldeCongeDTO)
    private List<SoldeEmployeResumeDTO> employes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SoldeEmployeResumeDTO {
        // Identité
        private Long employeId;
        private String matricule;
        private String nomEmploye;

        // Solde (mêmes champs que ton SoldeCongeDTO)
        private BigDecimal totalJoursAcquis;
        private BigDecimal totalJoursConsommes;
        private BigDecimal soldeJoursDisponibles;
        private BigDecimal valeurEstimeeSolde;
        private BigDecimal montantTotalProvisionne;
        private BigDecimal montantTotalConsomme;

        // Statut initialisation
        private Boolean estInitialise;

        // Alertes
        private Boolean alerteSoldeFaible;
        private Boolean alerteSoldeEpuise;
        private String messageAlerte;
    }
}