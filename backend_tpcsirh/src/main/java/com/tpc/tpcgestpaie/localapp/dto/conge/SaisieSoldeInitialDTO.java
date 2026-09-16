package com.tpc.tpcgestpaie.localapp.dto.conge;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Saisie solde initial (migration)
@Data
public class SaisieSoldeInitialDTO {
    @NotNull
    private Long employeId;
    @NotNull @Positive
    private BigDecimal soldeJours;
    @NotNull private LocalDate dateReference;            // Fin période (optionnel)
    private String commentaire;
}
