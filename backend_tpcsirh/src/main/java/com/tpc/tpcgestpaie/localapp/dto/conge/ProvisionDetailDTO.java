package com.tpc.tpcgestpaie.localapp.dto.conge;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProvisionDetailDTO {
    private String anneeMois;
    private BigDecimal joursAcquis;
    private BigDecimal montantProvision;
    private BigDecimal joursConsommes;
    private BigDecimal montantConsomme;
    private BigDecimal joursRestants;
    private BigDecimal montantRestant;
    private Boolean estTotalementConsommee;
}