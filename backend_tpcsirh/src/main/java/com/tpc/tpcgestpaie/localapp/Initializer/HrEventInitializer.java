package com.tpc.tpcgestpaie.localapp.Initializer;

import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.service.*;
import com.tpc.tpcgestpaie.localapp.util.HrEventMessageBuilder;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.ContratEmployeService;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import com.tpc.tpcgestpaie.localapp.service.HrEventService;

import com.tpc.tpcgestpaie.localapp.util.HrEventTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class HrEventInitializer implements CommandLineRunner {

    private final HrEventService hrEventService;
    private final EmployeService employeService;
    private final ContratEmployeService contratService;
    private final CompanyService companyService;

    @Override
    public void run(String... args) {
        // Démarrer le traitement async
        initializeEventsAsync();
    }

    @Async
    public void initializeEventsAsync() {
        try {
            List<Company> companies = companyService.getAll();
            for (Company company : companies) {
                List<Employe> employees = employeService.findEmployeesByCompany(company.getId());
                for (Employe emp : employees) {
                    ContratEmploye contrat = Objects.requireNonNull(contratService.getContratEmployeNonArreteParEmploye(emp).orElse(null)).toEntity();

                    // Vérifier si un événement "Anniversaire" existe
                   // boolean birthdayExists = hrEventService.existsRecurringEvent(emp, company, "Anniversaire");
                    if (emp.getDate_naissance() != null) {
                        hrEventService.createRecurringEvent(
                                emp,
                                contrat,
                                company,
                                HrEventTypes.BIRTHDAY_EMPLOYEE,
                                emp.getDate_naissance(),
                                null,
                                HrEventMessageBuilder.buildTitle(HrEventTypes.BIRTHDAY_EMPLOYEE, emp),
                                HrEventMessageBuilder.buildDescription(HrEventTypes.BIRTHDAY_EMPLOYEE, emp)
                        );
                    }


                    if (contrat.getDate_debut() != null) {
                        if (contrat.getDate_fin() != null) {
                            hrEventService.createRecurringEvent(emp, contrat, company,
                                    HrEventTypes.CONTRACT_ANNIVERSARY,
                                    contrat.getDate_debut(),
                                    contrat.getDate_fin(),
                                    HrEventMessageBuilder.buildTitle(HrEventTypes.CONTRACT_ANNIVERSARY, emp),
                                    HrEventMessageBuilder.buildDescription(HrEventTypes.CONTRACT_ANNIVERSARY, emp));
                        }else{
                            hrEventService.createRecurringEvent(emp, contrat, company,
                                    HrEventTypes.CONTRACT_ANNIVERSARY,
                                    contrat.getDate_debut(),
                                    null,
                                    HrEventMessageBuilder.buildTitle(HrEventTypes.CONTRACT_ANNIVERSARY, emp),
                                    HrEventMessageBuilder.buildDescription(HrEventTypes.CONTRACT_ANNIVERSARY, emp));
                        }
                    }

                    if (contrat.getDate_fin() != null) {
                        hrEventService.createUniqueEvent(
                                emp,
                                contrat,
                                company,
                                HrEventTypes.CONTRACT_END,
                                contrat.getDate_fin(),
                                HrEventMessageBuilder.buildTitle(HrEventTypes.CONTRACT_END, emp),
                                HrEventMessageBuilder.buildDescription(HrEventTypes.CONTRACT_END, emp),
                                true
                        );
                    }

                    if (contrat.getFin_essai() != null) {
                        hrEventService.createUniqueEvent(
                                emp,
                                contrat,
                                company,
                                HrEventTypes.TRIAL_PERIOD_END,
                                contrat.getFin_essai(),
                                HrEventMessageBuilder.buildTitle(HrEventTypes.TRIAL_PERIOD_END, emp),
                                HrEventMessageBuilder.buildDescription(HrEventTypes.TRIAL_PERIOD_END, emp),
                                true
                                );
                    }

                }
            }
            System.out.println("✅ Initialisation des événements terminée");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation des événements : " + e.getMessage());
        }
    }
}
