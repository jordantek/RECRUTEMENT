package com.tpc.tpcgestpaie.localapp.dto.absence;

import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ValidationNiveauDTO {

    @NotNull(message = "L'ID de la demande est obligatoire")
    private Long demandeId;

    @NotNull(message = "La décision (accepter/rejeter) est obligatoire")
    private Boolean accepter;

    @Size(max = 1000, message = "Le commentaire ne peut pas dépasser 1000 caractères")
    private String commentaire;

    @Size(max = 1000, message = "La raison du rejet ne peut pas dépasser 1000 caractères")
    private String raisonRejet;

    private GlobalEnums.ConditionAcceptationConge conditionAcceptation;
}