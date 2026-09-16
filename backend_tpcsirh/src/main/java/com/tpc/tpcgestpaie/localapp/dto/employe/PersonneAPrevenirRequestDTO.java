package com.tpc.tpcgestpaie.localapp.dto.employe;

import jakarta.validation.constraints.*;

public record PersonneAPrevenirRequestDTO(
        @NotBlank(message = "Veuillez indiquer le nom et prénom de la personne à prévenir.")
        @Size(max = 255, message = "Le nom ne doit pas dépasser 255 caractères.")
        String nomPrenom,

        @NotNull(message = "Veuillez sélectionner le lien de parenté.")
        String lienParenteId,

        @NotBlank(message = "Veuillez indiquer le numéro de téléphone.")
        @Pattern(
                regexp = "^[0-9]{8,15}$",
                message = "Le numéro de téléphone doit contenir entre 8 et 15 chiffres."
        )
        String telephone,

        @Size(max = 500, message = "L'adresse ne doit pas dépasser 500 caractères.")
        String adresse,

        @Email(message = "Le format de l'adresse email est invalide.")
        @Size(max = 150, message = "L'email ne doit pas dépasser 150 caractères.")
        String email
) {}
