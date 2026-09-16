package com.tpc.tpcgestpaie.localapp.dto.conge.solde;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ConsommationResumeDTO {
    @Schema(description = "ID demande liée", example = "1001")
    private Long demandeId;

    @Schema(description = "Jours pris", example = "2.5")
    private BigDecimal jours;

    @Schema(description = "Montant (€)", example = "250.00")
    private BigDecimal montant;

    @Schema(description = "Date consommation")
    private LocalDateTime dateConsommation;
}