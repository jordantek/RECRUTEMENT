package com.tpc.tpcgestpaie.localapp.dto.absence;

import com.tpc.tpcgestpaie.localapp.enums.UniteAbsence;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ConfirmationRetourDTO {

    @NotNull(message = "L'ID de la demande est obligatoire")
    private Long demandeId;

//    @NotNull(message = "L'unité de l'absence est obligatoire")
//    private UniteAbsence uniteAbsence;
//
//    @NotNull(message = "La date de retour effective est obligatoire")
//    private LocalDate dateRetourEffectif;
//
//    // Heure de retour obligatoire si unité = HEURE
//    private LocalTime heureRetourEffectif;

    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    private String commentaire;


}
