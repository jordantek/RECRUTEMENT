package com.tpc.tpcgestpaie.localapp.service.Alertes;

import com.tpc.tpcgestpaie.localapp.dto.alertes.ContratAlerteDTO;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.alertes.AlerteConfiguration;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import com.tpc.tpcgestpaie.localapp.service.users.UserCompanyAccessService;
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
public class AlerteContratService {

    private final ContratEmployeRepository contratRepository;
    private final AlerteConfigurationService configurationService;
    private final NotificationService notificationService;
    private final UserCompanyAccessService userCompanyAccessService;

    public AlerteContratService(ContratEmployeRepository contratRepository,
                                AlerteConfigurationService configurationService,
                                NotificationService notificationService,
                                UserCompanyAccessService userCompanyAccessService) {
        this.contratRepository = contratRepository;
        this.configurationService = configurationService;
        this.notificationService = notificationService;
        this.userCompanyAccessService = userCompanyAccessService;
    }

    /**
     * Vérifie toutes les alertes de contrat pour l'utilisateur connecté
     */
    public void verifierAlertesContrats(User currentUser) {
        if (currentUser == null) {
            return;
        }

        // Récupérer les configurations de l'utilisateur
        AlerteConfiguration configFinEssai = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.FIN_ESSAI,
                currentUser
        );

