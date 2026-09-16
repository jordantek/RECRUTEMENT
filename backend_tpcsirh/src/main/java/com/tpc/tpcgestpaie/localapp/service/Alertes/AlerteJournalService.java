package com.tpc.tpcgestpaie.localapp.service.Alertes;

import com.tpc.tpcgestpaie.localapp.dto.alertes.JournalAlerteDTO;
import com.tpc.tpcgestpaie.localapp.model.JournalRh;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.alertes.AlerteConfiguration;
import com.tpc.tpcgestpaie.localapp.repository.JournalRhRepository;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AlerteJournalService {

    private final JournalRhRepository journalRepository;
    private final AlerteConfigurationService configurationService;
    private final NotificationService notificationService;

    public AlerteJournalService(JournalRhRepository journalRepository,
                                AlerteConfigurationService configurationService,
                                NotificationService notificationService) {
        this.journalRepository = journalRepository;
        this.configurationService = configurationService;
        this.notificationService = notificationService;
    }

    /**
     * Vérifie les alertes du journal RH pour l'utilisateur connecté
     */
    public void verifierAlertesJournal(User currentUser) {
        if (currentUser == null) {
            return;
        }

        // Récupérer la configuration de l'utilisateur
        AlerteConfiguration config = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.JOURNAL_RH,
                currentUser
        );

        if (!config.isEnabled()) {
            return;
        }

        // Récupérer les événements futurs de l'utilisateur
        List<JournalRh> evenementsFuturs = journalRepository.findFutureEventsByUser(
                currentUser.getId(),
                LocalDate.now()
        );

        LocalDate aujourdhui = LocalDate.now();

        for (JournalRh journal : evenementsFuturs) {
            verifierAlertePourJournal(journal, aujourdhui, currentUser, config);
        }

        log.info("Vérification des alertes journal RH terminée pour {} événements", evenementsFuturs.size());
    }

    /**
     * Vérification automatique programmée
     */
    @Scheduled(cron = "0 0 8 * * ?") // Exécution quotidienne à 8h
    public void verifierAlertesJournalAutomatique() {
        log.info("Début de la vérification automatique des alertes journal RH");

        // Récupérer tous les utilisateurs ayant des événements futurs
        // Cette implémentation suppose que nous avons un service pour récupérer tous les utilisateurs
        // Pour l'instant, on va logger les événements à venir

        LocalDate aujourdhui = LocalDate.now();
        LocalDate dans7Jours = aujourdhui.plusDays(7);

        // Log des événements importants des 7 prochains jours
        logEvenementsProchains(aujourdhui, dans7Jours);

        log.info("Vérification automatique des alertes journal RH terminée");
    }

    /**
     * Log des événements prochains pour le debugging
     */
    private void logEvenementsProchains(LocalDate startDate, LocalDate endDate) {
        // Implémentation pour logger les événements à venir
        // À adapter selon ta structure de données utilisateur
        log.debug("Vérification des événements journal du {} au {}", startDate, endDate);
    }

    private void verifierAlertePourJournal(JournalRh journal, LocalDate dateReference, User currentUser, AlerteConfiguration config) {
        try {
            if (journal.getDate() == null) {
                return;
            }

            long joursRestants = Period.between(dateReference, journal.getDate()).getDays();

            // Vérifier si la catégorie est importante pour l'utilisateur
            if (!isCategorieImportante(journal, config)) {
                return;
            }

            // Utiliser les échéances configurées par l'utilisateur
            List<Integer> echeances = config.getEcheancesJoursAsList();

            for (Integer echeance : echeances) {
                if (joursRestants == echeance) {
                    envoyerAlerteJournal(journal, echeance, currentUser);
                    break;
                }
            }

            // Alerte pour le jour même si configuré
            if (config.isNotifierJourJ() && joursRestants == 0) {
                envoyerAlerteJournalJourJ(journal, currentUser);
            }

        } catch (Exception e) {
            log.error("Erreur lors de la vérification de l'alerte pour le journal {}: {}",
                    journal.getId(), e.getMessage());
        }
    }

    private boolean isCategorieImportante(JournalRh journal, AlerteConfiguration config) {
        return true;
//        if (journal.getCategorieEvenement() == null) {
//            return true; // Si pas de catégorie, on notifie par défaut
//        }
//
//        List<String> categoriesImportantes = config.getMotifsImportantsAsList();
//        if (categoriesImportantes.isEmpty()) {
//            return true; // Si aucune catégorie spécifiée, notifier pour tous
//        }
//
//        String categorieLibelle = journal.getCategorieEvenement().getLibelle().toUpperCase();
//        return categoriesImportantes.stream()
//                .map(String::toUpperCase)
//                .anyMatch(categorieImportante -> categorieLibelle.contains(categorieImportante));
    }

    private void envoyerAlerteJournal(JournalRh journal, int joursRestants, User currentUser) {
        String categorie = journal.getCategorieEvenement() != null ?
                journal.getCategorieEvenement().getLibelle() : "Non catégorisé";

        String message = String.format(
                "📝 Rappel: %s - %s dans %d jour(s) (le %s). %s",
                categorie,
                getResumeContenu(journal.getContenu()),
                joursRestants,
                journal.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                getPrioriteMessage(journal)
        );

        log.info("Alerte journal pour {}: {}", currentUser.getUsername(), message);
        // notificationService.envoyerNotification(message, TypeAlerte.JOURNAL_RH, currentUser);
    }

    private void envoyerAlerteJournalJourJ(JournalRh journal, User currentUser) {
        String categorie = journal.getCategorieEvenement() != null ?
                journal.getCategorieEvenement().getLibelle() : "Non catégorisé";

        String message = String.format(
                "🎯 Aujourd'hui: %s - %s. %s",
                categorie,
                getResumeContenu(journal.getContenu()),
                getPrioriteMessage(journal)
        );

        log.info("Alerte journal jour J pour {}: {}", currentUser.getUsername(), message);
        // notificationService.envoyerNotification(message, TypeAlerte.JOURNAL_RH_JOUR_J, currentUser);
    }

    private String getResumeContenu(String contenu) {
        if (contenu == null || contenu.length() <= 100) {
            return contenu != null ? contenu : "";
        }
        return contenu.substring(0, 100) + "...";
    }

    private String getPrioriteMessage(JournalRh journal) {
        // Déterminer la priorité basée sur la catégorie ou le contenu
        if (journal.getCategorieEvenement() != null) {
            String categorie = journal.getCategorieEvenement().getLibelle().toUpperCase();
            if (categorie.contains("URGENT") || categorie.contains("IMPORTANT")) {
                return "🚨 Priorité haute";
            } else if (categorie.contains("REUNION") || categorie.contains("ENTRETIEN")) {
                return "⚠️ Priorité moyenne";
            }
        }
        return "📋 Priorité normale";
    }

    /**
     * Récupère les événements prochains selon la configuration utilisateur
     */
    public List<JournalAlerteDTO> getEvenementsProchains(User currentUser, int jours) {
        log.info("🔍 Recherche événements pour user: {}, jours: {}", currentUser.getId(), jours);

        // Récupérer la configuration
        AlerteConfiguration config = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.JOURNAL_RH,
                currentUser
        );
        log.info("📋 Configuration JOURNAL_RH - enabled: {}, motifs: {}",
                config.isEnabled(), config.getMotifsImportants());

        // Récupérer les événements
        LocalDate aujourdhui = LocalDate.now();
        LocalDate dateLimite = aujourdhui.plusDays(jours);

        List<JournalRh> evenements = journalRepository.findUpcomingEvents(
                currentUser.getId(), aujourdhui, dateLimite
        );
        log.info("📅 Événements trouvés en base: {}", evenements.size());

        // Appliquer le filtre
        List<JournalAlerteDTO> result = evenements.stream()
                .filter(journal -> {
                    boolean important = isCategorieImportante(journal, config);
                    log.info("📝 Journal {} - Catégorie: {}, Important: {}",
                            journal.getId(),
                            journal.getCategorieEvenement() != null ?
                                    journal.getCategorieEvenement().getLibelle() : "null",
                            important);
                    return important;
                })
                .map(journal -> toDTO(journal, aujourdhui))
                .sorted(Comparator.comparing(JournalAlerteDTO::getDateEcheance))
                .collect(Collectors.toList());

        log.info("✅ Événements après filtrage: {}", result.size());
        return result;
    }
