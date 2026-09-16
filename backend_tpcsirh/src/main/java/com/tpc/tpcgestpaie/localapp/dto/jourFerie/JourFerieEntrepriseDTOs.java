package com.tpc.tpcgestpaie.localapp.dto.jourFerie;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

/**
 * DTOs pour la gestion des jours fériés par entreprise
 */
public class JourFerieEntrepriseDTOs {

    /**
     * DTO de demande pour activer/désactiver un jour férié
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActivationJourFerieDTO {

        private Long jourFerieId;

        private Integer annee;

        private String commentaire;

        private Boolean estPaye;

        // Pour le report automatique
        private Boolean autoReportSiWeekend;

        // Pour le report manuel
        private LocalDate dateEffectiveManuelle;

        private String motifReportManuel;
    }

    /**
     * DTO pour configurer le report d'un jour férié
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReportJourFerieDTO {

        private Long jourFerieEntrepriseId;

        private LocalDate nouvelleDateEffective;

        private String motifReport;

        private Boolean estPaye;

        private String commentaire;
    }

    /**
     * DTO pour modifier le statut de paiement
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaiementJourFerieDTO {

        private Long jourFerieEntrepriseId;

        private Boolean estPaye;

        private String commentairePaiement;

        private LocalDate datePaiement;
    }

    /**
     * DTO pour la recherche/filtrage
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FiltreJourFerieEntrepriseDTO {

        private Long companyId;

        private Integer annee;

        private Integer mois;

        private Boolean estPaye;

        private Boolean estReporte;

        private LocalDate dateDebut;

        private LocalDate dateFin;

        private String searchTerm; // recherche dans libellé ou commentaire
    }

    /**
     * DTO pour la réponse paginée
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageJourFerieEntrepriseDTO {

        private java.util.List<JourFerieEntrepriseResponseDTO> content;
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }
}