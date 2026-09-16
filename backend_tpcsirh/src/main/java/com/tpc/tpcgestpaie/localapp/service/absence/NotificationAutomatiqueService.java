package com.tpc.tpcgestpaie.localapp.service.absence;

import com.tpc.tpcgestpaie.localapp.model.absence.DemandeAbsence;
import com.tpc.tpcgestpaie.localapp.model.absence.SuiviAbsence;
import com.tpc.tpcgestpaie.localapp.repository.absence.SuiviAbsenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationAutomatiqueService {

    private final SuiviAbsenceRepository suiviAbsenceRepository;
    private final EmailNotificationService emailService;

    /**
     * TÂCHE 1 : Notification J-1 avant départ
     * Exécutée tous les jours à 18h00
     */
    @Scheduled(cron = "0 0 18 * * *") // Tous les jours à 18h
    public void notifierDepartsDemain() {
        log.info("🔔 Démarrage notification J-1 départs...");

        LocalDate demain = LocalDate.now().plusDays(1);
        List<SuiviAbsence> suivis = suiviAbsenceRepository.findDepartsPrevusDemain(demain);

        for (SuiviAbsence suivi : suivis) {
            DemandeAbsence demande = suivi.getDemandeAbsence();

            String message = "Bonjour " + demande.getEmployeDemandeur().getPrenom() + ",\n\n" +
                    "Votre congé débute demain (" + demain + ").\n\n" +
                    "Pour confirmer votre départ, veuillez vous connecter à l'application et " +
                    "valider votre départ effectif.\n\n" +
                    "Cordialement,\n" +
                    "Service RH";

            try {
                emailService.envoyerNotificationDemande(
                        demande.getEmployeDemandeur().getEmail(),
                        "Rappel : Votre congé débute demain",
                        message
                );

                suivi.setDateNotificationDepart(LocalDateTime.now());
                suiviAbsenceRepository.save(suivi);

                log.info("✅ Notification J-1 envoyée à {}", demande.getEmployeDemandeur().getEmail());

            } catch (Exception e) {
                log.error("❌ Erreur notification J-1 pour demande {}: {}",
                        demande.getId(), e.getMessage());
            }
        }

        log.info("✅ Notification J-1 terminée. {} notifications envoyées.", suivis.size());
    }

    /**
     * TÂCHE 2 : Notification jour de départ
     * Exécutée tous les jours à 08h00
     */
    @Scheduled(cron = "0 0 8 * * *") // Tous les jours à 8h
    public void notifierDepartsAujourdhui() {
        log.info("🔔 Démarrage notification départs aujourd'hui...");

        LocalDate aujourdhui = LocalDate.now();
        List<SuiviAbsence> suivis = suiviAbsenceRepository.findDepartsAConfirmerAujourdhui(aujourdhui);

        for (SuiviAbsence suivi : suivis) {
            DemandeAbsence demande = suivi.getDemandeAbsence();

            String message = "Bonjour " + demande.getEmployeDemandeur().getPrenom() + ",\n\n" +
                    "Votre congé débute aujourd'hui.\n\n" +
                    "Merci de confirmer votre départ en vous connectant à l'application.\n\n" +
                    "Si vous ne partez pas aujourd'hui, vous pouvez reporter votre congé.\n\n" +
                    "Cordialement,\n" +
                    "Service RH";

            try {
                emailService.envoyerNotificationDemande(
                        demande.getEmployeDemandeur().getEmail(),
                        "Confirmation de départ - Congé du " + aujourdhui,
                        message
                );

                log.info("✅ Notification départ envoyée à {}", demande.getEmployeDemandeur().getEmail());

            } catch (Exception e) {
                log.error("❌ Erreur notification départ pour demande {}: {}",
                        demande.getId(), e.getMessage());
            }
        }

        log.info("✅ Notification départs terminée. {} notifications envoyées.", suivis.size());
    }

    /**
     * TÂCHE 3 : Notification jour de retour
     * Exécutée tous les jours à 08h00
     */
    @Scheduled(cron = "0 0 8 * * *") // Tous les jours à 8h
    public void notifierRetoursAujourdhui() {
        log.info("🔔 Démarrage notification retours aujourd'hui...");

        LocalDate aujourdhui = LocalDate.now();
        List<SuiviAbsence> suivis = suiviAbsenceRepository.findRetoursAConfirmerAujourdhui(aujourdhui);

        for (SuiviAbsence suivi : suivis) {
            DemandeAbsence demande = suivi.getDemandeAbsence();

            String message = "Bonjour " + demande.getEmployeDemandeur().getPrenom() + ",\n\n" +
                    "Votre congé se termine aujourd'hui.\n\n" +
                    "Bon retour parmi nous ! Merci de confirmer votre reprise en vous connectant " +
                    "à l'application.\n\n" +
                    "Si vous ne reprenez pas aujourd'hui, merci de nous en informer.\n\n" +
                    "Cordialement,\n" +
                    "Service RH";

            try {
                emailService.envoyerNotificationDemande(
                        demande.getEmployeDemandeur().getEmail(),
                        "Confirmation de retour - Fin de congé",
                        message
                );

                suivi.setDateNotificationRetour(LocalDateTime.now());
                suiviAbsenceRepository.save(suivi);

                log.info("✅ Notification retour envoyée à {}", demande.getEmployeDemandeur().getEmail());

            } catch (Exception e) {
                log.error("❌ Erreur notification retour pour demande {}: {}",
                        demande.getId(), e.getMessage());
            }
        }

        log.info("✅ Notification retours terminée. {} notifications envoyées.", suivis.size());
    }

    /**
     * TÂCHE 4 : Relance pour confirmations manquantes
     * Exécutée tous les jours à 17h00
     */
    @Scheduled(cron = "0 0 17 * * *") // Tous les jours à 17h
    public void relancerConfirmationsManquantes() {
        log.info("🔔 Démarrage relances confirmations manquantes...");

        LocalDate aujourdhui = LocalDate.now();

        // Relance départs non confirmés (départ prévu il y a 1 jour)
        LocalDate hier = aujourdhui.minusDays(1);
        List<SuiviAbsence> departsEnRetard = suiviAbsenceRepository.findDepartsAConfirmerAujourdhui(hier);

        for (SuiviAbsence suivi : departsEnRetard) {
            if (!suivi.getDepartConfirme()) {
                DemandeAbsence demande = suivi.getDemandeAbsence();

                String message = "Bonjour " + demande.getEmployeDemandeur().getPrenom() + ",\n\n" +
                        "Nous n'avons pas reçu votre confirmation de départ prévue le " + hier + ".\n\n" +
                        "Merci de confirmer votre situation en vous connectant à l'application.\n\n" +
                        "Cordialement,\n" +
                        "Service RH";

                try {
                    emailService.envoyerNotificationDemande(
                            demande.getEmployeDemandeur().getEmail(),
                            "URGENT - Confirmation de départ manquante",
                            message
                    );

                    log.info("✅ Relance départ envoyée à {}", demande.getEmployeDemandeur().getEmail());

                } catch (Exception e) {
                    log.error("❌ Erreur relance départ pour demande {}: {}",
                            demande.getId(), e.getMessage());
                }
            }
        }

        log.info("✅ Relances terminées.");
    }

    /**
     * TÂCHE 5 : Rapport quotidien pour RH
     * Exécutée tous les jours à 09h00
     */
    @Scheduled(cron = "0 0 9 * * *") // Tous les jours à 9h
    public void envoyerRapportQuotidien() {
        log.info("📊 Génération rapport quotidien...");

        LocalDate aujourdhui = LocalDate.now();

        List<SuiviAbsence> absencesEnCours = suiviAbsenceRepository.findAbsencesEnCours();
        List<SuiviAbsence> departsAujourdhui = suiviAbsenceRepository.findDepartsAConfirmerAujourdhui(aujourdhui);
        List<SuiviAbsence> retoursAujourdhui = suiviAbsenceRepository.findRetoursAConfirmerAujourdhui(aujourdhui);

        StringBuilder rapport = new StringBuilder();
        rapport.append("📊 RAPPORT QUOTIDIEN ABSENCES - ").append(aujourdhui).append("\n\n");
        rapport.append("===========================================\n\n");

        rapport.append("🏖️ ABSENCES EN COURS : ").append(absencesEnCours.size()).append("\n");
        for (SuiviAbsence suivi : absencesEnCours) {
            DemandeAbsence d = suivi.getDemandeAbsence();
            rapport.append("  - ").append(d.getEmployeDemandeur().getNom())
                    .append(" ").append(d.getEmployeDemandeur().getPrenom())
                    .append(" (du ").append(suivi.getDateDepartEffective())
                    .append(" au ").append(suivi.getDateRetourPrevue()).append(")\n");
        }

        rapport.append("\n✈️ DÉPARTS PRÉVUS AUJOURD'HUI : ").append(departsAujourdhui.size()).append("\n");
        for (SuiviAbsence suivi : departsAujourdhui) {
            DemandeAbsence d = suivi.getDemandeAbsence();
            rapport.append("  - ").append(d.getEmployeDemandeur().getNom())
                    .append(" ").append(d.getEmployeDemandeur().getPrenom())
                    .append(" (").append(suivi.getJoursPrevu()).append(" jours)\n");
        }

        rapport.append("\n🏢 RETOURS PRÉVUS AUJOURD'HUI : ").append(retoursAujourdhui.size()).append("\n");
        for (SuiviAbsence suivi : retoursAujourdhui) {
            DemandeAbsence d = suivi.getDemandeAbsence();
            rapport.append("  - ").append(d.getEmployeDemandeur().getNom())
                    .append(" ").append(d.getEmployeDemandeur().getPrenom()).append("\n");
        }

        rapport.append("\n===========================================\n");
        rapport.append("Ce rapport est généré automatiquement chaque jour à 9h.\n");

        try {
            // Envoyer à l'adresse RH (à configurer)
            String emailRH = "rh@entreprise.com"; // À paramétrer

            emailService.envoyerNotificationDemande(
                    emailRH,
                    "Rapport quotidien absences - " + aujourdhui,
                    rapport.toString()
            );

            log.info("✅ Rapport quotidien envoyé à {}", emailRH);

        } catch (Exception e) {
            log.error("❌ Erreur envoi rapport quotidien: {}", e.getMessage());
        }
    }
}