//    public List<JournalAlerteDTO> getEvenementsProchains(User currentUser, int jours) {
//        // Récupérer la configuration de l'utilisateur
//        AlerteConfiguration config = configurationService.getConfigurationForUser(
//                AlerteConfiguration.TypeAlerte.JOURNAL_RH,
//                currentUser
//        );
//
//        if (!config.isEnabled()) {
//            return List.of();
//        }
//
//        LocalDate aujourdhui = LocalDate.now();
//        LocalDate dateLimite = aujourdhui.plusDays(jours);
//
//        List<JournalRh> evenements = journalRepository.findUpcomingEvents(
//                currentUser.getId(), aujourdhui, dateLimite
//        );
//
//        return evenements.stream()
//                .filter(journal -> isCategorieImportante(journal, config)) // Filtrer par configuration
//                .map(journal -> toDTO(journal, aujourdhui))
//                .sorted(Comparator.comparing(JournalAlerteDTO::getDateEcheance))
//                .collect(Collectors.toList());
//    }

    /**
     * Récupère les événements d'aujourd'hui
     */
    public List<JournalAlerteDTO> getEvenementsAujourdhui(User currentUser) {
        LocalDate aujourdhui = LocalDate.now();
        List<JournalRh> evenements = journalRepository.findByUserIdAndDate(currentUser.getId(), aujourdhui);

        return evenements.stream()
                .map(journal -> toDTO(journal, aujourdhui))
                .sorted(Comparator.comparing(JournalAlerteDTO::getCreatedAt))
                .collect(Collectors.toList());
    }

    /**
     * Récupère les événements de la semaine
     */
    public List<JournalAlerteDTO> getEvenementsCetteSemaine(User currentUser) {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate finSemaine = aujourdhui.plusDays(7);

        return getEvenementsProchains(currentUser, 7);
    }

    /**
     * Récupère les statistiques des événements
     */
    public JournalStatistiquesDTO getStatistiques(User currentUser, int jours) {
        List<JournalAlerteDTO> evenementsProchains = getEvenementsProchains(currentUser, jours);
        List<JournalAlerteDTO> evenementsAujourdhui = getEvenementsAujourdhui(currentUser);

        long totalProchains = evenementsProchains.size();
        long totalAujourdhui = evenementsAujourdhui.size();
        long totalCetteSemaine = getEvenementsCetteSemaine(currentUser).size();

        // Compter par catégorie
        long reunions = evenementsProchains.stream()
                .filter(e -> e.getCategorieEvenement() != null &&
                        e.getCategorieEvenement().toUpperCase().contains("REUNION"))
                .count();

        long entretiens = evenementsProchains.stream()
                .filter(e -> e.getCategorieEvenement() != null &&
                        e.getCategorieEvenement().toUpperCase().contains("ENTRETIEN"))
                .count();

        return JournalStatistiquesDTO.builder()
                .totalProchains(totalProchains)
                .totalAujourdhui(totalAujourdhui)
                .totalCetteSemaine(totalCetteSemaine)
                .reunionsProchaines(reunions)
                .entretiensProchains(entretiens)
                .build();
    }

    private JournalAlerteDTO toDTO(JournalRh journal, LocalDate dateReference) {
        long joursRestants = Period.between(dateReference, journal.getDate()).getDays();

        String priorite = "MOYENNE";
        if (journal.getCategorieEvenement() != null) {
            String categorie = journal.getCategorieEvenement().getLibelle().toUpperCase();
            if (categorie.contains("URGENT") || categorie.contains("IMPORTANT")) {
                priorite = "HAUTE";
            } else if (categorie.contains("REUNION") || categorie.contains("ENTRETIEN")) {
                priorite = "MOYENNE";
            } else {
                priorite = "BASSE";
            }
        }

        return JournalAlerteDTO.builder()
                .journalId(journal.getId())
                .dateEvenement(journal.getDate())
                .contenu(journal.getContenu())
                .categorieEvenement(journal.getCategorieEvenement() != null ?
                        journal.getCategorieEvenement().getLibelle() : "Non catégorisé")
                .addedByUser(journal.getAdded_by() != null ?
                        journal.getAdded_by().getUsername() : "Inconnu")
                .createdAt(journal.getCreatedAt())
                .typeAlerte("JOURNAL_RH")
                .joursRestants(joursRestants)
                .dateEcheance(journal.getDate())
                .priorite(priorite)
                .build();
    }

    // DTO pour les statistiques
    @Data
    @Builder
    public static class JournalStatistiquesDTO {
        private long totalProchains;
        private long totalAujourdhui;
        private long totalCetteSemaine;
        private long reunionsProchaines;
        private long entretiensProchains;

        public long getTotalProchains() {
            return totalProchains;
        }

        public void setTotalProchains(long totalProchains) {
            this.totalProchains = totalProchains;
        }

        public long getTotalAujourdhui() {
            return totalAujourdhui;
        }

        public void setTotalAujourdhui(long totalAujourdhui) {
            this.totalAujourdhui = totalAujourdhui;
        }

        public long getTotalCetteSemaine() {
            return totalCetteSemaine;
        }

        public void setTotalCetteSemaine(long totalCetteSemaine) {
            this.totalCetteSemaine = totalCetteSemaine;
        }

        public long getReunionsProchaines() {
            return reunionsProchaines;
        }

        public void setReunionsProchaines(long reunionsProchaines) {
            this.reunionsProchaines = reunionsProchaines;
        }

        public long getEntretiensProchains() {
            return entretiensProchains;
        }

        public void setEntretiensProchains(long entretiensProchains) {
            this.entretiensProchains = entretiensProchains;
        }
    }
}