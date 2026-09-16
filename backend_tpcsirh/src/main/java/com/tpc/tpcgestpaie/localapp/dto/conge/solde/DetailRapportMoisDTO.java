package com.tpc.tpcgestpaie.localapp.dto.conge.solde;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;




@Data
@Builder
public class DetailRapportMoisDTO {
    private String moisReference;
    private String moisLibelle;
    private BigDecimal joursConsommes;
    private BigDecimal montantConsomme;
    private Integer nombreDemandes;
}