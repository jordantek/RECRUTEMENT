package com.tpc.tpcgestpaie.localapp.service.Alertes;

import com.tpc.tpcgestpaie.localapp.dto.alertes.AllAlertesDTO;
import com.tpc.tpcgestpaie.localapp.repository.UserCompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.alertes.AlerteRepository;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class AlerteService {
    private final AlerteAbsenceService absenceService;
    private final UserCompanyRepository userCompanyRepository;
    private final UserService userService;
    private final AlerteRepository alerteRepository;

    public AlerteService(AlerteAbsenceService absenceService, UserCompanyRepository userCompanyRepository, UserService userService, AlerteRepository alerteRepository) {
        this.absenceService = absenceService;
        this.userCompanyRepository = userCompanyRepository;
        this.userService = userService;
        this.alerteRepository = alerteRepository;

    }

    public AllAlertesDTO getAllAlertes(int days) {

       /* User currentUser = userService.getCurrentUser();
        // 2️⃣ Récupérer les entreprises auxquelles il appartient
        List<?> companiesIds = userCompanyRepository.findByUserId(currentUser.getId());

        if (companiesIds.isEmpty()) {
            return new AllAlertesDTO(List.of());
        }*/

        LocalDate today = LocalDate.now();
        LocalDate targetDate = LocalDate.now().plusDays(days);

        System.out.println(today);
        System.out.println(targetDate);
        List<?> anniversaires = alerteRepository.findAnniversairesBetween(today,targetDate);
        List<?> contratsQuiArriventATermes = alerteRepository.findContratsQuiArriventATermes(today,targetDate);
        List<?> essaiQuiArriventATermes = alerteRepository.findEssaiQuiArriventATermes(today,targetDate);
        List<?> debutAbsence = alerteRepository.findDebutAbsenceBetween(today,targetDate);
        List<?> finAbsence = alerteRepository.findFinAbsenceBetween(today,targetDate);
        return new AllAlertesDTO(anniversaires,contratsQuiArriventATermes,essaiQuiArriventATermes,debutAbsence,finAbsence);
    }
}
