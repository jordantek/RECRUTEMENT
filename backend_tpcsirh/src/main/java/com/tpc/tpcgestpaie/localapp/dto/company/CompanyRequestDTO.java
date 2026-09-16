package com.tpc.tpcgestpaie.localapp.dto.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CompanyRequestDTO(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(min = 2, max = 150, message = "Le nom doit contenir entre 2 et 150 caractères")
        String name,
        @NotBlank(message = "Le NSS est obligatoire")
        String nss,
        @NotBlank(message = "Le RSS est obligatoire")
        String rss,
        String address,
        String webSite,
        @NotBlank(message = "Le pays est obligatoire")
        String country,
        @Email(message = "Format d'email invalide")
        String email,
       // @NotBlank(message = "Le téléphone est obligatoire")
        @Pattern(
                regexp = "^[0-9]{3}_[0-9]{8,15}$",
                message = "Format invalide. Exemple attendu : 229_0191162385"
        )
        String phone,
        @NotBlank(message = "Le RCCM est obligatoire")
        String rccm,
        @NotBlank(message = "L'IFU est obligatoire")
        String ifu,
        String directorName,
        @Pattern(
                regexp = "^[0-9]{3}_[0-9]{8,15}$",
                message = "Format invalide. Exemple attendu : 229_0191162385"
        )
        String directorPhone,
        @Email(message = "Format d'email invalide")
        String directorEmail
) {}