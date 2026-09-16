package com.tpc.tpcgestpaie.localapp.dto.conge.solde;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RapportPeriodiqueDTO {
    private Long employeId;
    private String nomEmploye;
    private LocalDate periodeDebut;
    private LocalDate periodeFin;
    private Integer nombreJoursTravailler;
    private BigDecimal totalJoursCongePris;
    private BigDecimal coutTotalConges;
    private List<DetailRapportMoisDTO> detailsMois;
}