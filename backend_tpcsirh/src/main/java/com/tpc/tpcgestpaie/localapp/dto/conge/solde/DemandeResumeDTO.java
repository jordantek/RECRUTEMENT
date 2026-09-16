package com.tpc.tpcgestpaie.localapp.dto.conge.solde;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DemandeResumeDTO {
    @Schema(description = "ID demande", example = "1001")
    private Long demandeId;

    @Schema(description = "Période", example = "10/03/2025 - 14/03/2025")
    private String periode;

    @Schema(description = "Jours", example = "4.0")
    private BigDecimal jours;

    @Schema(description = "Montant (€)", example = "410.00")
    private BigDecimal montant;

    @Schema(description = "Statut", example = "EN_COURS")
    private String statut;

    @Schema(description = "Date création")
    private LocalDateTime dateCreation;
}