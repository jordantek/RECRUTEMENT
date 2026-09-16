package com.tpc.tpcgestpaie.localapp.service.Alertes;

import com.tpc.tpcgestpaie.localapp.dto.alertes.EmployeAlerteDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.alertes.AlerteConfiguration;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import com.tpc.tpcgestpaie.localapp.service.users.UserCompanyAccessService;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AlerteAnniversaireService {

    private final EmployeRepository employeRepository;
    private final AlerteConfigurationService configurationService;
    private final NotificationService notificationService;
    private final UserCompanyAccessService userCompanyAccessService;

    public AlerteAnniversaireService(EmployeRepository employeRepository,
                                     AlerteConfigurationService configurationService,
                                     NotificationService notificationService,
                                     UserCompanyAccessService userCompanyAccessService) {
        this.employeRepository = employeRepository;
        this.configurationService = configurationService;
        this.notificationService = notificationService;
        this.userCompanyAccessService = userCompanyAccessService;
    }

    /**
     * Vérifie les anniversaires pour l'utilisateur connecté
     */
    public void verifierAnniversaires(User currentUser) {
        if (currentUser == null) {
            return;
        }

        // Récupérer la configuration de l'utilisateur
        AlerteConfiguration config = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.ANNIVERSAIRE_NAISSANCE,
                currentUser
        );

        if (!config.isEnabled()) {
            return;
        }

        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);

        if (accessibleCompanyIds.isEmpty()) {
            log.info("Aucune entreprise accessible pour l'utilisateur {}", currentUser.getUsername());
            return;
        }

        List<Employe> employes = employeRepository.findByCompanyIds(accessibleCompanyIds);
        LocalDate aujourdhui = LocalDate.now();

        for (Employe employe : employes) {
            verifierAlertePourEmploye(employe, aujourdhui, currentUser, config);
        }

        log.info("Vérification des anniversaires terminée pour {} employés", employes.size());
    }

    /**
     * Vérifie les anniversaires pour toutes les entreprises (cron job)
     * Cette méthode notifie tous les utilisateurs ayant activé les alertes anniversaire
     */
    @Scheduled(cron = "0 0 8 * * ?") // Exécution quotidienne à 8h
    public void verifierAnniversairesAutomatique() {
        log.info("Début de la vérification automatique des anniversaires");

        // Cette méthode utilise directement tous les employés avec contrats actifs
        List<Employe> employes = employeRepository.findAll().stream()
                .filter(e -> e.getDeleted_at() == null)
                .filter(e -> !employeRepository.findCompaniesByEmployeId(e.getId()).isEmpty())
                .collect(Collectors.toList());

        LocalDate aujourdhui = LocalDate.now();

        // Pour chaque employé, vérifier s'il y a des utilisateurs à notifier
        for (Employe employe : employes) {
            verifierAlertePourEmployeAutomatique(employe, aujourdhui);
        }

        log.info("Vérification automatique des anniversaires terminée pour {} employés", employes.size());
    }

    /**
     * Vérifie les alertes pour un employé spécifique (mode manuel avec configuration utilisateur)
     */
    private void verifierAlertePourEmploye(Employe employe, LocalDate dateReference, User currentUser, AlerteConfiguration config) {
        try {
            LocalDate prochainAnniversaire = employe.getProchainAnniversaire();
            long joursRestants = employe.getJoursRestants();

            // Utiliser les échéances configurées par l'utilisateur
            List<Integer> echeances = config.getEcheancesJoursAsList();

            for (Integer echeance : echeances) {
                if (joursRestants == echeance) {
                    envoyerAlerte(employe, echeance, currentUser);
                    break;
                }
            }

            // Alerte pour le jour même si configuré
            if (config.isNotifierJourJ() && joursRestants == 0) {
                envoyerAlerteJourJ(employe, currentUser);
            }
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de l'anniversaire pour l'employé {}: {}",
                    employe.getId(), e.getMessage());
        }
    }

    /**
     * Vérifie les alertes pour un employé en mode automatique (cron job)
     * Notifie tous les utilisateurs concernés par cet employé
     */
    private void verifierAlertePourEmployeAutomatique(Employe employe, LocalDate dateReference) {
        try {
            LocalDate prochainAnniversaire = employe.getProchainAnniversaire();
            long joursRestants = employe.getJoursRestants();

            if (joursRestants < 0) {
                return; // Anniversaire déjà passé
            }

            // Récupérer tous les utilisateurs qui gèrent les entreprises de cet employé
            List<Company> companies = employeRepository.findCompaniesByEmployeId(employe.getId());
            if (companies.isEmpty()) {
                return;
            }

            // Pour chaque entreprise, trouver les utilisateurs avec alertes activées
            for (Company company : companies) {
                notifierUtilisateursConcernes(employe, company, joursRestants, dateReference);
            }

        } catch (Exception e) {
            log.error("Erreur lors de la vérification automatique de l'anniversaire pour l'employé {}: {}",
                    employe.getId(), e.getMessage());
        }
    }

    /**
     * Notifie tous les utilisateurs concernés par cet employé
     */
    private void notifierUtilisateursConcernes(Employe employe, Company company, long joursRestants, LocalDate dateReference) {
        // Implémentation pour trouver les utilisateurs ayant accès à cette entreprise
        // et ayant activé les alertes anniversaire
        // Cette méthode nécessite un service pour récupérer les utilisateurs par entreprise

        log.debug("Vérification des utilisateurs à notifier pour l'employé {} dans l'entreprise {}",
                employe.getId(), company.getName());

        // TODO: Implémenter la logique pour trouver les utilisateurs concernés
        // List<User> utilisateursConcernes = userService.findByCompanyAndAlerteActive(company.getId(), TypeAlerte.ANNIVERSAIRE_NAISSANCE);

        // Pour l'instant, on loggue seulement
        if (joursRestants == 0) {
            log.info("🎉 Anniversaire aujourd'hui pour {} {} ({})",
                    employe.getPrenom(), employe.getNom(), company.getName());
        } else if (joursRestants <= 7) {
            log.info("📅 Anniversaire dans {} jours pour {} {} ({})",
                    joursRestants, employe.getPrenom(), employe.getNom(), company.getName());
        }
    }

    private void envoyerAlerte(Employe employe, int joursRestants, User currentUser) {
        String message = String.format(
                "L'anniversaire de %s %s (%s) est dans %d jour(s) (le %s). Il/Elle aura %d ans.",
                employe.getPrenom(), employe.getNom(), getCompanyName(employe),
                joursRestants, employe.getProchainAnniversaire().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                employe.getAge() + 1
        );

        log.info("Alerte anniversaire pour {}: {}", currentUser.getUsername(), message);
        // notificationService.envoyerNotification(message, TypeAlerte.ANNIVERSAIRE, currentUser);
    }

    private void envoyerAlerteJourJ(Employe employe, User currentUser) {
        String message = String.format(
                "🎉 Aujourd'hui c'est l'anniversaire de %s %s (%s) ! Souhaitons-lui un joyeux anniversaire pour ses %d ans !",
                employe.getPrenom(), employe.getNom(), getCompanyName(employe),
                employe.getAge() + 1
        );

        log.info("Alerte jour J pour {}: {}", currentUser.getUsername(), message);
        // notificationService.envoyerNotification(message, TypeAlerte.ANNIVERSAIRE_JOUR_J, currentUser);
    }

    private String getCompanyName(Employe employe) {
        List<Company> companies = employeRepository.findCompaniesByEmployeId(employe.getId());
        if (!companies.isEmpty()) {
            return companies.get(0).getName();
        }
        return "Entreprise inconnue";
    }

    /**
     * Récupère les anniversaires prochains pour l'utilisateur connecté
     */
    @Transactional
    public List<EmployeAlerteDTO> getAnniversairesProchains(User currentUser, int jours) {
        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);

        if (accessibleCompanyIds.isEmpty()) {
            return List.of();
        }

        List<Employe> employes = employeRepository.findByCompanyIds(accessibleCompanyIds);

        return employes.stream()
                .filter(e -> e.getJoursRestants() <= jours && e.getJoursRestants() >= 0)
                .sorted(Comparator.comparing(Employe::getJoursRestants))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les anniversaires prochains selon la configuration de l'utilisateur
     */
    @Transactional
    public List<EmployeAlerteDTO> getAnniversairesProchainsAvecConfig(User currentUser) {
        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);

        if (accessibleCompanyIds.isEmpty()) {
            return List.of();
        }

        // Récupérer la configuration de l'utilisateur pour déterminer la période
        AlerteConfiguration config = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.ANNIVERSAIRE_NAISSANCE,
                currentUser
        );

        if (!config.isEnabled()) {
            return List.of();
        }

        // Utiliser la plus grande échéance configurée comme période de recherche
        List<Integer> echeances = config.getEcheancesJoursAsList();
        int joursRecherche = echeances.isEmpty() ? 7 : echeances.get(0); // Premier élément (le plus grand)

        List<Employe> employes = employeRepository.findByCompanyIds(accessibleCompanyIds);

        return employes.stream()
                .filter(e -> e.getJoursRestants() <= joursRecherche && e.getJoursRestants() >= 0)
                .sorted(Comparator.comparing(Employe::getJoursRestants))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les anniversaires du mois pour l'utilisateur connecté
     */
    public List<EmployeAlerteDTO> getAnniversairesDuMois(User currentUser) {
        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);

        if (accessibleCompanyIds.isEmpty()) {
            return List.of();
        }

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();

        List<Employe> employes = employeRepository.findByCompanyIds(accessibleCompanyIds);

        return employes.stream()
                .filter(e -> e.getProchainAnniversaire().getMonthValue() == currentMonth)
                .sorted(Comparator.comparing(Employe::getProchainAnniversaire))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les anniversaires d'aujourd'hui
     */
    public List<EmployeAlerteDTO> getAnniversairesAujourdhui(User currentUser) {
        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);

        if (accessibleCompanyIds.isEmpty()) {
            return List.of();
        }

        List<Employe> employes = employeRepository.findByCompanyIds(accessibleCompanyIds);

        return employes.stream()
                .filter(e -> e.getJoursRestants() == 0)
                .sorted(Comparator.comparing(Employe::getNom))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les statistiques des anniversaires
     */
    public AnniversaireStatistiquesDTO getStatistiques(User currentUser) {
        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);

        if (accessibleCompanyIds.isEmpty()) {
            return AnniversaireStatistiquesDTO.builder()
                    .totalAujourdhui(0)
                    .totalCetteSemaine(0)
                    .totalCeMois(0)
                    .build();
        }

        List<Employe> employes = employeRepository.findByCompanyIds(accessibleCompanyIds);
        LocalDate maintenant = LocalDate.now();

        long aujourdhui = employes.stream().filter(e -> e.getJoursRestants() == 0).count();
        long cetteSemaine = employes.stream().filter(e -> e.getJoursRestants() <= 7 && e.getJoursRestants() >= 0).count();
        long ceMois = employes.stream()
                .filter(e -> e.getProchainAnniversaire().getMonthValue() == maintenant.getMonthValue())
                .count();

        return AnniversaireStatistiquesDTO.builder()
                .totalAujourdhui(aujourdhui)
                .totalCetteSemaine(cetteSemaine)
                .totalCeMois(ceMois)
                .build();
    }

    private EmployeAlerteDTO toDTO(Employe employe) {
        return EmployeAlerteDTO.builder()
                .id(employe.getId())
                .matricule(employe.getMatricule())
                .nom(employe.getNom())
                .prenom(employe.getPrenom())
                .dateNaissance(employe.getDate_naissance())
                .prochainAnniversaire(employe.getProchainAnniversaire())
                .joursRestants(employe.getJoursRestants())
                .ageActuel(employe.getAge())
                .companyName(getCompanyName(employe))
                .build();
    }

    // DTO pour les statistiques
    @Data
    @Builder
    public static class AnniversaireStatistiquesDTO {
        private long totalAujourdhui;
        private long totalCetteSemaine;
        private long totalCeMois;

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

        public long getTotalCeMois() {
            return totalCeMois;
        }

        public void setTotalCeMois(long totalCeMois) {
            this.totalCeMois = totalCeMois;
        }
    }
}