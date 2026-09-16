package com.tpc.tpcgestpaie.localapp.dto.employe;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record CreateEmployeRequestDTO(

        @NotBlank(message = "Veuillez renseigner le matricule de l’employé.")
        @Size(min = 3, max = 20, message = "Le matricule doit contenir entre 3 et 20 caractères.")
        String matricule,

        @NotNull(message = "Veuillez sélectionner un titre (Monsieur, Madame ou Mademoiselle).")
        String titre,

        @NotBlank(message = "Veuillez indiquer le nom de famille.")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères.")
        String nom,

        @NotBlank(message = "Veuillez indiquer le prénom.")
        @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères.")
        String prenom,

        @NotNull(message = "Veuillez préciser le sexe.")
        String sexe,

        @NotNull(message = "Veuillez indiquer la date de naissance.")
        @Past(message = "La date de naissance doit être dans le passé.")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date_naissance,

        @NotBlank(message = "Veuillez indiquer le lieu de naissance.")
        String lieu_naissance,

        @Size(min = 13, max = 13, message = "Numéro IFU invalide.")
        @Pattern(regexp = "^[0-9]+$", message = "Le numéro IFU doit contenir uniquement des chiffres.")
        String numero_ifu,

        @NotNull(message = "Veuillez sélectionner la situation matrimoniale.")
        String situationMatrimoniale,

        @Size(max = 20, message = "Le numéro CNSS ne doit pas dépasser 20 caractères.")
        @Pattern(regexp = "^[0-9]+$", message = "Le numéro CNSS doit contenir uniquement des chiffres.")
        String numero_cnss,

        @Pattern(
                regexp = "^[0-9]{3}_[0-9]{8,15}$",
                message = "Format invalide. Exemple attendu : 229_0191162385"
        )
        String telephone,

        @Email(message = "Le format de l'adresse email est invalide.")
        @Size(max = 150, message = "L'email ne doit pas dépasser 150 caractères.")
        String email,

        @NotBlank(message = "Veuillez indiquer le quartier de résidence.")
        String quartier,

        @NotBlank(message = "Veuillez indiquer la nationalité.")
        String nationalite,

        @Size(max = 150, message = "La profession ne doit pas dépasser 150 caractères.")
        String profession,

        String companyId,

        @Valid
        @Size(max = 10, message = "Un employé ne peut pas avoir plus de 10 enfants.")
        List<EnfantEmployeRequestDTO> enfants,

        @Valid
        @Size(max = 5, message = "Vous ne pouvez pas ajouter plus de 5 personnes à prévenir.")
        List<PersonneAPrevenirRequestDTO> personnesAPrevenir

) {}