        AlerteConfiguration configFinContrat = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.FIN_CONTRAT,
                currentUser
        );

        AlerteConfiguration configAnnivRecrutement = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.ANNIVERSAIRE_RECRUTEMENT,
                currentUser
        );

        // Si toutes les alertes sont désactivées, ne rien faire
        if (!configFinEssai.isEnabled() && !configFinContrat.isEnabled() && !configAnnivRecrutement.isEnabled()) {
            return;
        }

        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);
        if (accessibleCompanyIds.isEmpty()) {
            log.info("Aucune entreprise accessible pour l'utilisateur {}", currentUser.getUsername());
            return;
        }

        List<ContratEmploye> contrats = contratRepository.findByCompanyIdsAndActifs(accessibleCompanyIds);
        LocalDate aujourdhui = LocalDate.now();

        for (ContratEmploye contrat : contrats) {
            verifierAlertesPourContrat(contrat, aujourdhui, currentUser,
                    configFinEssai, configFinContrat, configAnnivRecrutement);
        }

        log.info("Vérification des alertes contrat terminée pour {} contrats", contrats.size());
    }

    /**
     * Vérification automatique programmée
     * Notifie tous les utilisateurs concernés par chaque contrat
     */
    @Scheduled(cron = "0 0 8 * * ?") // Exécution quotidienne à 8h
    public void verifierAlertesContratsAutomatique() {
        log.info("Début de la vérification automatique des alertes contrat");

        List<ContratEmploye> contrats = contratRepository.findContratsActifs();
        LocalDate aujourdhui = LocalDate.now();

        for (ContratEmploye contrat : contrats) {
            verifierAlertesPourContratAutomatique(contrat, aujourdhui);
        }

        log.info("Vérification automatique des alertes contrat terminée pour {} contrats", contrats.size());
    }

    private void verifierAlertesPourContrat(ContratEmploye contrat, LocalDate dateReference, User currentUser,
                                            AlerteConfiguration configFinEssai,
                                            AlerteConfiguration configFinContrat,
                                            AlerteConfiguration configAnnivRecrutement) {
        try {
            // Vérifier fin de période d'essai
            if (configFinEssai.isEnabled() && isContratImportantPourUtilisateur(contrat, configFinEssai)) {
                verifierFinEssai(contrat, dateReference, currentUser, configFinEssai);
            }

            // Vérifier fin de contrat
            if (configFinContrat.isEnabled() && isContratImportantPourUtilisateur(contrat, configFinContrat)) {
                verifierFinContrat(contrat, dateReference, currentUser, configFinContrat);
            }

            // Vérifier anniversaire de recrutement
            if (configAnnivRecrutement.isEnabled() && isContratImportantPourUtilisateur(contrat, configAnnivRecrutement)) {
                verifierAnniversaireRecrutement(contrat, dateReference, currentUser, configAnnivRecrutement);
            }

        } catch (Exception e) {
            log.error("Erreur lors de la vérification des alertes pour le contrat {}: {}",
                    contrat.getId(), e.getMessage());
        }
    }

    /**
     * Vérification automatique pour notifier tous les utilisateurs concernés
     */
    private void verifierAlertesPourContratAutomatique(ContratEmploye contrat, LocalDate dateReference) {
        try {
            log.debug("Vérification automatique pour le contrat {} de l'entreprise {}",
                    contrat.getId(), contrat.getCompany().getName());

            // Vérifier les dates et logger pour le moment
            verifierDatesContratPourLog(contrat, dateReference);

        } catch (Exception e) {
            log.error("Erreur lors de la vérification automatique pour le contrat {}: {}",
                    contrat.getId(), e.getMessage());
        }
    }

    /**
     * Vérification des dates pour le logging (en attendant l'implémentation complète)
     */
    private void verifierDatesContratPourLog(ContratEmploye contrat, LocalDate dateReference) {
        // Fin d'essai
        if (contrat.getFin_essai() != null && !contrat.isArretContrat()) {
            long joursRestantsEssai = Period.between(dateReference, contrat.getFin_essai()).getDays();
            if (joursRestantsEssai >= 0 && joursRestantsEssai <= 30) {
                log.info("📋 Contrat {} - Fin essai dans {} jours pour {} {}",
                        contrat.getId(), joursRestantsEssai,
                        contrat.getEmploye().getPrenom(), contrat.getEmploye().getNom());
            }
        }

        // Fin de contrat
        if (contrat.getDate_fin() != null && !contrat.isArretContrat()) {
            long joursRestantsContrat = Period.between(dateReference, contrat.getDate_fin()).getDays();
            if (joursRestantsContrat >= 0 && joursRestantsContrat <= 60) {
                log.info("📄 Contrat {} - Fin contrat dans {} jours pour {} {}",
                        contrat.getId(), joursRestantsContrat,
                        contrat.getEmploye().getPrenom(), contrat.getEmploye().getNom());
            }
        }

        // Anniversaire recrutement
        if (contrat.getDate_debut() != null && !contrat.isArretContrat()) {
            LocalDate anniversaire = contrat.getDate_debut().withYear(dateReference.getYear());
            if (anniversaire.isBefore(dateReference)) {
                anniversaire = anniversaire.plusYears(1);
            }
            long joursRestantsAnniv = Period.between(dateReference, anniversaire).getDays();
            if (joursRestantsAnniv >= 0 && joursRestantsAnniv <= 30) {
                int annees = Period.between(contrat.getDate_debut(), dateReference).getYears() + 1;
                log.info("🎉 Contrat {} - {} ans de service dans {} jours pour {} {}",
                        contrat.getId(), annees, joursRestantsAnniv,
                        contrat.getEmploye().getPrenom(), contrat.getEmploye().getNom());
            }
        }
    }

    private void verifierFinEssai(ContratEmploye contrat, LocalDate dateReference, User currentUser, AlerteConfiguration config) {
        if (contrat.getFin_essai() == null || contrat.isArretContrat()) {
            return;
        }

        long joursRestants = Period.between(dateReference, contrat.getFin_essai()).getDays();
        List<Integer> echeances = config.getEcheancesJoursAsList();

        for (Integer echeance : echeances) {
            if (joursRestants == echeance) {
                envoyerAlerteFinEssai(contrat, echeance, currentUser);
                break;
            }
        }

        // Alerte pour le jour même si configuré
        if (config.isNotifierJourJ() && joursRestants == 0) {
            envoyerAlerteFinEssaiJourJ(contrat, currentUser);
        }
    }

    private void verifierFinContrat(ContratEmploye contrat, LocalDate dateReference, User currentUser, AlerteConfiguration config) {
        if (contrat.getDate_fin() == null || contrat.isArretContrat()) {
            return;
        }

        long joursRestants = Period.between(dateReference, contrat.getDate_fin()).getDays();
        List<Integer> echeances = config.getEcheancesJoursAsList();

        for (Integer echeance : echeances) {
            if (joursRestants == echeance) {
                envoyerAlerteFinContrat(contrat, echeance, currentUser);
                break;
            }
        }

        // Alerte pour le jour même si configuré
        if (config.isNotifierJourJ() && joursRestants == 0) {
            envoyerAlerteFinContratJourJ(contrat, currentUser);
        }
    }

    private void verifierAnniversaireRecrutement(ContratEmploye contrat, LocalDate dateReference, User currentUser, AlerteConfiguration config) {
        if (contrat.getDate_debut() == null || contrat.isArretContrat()) {
            return;
        }

        LocalDate anniversaireCetteAnnee = contrat.getDate_debut().withYear(dateReference.getYear());
        long joursRestants = Period.between(dateReference, anniversaireCetteAnnee).getDays();

        // Si l'anniversaire est déjà passé cette année, prendre l'année prochaine
        if (joursRestants < 0) {
            anniversaireCetteAnnee = anniversaireCetteAnnee.plusYears(1);
            joursRestants = Period.between(dateReference, anniversaireCetteAnnee).getDays();
        }

        // Utiliser les échéances configurées par l'utilisateur
        List<Integer> echeances = config.getEcheancesJoursAsList();

        for (Integer echeance : echeances) {
            if (joursRestants == echeance) {
                envoyerAlerteAnniversaireRecrutement(contrat, echeance, currentUser);
                break;
            }
        }

        // Alerte pour le jour même si configuré
        if (config.isNotifierJourJ() && joursRestants == 0) {
            envoyerAlerteAnniversaireRecrutementJourJ(contrat, currentUser);
        }
    }

    private void envoyerAlerteFinEssai(ContratEmploye contrat, int joursRestants, User currentUser) {
        String message = String.format(
                "La période d'essai de %s %s (%s) se termine dans %d jour(s) (le %s). Poste: %s",
                contrat.getEmploye().getPrenom(), contrat.getEmploye().getNom(),
                contrat.getCompany().getName(), joursRestants,
                contrat.getFin_essai().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                contrat.getPoste() != null ? contrat.getPoste().getLibelle() : "Non spécifié"
        );

        log.info("Alerte fin essai pour {}: {}", currentUser.getUsername(), message);
        // notificationService.envoyerNotification(message, TypeAlerte.FIN_ESSAI, currentUser);
    }

    private void envoyerAlerteFinEssaiJourJ(ContratEmploye contrat, User currentUser) {
        String message = String.format(
                "⏰ Aujourd'hui marque la fin de la période d'essai de %s %s (%s). Poste: %s",
                contrat.getEmploye().getPrenom(), contrat.getEmploye().getNom(),
                contrat.getCompany().getName(),
                contrat.getPoste() != null ? contrat.getPoste().getLibelle() : "Non spécifié"
        );

        log.info("Alerte fin essai jour J pour {}: {}", currentUser.getUsername(), message);
        // notificationService.envoyerNotification(message, TypeAlerte.FIN_ESSAI_JOUR_J, currentUser);
    }

    private void envoyerAlerteFinContrat(ContratEmploye contrat, int joursRestants, User currentUser) {
        int anciennete = Period.between(contrat.getDate_debut(), LocalDate.now()).getYears();

        String message = String.format(
                "Le contrat de %s %s (%s) arrive à échéance dans %d jour(s) (le %s). Ancienneté: %d an(s). Poste: %s",
                contrat.getEmploye().getPrenom(), contrat.getEmploye().getNom(),
                contrat.getCompany().getName(), joursRestants,
                contrat.getDate_fin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                anciennete,
                contrat.getPoste() != null ? contrat.getPoste().getLibelle() : "Non spécifié"
        );

        log.info("Alerte fin contrat pour {}: {}", currentUser.getUsername(), message);
        // notificationService.envoyerNotification(message, TypeAlerte.FIN_CONTRAT, currentUser);
    }

    private void envoyerAlerteFinContratJourJ(ContratEmploye contrat, User currentUser) {
        int anciennete = Period.between(contrat.getDate_debut(), LocalDate.now()).getYears();

        String message = String.format(
                "📅 Le contrat de %s %s (%s) arrive à échéance aujourd'hui. Ancienneté: %d an(s). Poste: %s",
                contrat.getEmploye().getPrenom(), contrat.getEmploye().getNom(),
                contrat.getCompany().getName(), anciennete,
                contrat.getPoste() != null ? contrat.getPoste().getLibelle() : "Non spécifié"
        );

        log.info("Alerte fin contrat jour J pour {}: {}", currentUser.getUsername(), message);
        // notificationService.envoyerNotification(message, TypeAlerte.FIN_CONTRAT_JOUR_J, currentUser);
    }

    private void envoyerAlerteAnniversaireRecrutement(ContratEmploye contrat, int joursRestants, User currentUser) {
        int annees = LocalDate.now().getYear() - contrat.getDate_debut().getYear();
        LocalDate prochainAnniversaire = contrat.getDate_debut().withYear(LocalDate.now().getYear());

        if (prochainAnniversaire.isBefore(LocalDate.now())) {
            prochainAnniversaire = prochainAnniversaire.plusYears(1);
            annees++;
        }

        String message = String.format(
                "L'anniversaire de recrutement de %s %s (%s) est dans %d jour(s) (le %s). Cela fera %d an(s) de service.",
                contrat.getEmploye().getPrenom(), contrat.getEmploye().getNom(),
                contrat.getCompany().getName(), joursRestants,
                prochainAnniversaire.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                annees
        );

        log.info("Alerte anniversaire recrutement pour {}: {}", currentUser.getUsername(), message);
        // notificationService.envoyerNotification(message, TypeAlerte.ANNIVERSAIRE_RECRUTEMENT, currentUser);
    }

    private void envoyerAlerteAnniversaireRecrutementJourJ(ContratEmploye contrat, User currentUser) {
        int annees = Period.between(contrat.getDate_debut(), LocalDate.now()).getYears();

        String message = String.format(
                "🎉 Aujourd'hui marque les %d an(s) de service de %s %s (%s) ! Poste: %s",
                annees, contrat.getEmploye().getPrenom(), contrat.getEmploye().getNom(),
                contrat.getCompany().getName(),
                contrat.getPoste() != null ? contrat.getPoste().getLibelle() : "Non spécifié"
        );

        log.info("Alerte anniversaire recrutement jour J pour {}: {}", currentUser.getUsername(), message);
        // notificationService.envoyerNotification(message, TypeAlerte.ANNIVERSAIRE_RECRUTEMENT_JOUR_J, currentUser);
    }

    /**
     * Récupère les alertes de fin d'essai prochaines selon la configuration utilisateur
     */
    public List<ContratAlerteDTO> getFinsEssaiProchaines(User currentUser, int jours) {
        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);
        if (accessibleCompanyIds.isEmpty()) {
            return List.of();
        }

        // Récupérer la configuration SPÉCIFIQUE pour fin d'essai
        AlerteConfiguration config = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.FIN_ESSAI,
                currentUser
        );

        if (!config.isEnabled()) {
            log.info("Alertes fin d'essai désactivées pour l'utilisateur {}", currentUser.getUsername());
            return List.of();
        }

        List<ContratEmploye> contrats = contratRepository.findByCompanyIdsAndActifs(accessibleCompanyIds);
        LocalDate aujourdhui = LocalDate.now();
        List<Integer> echeancesConfig = config.getEcheancesJoursAsList();

        log.info("🔍 Recherche fins d'essai - Échéances: {}, Contrats: {}", echeancesConfig, contrats.size());

        return contrats.stream()
                .filter(c -> c.getFin_essai() != null && !c.isArretContrat())
                .filter(c -> isContratImportantPourUtilisateur(c, config))
                .map(c -> {
                    long joursRestants = Period.between(aujourdhui, c.getFin_essai()).getDays();
                    return new Object[]{c, joursRestants};
                })
                .filter(obj -> {
                    long joursRestants = (long) obj[1];
                    // Vérifier si le nombre de jours restants correspond aux échéances configurées
                    boolean correspondEcheance = echeancesConfig.contains((int) joursRestants);
                    log.debug("Fin essai - Jours restants: {}, Correspond: {}", joursRestants, correspondEcheance);
                    return correspondEcheance && joursRestants >= 0;
                })
                .peek(obj -> {
                    ContratEmploye c = (ContratEmploye) ((Object[]) obj)[0];
                    long joursRestants = (long) obj[1];
                    log.info("✅ Fin essai trouvée - Contrat: {}, Employé: {}, Poste: {}, Jours: {}, Date fin essai: {}",
                            c.getId(), c.getEmploye().getNom(),
                            c.getPoste() != null ? c.getPoste().getLibelle() : "N/A",
                            joursRestants, c.getFin_essai());
                })
                .sorted(Comparator.comparing(obj -> (long) ((Object[]) obj)[1]))
                .map(obj -> {
                    ContratEmploye c = (ContratEmploye) ((Object[]) obj)[0];
                    long joursRestants = (long) obj[1];
                    return toDTO(c, "FIN_ESSAI", joursRestants);
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère les alertes de fin de contrat prochaines selon la configuration utilisateur
     */
    public List<ContratAlerteDTO> getFinsContratProchaines(User currentUser, int jours) {
        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);
        if (accessibleCompanyIds.isEmpty()) {
            return List.of();
        }

        // Récupérer la configuration SPÉCIFIQUE pour fin de contrat
        AlerteConfiguration config = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.FIN_CONTRAT,
                currentUser
        );

        if (!config.isEnabled()) {
            log.info("Alertes fin de contrat désactivées pour l'utilisateur {}", currentUser.getUsername());
            return List.of();
        }

        List<ContratEmploye> contrats = contratRepository.findByCompanyIdsAndActifs(accessibleCompanyIds);
        LocalDate aujourdhui = LocalDate.now();
        List<Integer> echeancesConfig = config.getEcheancesJoursAsList();

        log.info("🔍 Recherche fins de contrat - Échéances: {}, Contrats: {}", echeancesConfig, contrats.size());

        return contrats.stream()
                .filter(c -> c.getDate_fin() != null && !c.isArretContrat())
                .filter(c -> isContratImportantPourUtilisateur(c, config))
                .map(c -> {
                    long joursRestants = Period.between(aujourdhui, c.getDate_fin()).getDays();
                    return new Object[]{c, joursRestants};
                })
                .filter(obj -> {
                    long joursRestants = (long) obj[1];
                    // Vérifier si le nombre de jours restants correspond aux échéances configurées
                    boolean correspondEcheance = echeancesConfig.contains((int) joursRestants);
                    log.debug("Fin contrat - Jours restants: {}, Correspond: {}", joursRestants, correspondEcheance);
                    return correspondEcheance && joursRestants >= 0;
                })
                .peek(obj -> {
                    ContratEmploye c = (ContratEmploye) ((Object[]) obj)[0];
                    long joursRestants = (long) obj[1];
                    log.info("✅ Fin contrat trouvée - Contrat: {}, Employé: {}, Poste: {}, Jours: {}, Date fin: {}",
                            c.getId(), c.getEmploye().getNom(),
                            c.getPoste() != null ? c.getPoste().getLibelle() : "N/A",
                            joursRestants, c.getDate_fin());
                })
                .sorted(Comparator.comparing(obj -> (long) ((Object[]) obj)[1]))
                .map(obj -> {
                    ContratEmploye c = (ContratEmploye) ((Object[]) obj)[0];
                    long joursRestants = (long) obj[1];
                    return toDTO(c, "FIN_CONTRAT", joursRestants);
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère les anniversaires de recrutement prochains selon la configuration utilisateur
     */
    public List<ContratAlerteDTO> getAnniversairesRecrutementProchains(User currentUser, int jours) {
        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);
        if (accessibleCompanyIds.isEmpty()) {
            return List.of();
        }

        // Récupérer la configuration SPÉCIFIQUE pour anniversaire recrutement
        AlerteConfiguration config = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.ANNIVERSAIRE_RECRUTEMENT,
                currentUser
        );

        if (!config.isEnabled()) {
            log.info("Alertes anniversaire recrutement désactivées pour l'utilisateur {}", currentUser.getUsername());
            return List.of();
        }

        List<ContratEmploye> contrats = contratRepository.findByCompanyIdsAndActifs(accessibleCompanyIds);
        LocalDate aujourdhui = LocalDate.now();
        List<Integer> echeancesConfig = config.getEcheancesJoursAsList();

        log.info("🔍 Recherche anniversaires recrutement - Échéances: {}, Contrats: {}", echeancesConfig, contrats.size());

        return contrats.stream()
                .filter(c -> c.getDate_debut() != null && !c.isArretContrat())
                .filter(c -> isContratImportantPourUtilisateur(c, config))
                .map(c -> {
                    LocalDate dateDebut = c.getDate_debut();
                    LocalDate anniversaireCetteAnnee = dateDebut.withYear(aujourdhui.getYear());

                    if (anniversaireCetteAnnee.isBefore(aujourdhui) || anniversaireCetteAnnee.isEqual(aujourdhui)) {
                        anniversaireCetteAnnee = anniversaireCetteAnnee.plusYears(1);
                    }

                    long joursRestants = Period.between(aujourdhui, anniversaireCetteAnnee).getDays();
                    return new Object[]{c, joursRestants, anniversaireCetteAnnee};
                })
                .filter(obj -> {
                    long joursRestants = (long) obj[1];
                    // Vérifier si le nombre de jours restants correspond aux échéances configurées
                    boolean correspondEcheance = echeancesConfig.contains((int) joursRestants);
                    log.debug("Anniv recrutement - Jours restants: {}, Correspond: {}", joursRestants, correspondEcheance);
                    return correspondEcheance && joursRestants >= 0;
                })
                .peek(obj -> {
                    ContratEmploye c = (ContratEmploye) ((Object[]) obj)[0];
                    long joursRestants = (long) obj[1];
                    LocalDate dateAnniversaire = (LocalDate) obj[2];
                    log.info("✅ Anniversaire recrutement trouvé - Contrat: {}, Employé: {}, Poste: {}, Jours: {}, Date anniv: {}",
                            c.getId(), c.getEmploye().getNom(),
                            c.getPoste() != null ? c.getPoste().getLibelle() : "N/A",
                            joursRestants, dateAnniversaire);
                })
                .sorted(Comparator.comparing(obj -> (long) ((Object[]) obj)[1]))
                .map(obj -> {
                    ContratEmploye c = (ContratEmploye) ((Object[]) obj)[0];
                    long joursRestants = (long) obj[1];
                    LocalDate dateAnniversaire = (LocalDate) obj[2];
                    return toAnniversaireRecrutementDTO(c, joursRestants, dateAnniversaire);
                })
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si un contrat est important pour l'utilisateur selon sa configuration
     */
    private boolean isContratImportantPourUtilisateur(ContratEmploye contrat, AlerteConfiguration config) {
        // Si aucun filtre configuré, tous les contrats sont importants
        List<String> postesImportants = config.getMotifsImportantsAsList();
        if (postesImportants.isEmpty()) {
            return true;
        }

        // Vérifier si le poste du contrat est dans la liste des postes importants
        String posteContrat = contrat.getPoste() != null ?
                contrat.getPoste().getLibelle().toUpperCase() : "";

        boolean estImportant = postesImportants.stream()
                .map(String::toUpperCase)
                .anyMatch(posteImportant -> posteContrat.contains(posteImportant));

        log.debug("Contrat {} - Poste: {}, Configuration: {}, Important: {}",
                contrat.getId(), posteContrat, postesImportants, estImportant);

        return estImportant;
    }

    private ContratAlerteDTO toAnniversaireRecrutementDTO(ContratEmploye contrat, long joursRestants, LocalDate dateAnniversaire) {
        // Calculer le nombre d'années de service
        int anneesService = Period.between(contrat.getDate_debut(), LocalDate.now()).getYears();
        // Pour l'anniversaire à venir, on ajoute 1 année
        int anneesTotal = anneesService + 1;

        return ContratAlerteDTO.builder()
                .contratId(contrat.getId())
                .numeroContrat(contrat.getNumeroContrat())
                .employeId(contrat.getEmploye().getId())
                .employeNom(contrat.getEmploye().getNom())
                .employePrenom(contrat.getEmploye().getPrenom())
                .poste(contrat.getPoste() != null ? contrat.getPoste().getLibelle() : "Non spécifié")
                .departement(contrat.getDepartement() != null ? contrat.getDepartement().getLibelle() : "Non spécifié")
                .companyName(contrat.getCompany().getName())
                .dateDebut(contrat.getDate_debut())
                .dateFin(contrat.getDate_fin())
                .debutEssai(contrat.getDebut_essai())
                .finEssai(contrat.getFin_essai())
                .typeAlerte("ANNIVERSAIRE_RECRUTEMENT")
                .joursRestants(joursRestants)
                .dateEcheance(dateAnniversaire)
                .statutContrat(contrat.getStatus_contrat())
                .build();
    }

    /**
     * Récupère toutes les alertes de contrat selon la configuration utilisateur
     */
    public List<ContratAlerteDTO> getToutesAlertesContrat(User currentUser, int jours) {
        List<ContratAlerteDTO> finsEssai = getFinsEssaiProchaines(currentUser, jours);
        List<ContratAlerteDTO> finsContrat = getFinsContratProchaines(currentUser, jours);
        List<ContratAlerteDTO> anniversaires = getAnniversairesRecrutementProchains(currentUser, jours);

        finsEssai.addAll(finsContrat);
        finsEssai.addAll(anniversaires);

        return finsEssai.stream()
                .sorted(Comparator.comparing(ContratAlerteDTO::getDateEcheance))
                .collect(Collectors.toList());
    }

    /**
     * Récupère les statistiques des alertes contrat
     */
    public ContratStatistiquesDTO getStatistiques(User currentUser, int jours) {
        List<ContratAlerteDTO> finsEssai = getFinsEssaiProchaines(currentUser, jours);
        List<ContratAlerteDTO> finsContrat = getFinsContratProchaines(currentUser, jours);
        List<ContratAlerteDTO> anniversaires = getAnniversairesRecrutementProchains(currentUser, jours);

        return ContratStatistiquesDTO.builder()
                .finsEssaiProchaines(finsEssai.size())
                .finsContratProchaines(finsContrat.size())
                .anniversairesRecrutement(anniversaires.size())
                .finsEssaiAujourdhui(finsEssai.stream().filter(a -> a.getJoursRestants() == 0).count())
                .finsContratAujourdhui(finsContrat.stream().filter(a -> a.getJoursRestants() == 0).count())
                .anniversairesAujourdhui(anniversaires.stream().filter(a -> a.getJoursRestants() == 0).count())
                .build();
    }

    private ContratAlerteDTO toDTO(ContratEmploye contrat, String typeAlerte, long joursRestants) {
        LocalDate dateEcheance = null;
        int anneesService = 0;

        switch (typeAlerte) {
            case "FIN_ESSAI":
                dateEcheance = contrat.getFin_essai();
                break;
            case "FIN_CONTRAT":
                dateEcheance = contrat.getDate_fin();
                break;
            case "ANNIVERSAIRE_RECRUTEMENT":
                dateEcheance = contrat.getDate_debut().withYear(LocalDate.now().getYear());
                if (dateEcheance.isBefore(LocalDate.now())) {
                    dateEcheance = dateEcheance.plusYears(1);
                }
                anneesService = Period.between(contrat.getDate_debut(), LocalDate.now()).getYears() + 1;
                break;
        }

        return ContratAlerteDTO.builder()
                .contratId(contrat.getId())
                .numeroContrat(contrat.getNumeroContrat())
                .employeId(contrat.getEmploye().getId())
                .employeNom(contrat.getEmploye().getNom())
                .employePrenom(contrat.getEmploye().getPrenom())
                .poste(contrat.getPoste() != null ? contrat.getPoste().getLibelle() : "Non spécifié")
                .departement(contrat.getDepartement() != null ? contrat.getDepartement().getLibelle() : "Non spécifié")
                .companyName(contrat.getCompany().getName())
                .dateDebut(contrat.getDate_debut())
                .dateFin(contrat.getDate_fin())
                .debutEssai(contrat.getDebut_essai())
                .finEssai(contrat.getFin_essai())
                .typeAlerte(typeAlerte)
                .joursRestants(joursRestants)
                .dateEcheance(dateEcheance)
                .statutContrat(contrat.getStatus_contrat())
                .build();
    }

    // DTO pour les statistiques
    @Data
    @Builder
    public static class ContratStatistiquesDTO {
        private int finsEssaiProchaines;
        private int finsContratProchaines;
        private int anniversairesRecrutement;
        private long finsEssaiAujourdhui;
        private long finsContratAujourdhui;
        private long anniversairesAujourdhui;

        public int getFinsEssaiProchaines() {
            return finsEssaiProchaines;
        }

        public void setFinsEssaiProchaines(int finsEssaiProchaines) {
            this.finsEssaiProchaines = finsEssaiProchaines;
        }

        public int getFinsContratProchaines() {
            return finsContratProchaines;
        }

        public void setFinsContratProchaines(int finsContratProchaines) {
            this.finsContratProchaines = finsContratProchaines;
        }

        public int getAnniversairesRecrutement() {
            return anniversairesRecrutement;
        }

        public void setAnniversairesRecrutement(int anniversairesRecrutement) {
            this.anniversairesRecrutement = anniversairesRecrutement;
        }

        public long getFinsEssaiAujourdhui() {
            return finsEssaiAujourdhui;
        }

        public void setFinsEssaiAujourdhui(long finsEssaiAujourdhui) {
            this.finsEssaiAujourdhui = finsEssaiAujourdhui;
        }

        public long getFinsContratAujourdhui() {
            return finsContratAujourdhui;
        }

        public void setFinsContratAujourdhui(long finsContratAujourdhui) {
            this.finsContratAujourdhui = finsContratAujourdhui;
        }

        public long getAnniversairesAujourdhui() {
            return anniversairesAujourdhui;
        }

        public void setAnniversairesAujourdhui(long anniversairesAujourdhui) {
            this.anniversairesAujourdhui = anniversairesAujourdhui;
        }
    }
}