// SaisieSoldeInitialDTO.java
package com.tpc.tpcgestpaie.localapp.dto.conge.initialisation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaisieSoldeInitialDTO {

    @NotNull
    private Long employeId;

    @NotNull
    private LocalDate dateDebutPremierContrat;

    @NotNull
    private LocalDate dateReference;

    @NotNull
    @PositiveOrZero
    private BigDecimal joursDejaPrisTotal;

    @NotNull
    @PositiveOrZero
    private BigDecimal joursRestantTotal;

    @NotNull
    @PositiveOrZero
    private BigDecimal montantRestantEstime;

    @Valid
    private List<DetailCongePrisDTO> detailCongesPris;

    private String commentaire;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailCongePrisDTO {
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private BigDecimal joursPris;
        private String periodeConcernee;
    }
}