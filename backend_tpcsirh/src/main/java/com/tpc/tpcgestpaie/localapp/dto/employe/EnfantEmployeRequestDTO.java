package com.tpc.tpcgestpaie.localapp.dto.employe;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EnfantEmployeRequestDTO(
        @NotBlank(message = "Veuillez indiquer le nom de l'enfant.")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères.")
        String nom,

        @NotBlank(message = "Veuillez indiquer le prénom de l'enfant.")
        @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères.")
        String prenom,

        @NotNull(message = "Veuillez préciser le sexe de l'enfant.")
        GlobalEnums.Sexe sexe,

        @NotNull(message = "Veuillez indiquer la date de naissance de l'enfant.")
        @PastOrPresent(message = "La date de naissance doit être valide.")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dateNaissance,

        @NotBlank(message = "Veuillez indiquer le lieu de naissance.")
        String lieuNaissance,

        Boolean estDecede
) {
}
