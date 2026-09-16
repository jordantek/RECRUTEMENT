package com.tpc.tpcgestpaie.localapp.dto.jourFerie;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JourFerieEntrepriseRequestDTO {

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    @NotNull(message = "La date est obligatoire")
    private LocalDate dateFerie;

    private String description;

    private Boolean estFixe = false;

    private Boolean estRecurrent = false;

    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;
}