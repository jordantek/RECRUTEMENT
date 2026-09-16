package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.CalculUtils.OvertimeCalculator;
import com.tpc.tpcgestpaie.localapp.CalculUtils.OvertimeCalculatorResultat;
import com.tpc.tpcgestpaie.localapp.dto.paie.HeureSupplementaireDTO;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.AuditLogService;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.paie.HeureSupplementaireService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/paie/heures-supplementaires")
public class HeureSupplementairesController {

    private final UserService userService;
    private final HeureSupplementaireService heureSupplementaireService;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private final RequestHelper requestHelper;

    public HeureSupplementairesController(UserService userService, HeureSupplementaireService heureSupplementaireService, AuditLogService auditService, NotificationService notificationService, RequestHelper requestHelper) {
        this.userService = userService;
        this.heureSupplementaireService = heureSupplementaireService;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.requestHelper = requestHelper;
    }


    @PostMapping("/calculer")
        public ResponseEntity<?> calculerHeuresSupplementaires(@RequestBody Map<String, Object> requestBody) {
        try {
            double dayHours41To48 = Double.parseDouble(requestBody.get("dayHours41To48").toString());
            double dayHoursAbove48 = Double.parseDouble(requestBody.get("dayHoursAbove48").toString());
            double dayHoursSundayAndHoliday = Double.parseDouble(requestBody.get("dayHoursSundayAndHoliday").toString());
            double nightHoursSundayAndHoliday = Double.parseDouble(requestBody.get("nightHoursSundayAndHoliday").toString());
            double grossSalary = Double.parseDouble(requestBody.get("grossSalary").toString());

            // Calcul des heures supplémentaires
            OvertimeCalculator calculator = new OvertimeCalculator(
                    dayHours41To48,
                    dayHoursAbove48,
                    dayHoursSundayAndHoliday,
                    nightHoursSundayAndHoliday,
                    grossSalary
            );
            // Calcul des heures supplémentaires
            OvertimeCalculatorResultat result = calculator.calculateOvertime();
            return ResponseEntity.ok(new ApiResponse<>(true, "Calcul des heures supplémentaires effectué avec succès", result));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreur lors du calcul des heures supplémentaires: " + e.getMessage(), null));
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody HeureSupplementaireDTO dtoHS) {
        try {

            //Vérification d'une heure supplémentaire existante
            if (heureSupplementaireService.existePourEmployeEtMois(dtoHS.getEmployeId(), dtoHS.getMois())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(
                                "Une heure supplémentaire existe déjà pour cet employé à mois.",
                                "CONFLICT",
                                List.of("Une heure supplémentaire existe deja pour cet employé et ce mois.")
                        ));
            }
            User currentUser = userService.getCurrentUser();
            dtoHS.setLastUpdateUserId(currentUser.getId());
            HeureSupplementaireDTO saved = heureSupplementaireService.save(dtoHS);

            // Journalisation
            auditService.log(
                    "heures-supplementaires",
                    "heures_supplementaires",
                    currentUser.getId(),
                    "Ajout de heures supplémentaires par " + currentUser.getFullName(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            // Notification
            notificationService.createNotification(
                    currentUser,
                    "Heures supplémentaires enregistrées avec succès",
                    "Les heures supplémentaires ont bien été enregistrées"
            );


            return ResponseEntity.ok(new ApiResponse<>(true, "Heures supplémentaires enregistrées avec succès", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            "Erreur lors de l’enregistrement des heures supplémentaires",
                            "INTERNAL_ERROR",
                            List.of(e.getMessage())
                    ));
        }
    }




    @GetMapping("par-entreprise/{entrepriseId}")
    public ResponseEntity<?> getByEntreprise(@PathVariable Long entrepriseId) {
        try {
            List<HeureSupplementaireDTO> heureSupplementaires = heureSupplementaireService.findByEntrepriseId(entrepriseId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des heures supplémentaires de l'entreprise récupérée avec succès", heureSupplementaires));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            "Erreur lors de la récupération des heures supplémentaires de l'entreprise",
                            "INTERNAL_ERROR",
                            List.of(e.getMessage())
                    ));
        }
    }
}


