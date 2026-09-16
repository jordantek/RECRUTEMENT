package com.tpc.tpcgestpaie.localapp.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record HrEventCreateRequestDTO(
        Long companyId,             // optionnel
        Long employeeId,            // optionnel
        Long contratEmployeId,      // optionnel
        @NotBlank(message = "Le type d'événement est obligatoire")
        String type,                // obligatoire
        @NotNull(message = "La date de l'événement est obligatoire")
        LocalDate eventDate,        // obligatoire
        Boolean recurring,          // optionnel, default false
        LocalDate endDate,          // optionnel
        @NotBlank(message = "Le titre de l'événement est obligatoire")
        String title,               // obligatoire
        String description,      // optionnel
        Boolean actionRequired
) {}