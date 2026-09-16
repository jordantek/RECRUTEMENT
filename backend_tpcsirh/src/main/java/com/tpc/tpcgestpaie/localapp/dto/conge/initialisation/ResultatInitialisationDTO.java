// ResultatInitialisationDTO.java
package com.tpc.tpcgestpaie.localapp.dto.conge.initialisation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultatInitialisationDTO {
    private Long employeId;
    private String nomEmploye;
    private LocalDate dateDebutContrat;
    private LocalDate dateReference;
    private Integer nombreMoisAnciennete;
    private BigDecimal joursDejaPris;
    private BigDecimal joursRestants;
    private BigDecimal montantRestantEstime;
    private Long provisionId;
    private String statut;
    private String typeProvision;
    private String messageSucces;
}