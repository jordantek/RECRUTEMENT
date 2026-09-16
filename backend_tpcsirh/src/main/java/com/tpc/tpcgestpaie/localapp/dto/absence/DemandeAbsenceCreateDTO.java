package com.tpc.tpcgestpaie.localapp.dto.absence;

import com.tpc.tpcgestpaie.localapp.enums.UniteAbsence;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DemandeAbsenceCreateDTO {

    @NotNull(message = "L'ID de employé est obligatoire")
    private Long employeId;

    @NotNull(message = "Le type d'absence est obligatoire")
    private Long typeAbsenceId;

    @NotBlank(message = "Le motif de la demande est obligatoire")
    @Size(min = 5, max = 1000, message = "Le motif doit contenir entre 5 et 1000 caractères")
    private String motifDemande;

    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début doit être aujourd'hui ou dans le futur")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    // Justificatif (optionnel selon type)
    private MultipartFile justificatif;

    @NotNull(message = "L'unité de l'absence est obligatoire")
    private UniteAbsence uniteAbsence;

    // Heures prévues (optionnel si unité = HEURE)
    private LocalTime heureDepart;
    private LocalTime heureArrivee;

}
