package com.tpc.tpcgestpaie.localapp.dto.conge;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class AjustementSoldeDTO {
    private BigDecimal joursAjoutes;      // Positif ou négatif
    private String motif;
    private LocalDate dateEffet;
}