package com.tpc.tpcgestpaie.localapp.dto;

public record ContratEmployeRubriqueResponseDTO(
        Long id,
        Long rubriqueId,
        String rubriqueNom,
        String libelle,
        Double montant,
        Double montantAjout
) {}