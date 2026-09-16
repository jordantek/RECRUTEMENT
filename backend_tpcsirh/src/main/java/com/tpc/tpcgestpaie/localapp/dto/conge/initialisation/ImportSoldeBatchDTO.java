package com.tpc.tpcgestpaie.localapp.dto.conge.initialisation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportSoldeBatchDTO {

    private int totalLignes;
    private int succes;
    private int erreurs;
    private int warnings;

    private List<LigneImportSoldeDTO> details;

    private String rapportGlobal;
}