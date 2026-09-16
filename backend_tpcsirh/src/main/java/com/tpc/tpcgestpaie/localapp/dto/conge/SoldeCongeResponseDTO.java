package com.tpc.tpcgestpaie.localapp.dto.conge;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
// Réponse solde
@Data @Builder
public class SoldeCongeResponseDTO {
    private Long employeId;
    private String nomEmploye;
    private BigDecimal soldeTotal;
    private BigDecimal soldeConsomme;
    private BigDecimal soldeRestant;
    private LocalDate dateReference;
    private BigDecimal soldeInitialSaisi;
    private List<DetailSoldeDTO> details;

    @Data @Builder
    public static class DetailSoldeDTO {
        private String type;                    // "ACQUIS", "PRIS", "RESTANT"
        private BigDecimal jours;
        private BigDecimal montantEstime;       // Basé sur dernière provision
        private String periode;
    }
}