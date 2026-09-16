package com.tpc.tpcgestpaie.localapp.dto;

import jakarta.validation.constraints.NotNull;

public record ContratEmployeRubriqueUpdateDTO(
        Long id,
        @NotNull(message = "La rubrique est obligatoire")
        Long rubriqueId,
        String libelle,
        @NotNull(message = "Le montant est obligatoire")
        Double montant,
        Double montantAjout

) {
}