package com.tpc.tpcgestpaie.localapp.controller.paie;


import com.tpc.tpcgestpaie.localapp.dto.util.AncienneteResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/paie/utils")
public class UtilsController {

    private final ContratEmployeRepository contratEmployeRepository;

    public UtilsController(ContratEmployeRepository contratEmployeRepository) {
        this.contratEmployeRepository = contratEmployeRepository;
    }
    @GetMapping("/last-12-months")
    public ResponseEntity<?> getLast12Months(
            @RequestParam String dateRef,
            @RequestParam Long contratEmployeId
    ) {
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatterMois = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);
        LocalDate refDate;

        try {
            refDate = LocalDate.parse(dateRef, formatterDate);
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Format de date invalide. Format attendu : dd-MM-yyyy", null));
        }

        ContratEmploye contrat = contratEmployeRepository.findById(contratEmployeId).orElse(null);
        if (contrat == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Contrat introuvable", null));
        }

        LocalDate dateDebutContrat = contrat.getDate_debut();
        LocalDate dateFinContrat = contrat.getDate_fin();

        // Vérifier si refDate dans la période du contrat
        if (refDate.isBefore(dateDebutContrat) || (dateFinContrat != null && refDate.isAfter(dateFinContrat))) {
            return ResponseEntity.ok(new ApiResponse<>(false, "La date de référence ne se trouve pas dans la période du contrat.", null));
        }

        YearMonth startYM = YearMonth.from(refDate).minusMonths(11);
        YearMonth endYM = YearMonth.from(refDate);

        // Ajuster début
        YearMonth contratDebutYM = YearMonth.from(dateDebutContrat);
        if (startYM.isBefore(contratDebutYM)) {
            startYM = contratDebutYM;
        }

        // Ajuster fin
        if (dateFinContrat != null) {
            YearMonth contratFinYM = YearMonth.from(dateFinContrat);
            if (endYM.isAfter(contratFinYM)) {
                endYM = contratFinYM;
            }
        }

        // Générer mois
        List<String> months = new ArrayList<>();
        YearMonth currentYM = startYM;
        while (!currentYM.isAfter(endYM)) {
            String formatted = capitalizeFirstLetter(currentYM.format(formatterMois));
            months.add(formatted);
            currentYM = currentYM.plusMonths(1);
        }

        // Déterminer fin pour calcul ancienneté
        LocalDate finCalcul = dateFinContrat != null && dateFinContrat.isBefore(endYM.atEndOfMonth())
                ? dateFinContrat
                : endYM.atEndOfMonth();

        double anciennete = getNumberOfYearsBetweenTwoDates(
                dateDebutContrat.format(formatterDate),
                finCalcul.format(formatterDate)
        );

        AncienneteResponseDTO response = new AncienneteResponseDTO(
                dateDebutContrat,
                dateFinContrat,
                anciennete,
                months
        );

        return ResponseEntity.ok(new ApiResponse<>(true, "Dates récupérées.", response));
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public double getNumberOfYearsBetweenTwoDates(String dateDebut, String dateFin) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate startDate = LocalDate.parse(dateDebut, formatter);
        LocalDate endDate = LocalDate.parse(dateFin, formatter);

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        double years = daysBetween / 365.25;
        return Math.round(years * 100.0) / 100.0;
    }



    // DTO de réponse
    public static class MonthsResponse {
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private List<String> months;

        public MonthsResponse(LocalDate dateDebut, LocalDate dateFin, List<String> months) {
            this.dateDebut = dateDebut;
            this.dateFin = dateFin;
            this.months = months;
        }

        public LocalDate getDateDebut() {
            return dateDebut;
        }

        public void setDateDebut(LocalDate dateDebut) {
            this.dateDebut = dateDebut;
        }

        public LocalDate getDateFin() {
            return dateFin;
        }

        public void setDateFin(LocalDate dateFin) {
            this.dateFin = dateFin;
        }

        public List<String> getMonths() {
            return months;
        }

        public void setMonths(List<String> months) {
            this.months = months;
        }
    }
}