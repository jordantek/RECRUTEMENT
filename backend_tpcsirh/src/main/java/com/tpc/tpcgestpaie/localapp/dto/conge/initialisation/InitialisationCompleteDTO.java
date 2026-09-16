package com.tpc.tpcgestpaie.localapp.dto.conge.initialisation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class InitialisationCompleteDTO {

    @NotNull
    private Long employeId;

    @NotNull
    private LocalDate dateDebutPremierContrat;

    @NotNull
    private LocalDate dateReference;

    @PositiveOrZero
    @Builder.Default
    private BigDecimal soldeReportAnneePrecedente = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private BigDecimal joursDejaPrisTotal;

    private List<DetailCongePrisDTO> detailCongesPris;

    @NotNull
    @Positive
    private BigDecimal tauxAcquisitionMensuel;

    private BigDecimal salaireReference;

    private String commentaire;

    @Data
    @Builder
    public static class DetailCongePrisDTO {
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private BigDecimal joursPris;
        private String periodeConcernee;
    }
}