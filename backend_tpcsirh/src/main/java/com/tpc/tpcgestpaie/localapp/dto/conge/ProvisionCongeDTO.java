package com.tpc.tpcgestpaie.localapp.dto.conge;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Provision mensuelle
@Data @Builder
public class ProvisionCongeDTO {
    private String anneeMois;
    private BigDecimal joursAcquis;
    private BigDecimal montantProvision;
    private BigDecimal joursConsommes;
    private BigDecimal montantConsomme;
    private BigDecimal joursRestants;
    private BigDecimal montantRestant;
    private Boolean estConsommee;
}