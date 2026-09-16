package com.tpc.tpcgestpaie.localapp.dto.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record HrAlertSettingsRequestDTO(

        // NULL = configuration globale
        Long companyId,

        @NotNull(message = "L'id de lutilisateur es obligatoire")
        Long userId,

        @NotNull(message = "Le nombre de jours pour les événements à venir est obligatoire")
        @Min(value = 1, message = "Le nombre de jours doit être au minimum 1")
        @Max(value = 365, message = "Le nombre de jours ne peut pas dépasser 365")
        Integer upcomingEventsDays,

        @NotNull(message = "Le nombre de jours pour les actions requises est obligatoire")
        @Min(value = 1, message = "Le nombre de jours doit être au minimum 1")
        @Max(value = 300, message = "Le nombre de jours ne peut pas dépasser 90")
        Integer requiredActionsDays,

        @NotNull(message = "Le champ enabled est obligatoire")
        Boolean enabled

) {
}
