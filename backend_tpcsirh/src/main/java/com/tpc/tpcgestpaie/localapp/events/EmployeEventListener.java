package com.tpc.tpcgestpaie.localapp.events;


import com.tpc.tpcgestpaie.localapp.dto.event.*;
//import com.tpc.tpcgestpaie.localapp.model.Company;
//import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
//import com.tpc.tpcgestpaie.localapp.model.Employe;
//import com.tpc.tpcgestpaie.localapp.service.HrEventService;
import com.tpc.tpcgestpaie.localapp.util.HrEventMessageBuilder;

import com.tpc.tpcgestpaie.localapp.dto.event.EmployeChangedEvent;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.service.HrEventService;

import com.tpc.tpcgestpaie.localapp.util.HrEventTypes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeEventListener {

    private final HrEventService hrEventService; // Service qui gère la création/mise à jour des événements RH

    // ==========================
    // ÉVÉNEMENTS LIÉS AUX EMPLOYÉS & CONTRATS
    // ==========================

    /**
     * Écouteur principal qui gère les changements d'un employé
     * - Anniversaire de l'employé
     * - Anniversaire du contrat
     * - Fin de contrat
     * - Fin de période d'essai
     */
    @Async // Exécute l'événement de manière asynchrone pour ne pas bloquer le flux principal
    @Transactional // Garantit que toutes les opérations de création/modification sont transactionnelles
    @EventListener
    public void handleEmployeChanged(EmployeChangedEvent event) {
        Employe employe = event.employe();
        ContratEmploye contrat = event.contrat();
        Company company = event.company();

        try {
            // --------------------------
            // 1️⃣ Anniversaire de l'employé
            // --------------------------
            hrEventService.upsertRecurringEvent(
                    employe,
                    contrat,
                    company,
                    HrEventTypes.BIRTHDAY_EMPLOYEE,
                    employe.getDate_naissance(), // Date de naissance
                    null,
                    HrEventMessageBuilder.buildTitle(HrEventTypes.BIRTHDAY_EMPLOYEE, employe),
                    HrEventMessageBuilder.buildDescription(HrEventTypes.BIRTHDAY_EMPLOYEE, employe),
                    false // Événement récurrent
            );

            // --------------------------
            // 2️⃣ Anniversaire du contrat
            // --------------------------
            hrEventService.upsertRecurringEvent(
                    employe,
                    contrat,
                    company,
                    HrEventTypes.CONTRACT_ANNIVERSARY,
                    contrat.getDate_debut(), // Date de début du contrat
                    contrat.getDate_debut(),
                    HrEventMessageBuilder.buildTitle(HrEventTypes.CONTRACT_ANNIVERSARY, employe),
                    HrEventMessageBuilder.buildDescription(HrEventTypes.CONTRACT_ANNIVERSARY, employe),
                    false // Répété chaque année
            );

            // --------------------------
            // 3️⃣ Fin de contrat (unique)
            // --------------------------
            if (contrat.getDate_fin() != null) {
                hrEventService.upsertUniqueEvent(
                        employe,
                        contrat,
                        company,
                        HrEventTypes.CONTRACT_END,
                        contrat.getDate_fin(), // Date de fin de contrat
                        HrEventMessageBuilder.buildTitle(HrEventTypes.CONTRACT_END, employe),
                        HrEventMessageBuilder.buildDescription(HrEventTypes.CONTRACT_END, employe),
                        true // Un seul événement
                );
            }

            // --------------------------
            // 4️⃣ Fin de période d'essai (unique)
            // --------------------------
            if (contrat.getFin_essai() != null) {
                hrEventService.upsertUniqueEvent(
                        employe,
                        contrat,
                        company,
                        HrEventTypes.TRIAL_PERIOD_END,
                        contrat.getFin_essai(), // Date fin d'essai
                        HrEventMessageBuilder.buildTitle(HrEventTypes.TRIAL_PERIOD_END, employe),
                        HrEventMessageBuilder.buildDescription(HrEventTypes.TRIAL_PERIOD_END, employe),
                        true
                );
            }

        } catch (Exception e) {
            log.error("❌ Erreur création événements employé {}", employe.getId(), e);
        }
    }

    // ==========================
    // ÉVÉNEMENTS CONGÉS ET ABSENCES
    // ==========================

    /**
     * Début de congé
     */
    @Async
    @Transactional
    @EventListener
    public void handleLeaveStart(LeaveStartEvent event) {
        try {
            hrEventService.upsertUniqueEvent(
                    event.employe(),
                    null, // Pas de contrat associé pour un congé
                    event.company(),
                    HrEventTypes.LEAVE_START,
                    event.startDate(),
                    HrEventMessageBuilder.buildTitle(HrEventTypes.LEAVE_START, event.employe()),
                    HrEventMessageBuilder.buildDescription(HrEventTypes.LEAVE_START, event.employe()),
                    true // Événement unique
            );
        } catch (Exception e) {
            log.error("❌ Erreur création événement début de congé pour l'employé {}", event.employe().getId(), e);
        }
    }

    /**
     * Fin de congé
     */
    @Async
    @Transactional
    @EventListener
    public void handleLeaveEnd(LeaveEndEvent event) {
        try {
            hrEventService.upsertUniqueEvent(
                    event.employe(),
                    null,
                    event.company(),
                    HrEventTypes.LEAVE_END,
                    event.endDate(),
                    HrEventMessageBuilder.buildTitle(HrEventTypes.LEAVE_END, event.employe()),
                    HrEventMessageBuilder.buildDescription(HrEventTypes.LEAVE_END, event.employe()),
                    true
            );
        } catch (Exception e) {
            log.error("❌ Erreur création événement fin de congé pour l'employé {}", event.employe().getId(), e);
        }
    }

    /**
     * Début d'absence
     */
    @Async
    @Transactional
    @EventListener
    public void handleAbsenceStart(AbsenceStartEvent event) {
        try {
            hrEventService.upsertUniqueEvent(
                    event.employe(),
                    null,
                    event.company(),
                    HrEventTypes.ABSENCE_START,
                    event.startDate(),
                    HrEventMessageBuilder.buildTitle(HrEventTypes.ABSENCE_START, event.employe()),
                    HrEventMessageBuilder.buildDescription(HrEventTypes.ABSENCE_START, event.employe()),
                    true
            );
        } catch (Exception e) {
            log.error("❌ Erreur création événement début d'absence pour l'employé {}", event.employe().getId(), e);
        }
    }

    /**
     * Fin d'absence
     */
    @Async
    @Transactional
    @EventListener
    public void handleAbsenceEnd(AbsenceEndEvent event) {
        try {
            hrEventService.upsertUniqueEvent(
                    event.employe(),
                    null,
                    event.company(),
                    HrEventTypes.ABSENCE_END,
                    event.endDate(),
                    HrEventMessageBuilder.buildTitle(HrEventTypes.ABSENCE_END, event.employe()),
                    HrEventMessageBuilder.buildDescription(HrEventTypes.ABSENCE_END, event.employe()),
                    true
            );
        } catch (Exception e) {
            log.error("❌ Erreur création événement fin d'absence pour l'employé {}", event.employe().getId(), e);
        }
    }

    @Async
    @Transactional
    @EventListener
    public void handleTrainingStart(TrainingStartEvent event) {
        try {
            hrEventService.upsertUniqueEvent(
                    event.employe(),
                    null,
                    event.company(),
                    HrEventTypes.TRAINING_START,
                    event.startDate(),
                    HrEventMessageBuilder.buildTitle(HrEventTypes.TRAINING_START, event.employe()),
                    HrEventMessageBuilder.buildDescription(HrEventTypes.TRAINING_START, event.employe()),
                    true
            );
        } catch (Exception e) {
            log.error("❌ Erreur création événement début de formation pour l'employé {}", event.employe().getId(), e);
        }
    }

    @Async
    @Transactional
    @EventListener
    public void handleTrainingEnd(TrainingEndEvent event) {
        try {
            hrEventService.upsertUniqueEvent(
                    event.employe(),
                    null,
                    event.company(),
                    HrEventTypes.TRAINING_END,
                    event.endDate(),
                    HrEventMessageBuilder.buildTitle(HrEventTypes.TRAINING_END, event.employe()),
                    HrEventMessageBuilder.buildDescription(HrEventTypes.TRAINING_END, event.employe()),
                    true
            );
        } catch (Exception e) {
            log.error("❌ Erreur création événement fin de formation pour l'employé {}", event.employe().getId(), e);
        }
    }
}

