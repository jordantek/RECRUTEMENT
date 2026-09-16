package com.tpc.tpcgestpaie.localapp.dto.DocumentsModel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DocumentModelRequestDTO(

        @NotBlank(message = "Le nom du model de document est obligatoire")
        String name,
        String code,
        Long categoryId,
        String description,
        Boolean showOnEmployeeProfile,
        @NotNull(message = "l'entreprise est obligatoire")
        Long companyId

) {}
