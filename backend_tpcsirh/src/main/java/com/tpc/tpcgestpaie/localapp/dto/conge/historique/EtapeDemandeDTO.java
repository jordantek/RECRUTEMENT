
package com.tpc.tpcgestpaie.localapp.dto.conge.historique;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
class EtapeDemandeDTO {
    @Schema(description = "Nom étape", example = "Création")
    private String etape;

    @Schema(description = "Date", example = "2025-03-05T10:15:00")
    private LocalDateTime date;

    @Schema(description = "Acteur", example = "KARIM Benali")
    private String acteur;

    @Schema(description = "Commentaire", example = "Demande soumise pour validation")
    private String commentaire;
}