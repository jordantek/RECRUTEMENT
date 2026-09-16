package com.tpc.tpcgestpaie.localapp.dto.absence;

import com.tpc.tpcgestpaie.localapp.enums.UniteAbsence;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReportAbsenceDTO {

    @NotNull(message = "L'ID de la demande est obligatoire")
    private Long demandeId;

    @NotNull(message = "La nouvelle date de début est obligatoire")
    @Future(message = "La nouvelle date de début doit être dans le futur")
    private LocalDate nouvelleDateDebut;

    // La nouvelle date de fin peut être optionnelle pour HEURE ou DEMI_JOURNEE
    private LocalDate nouvelleDateFin;

    @NotBlank(message = "Le motif du report est obligatoire")
    @Size(max = 1000, message = "Le motif du report ne peut pas dépasser 1000 caractères")
    private String motifReport;

    // ================= UNITE =================
    @NotNull(message = "L'unité de l'absence est obligatoire")
    private UniteAbsence uniteAbsence;

    // ================= HEURES (pour absence horaire) =================
    private LocalTime nouvelleHeureDepart;
    private LocalTime nouvelleHeureArrivee;

    // ================= UTILS / LOGIQUE =================
    public boolean isHoraire() {
        return uniteAbsence == UniteAbsence.HEURE;
    }

    public boolean isDemiJournee() {
        return uniteAbsence == UniteAbsence.DEMI_JOURNEE;
    }

    public boolean isJourneeEntiere() {
        return uniteAbsence == UniteAbsence.JOURNEE_ENTIERE;
    }
}
