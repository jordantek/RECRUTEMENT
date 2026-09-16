package com.tpc.tpcgestpaie.localapp.dto.DocumentCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DocumentCategoryRequestDTO(

        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
        String name,

        @Size(max = 50, message = "Le code ne doit pas dépasser 50 caractères")
        String code,

        String description,

        String color,

        Integer retentionPeriod,

        @NotNull(message = "L'entreprise est obligatoire")
        Long companyId

) {}