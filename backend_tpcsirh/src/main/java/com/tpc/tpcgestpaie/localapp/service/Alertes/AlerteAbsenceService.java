package com.tpc.tpcgestpaie.localapp.service.Alertes;

import com.tpc.tpcgestpaie.localapp.dto.alertes.AbsenceAlerteDTO;
import com.tpc.tpcgestpaie.localapp.model.Absence;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.alertes.AlerteConfiguration;
import com.tpc.tpcgestpaie.localapp.repository.administration.AbsenceRepository;
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
public class AlerteAbsenceService {

    private final AbsenceRepository absenceRepository;
    private final AlerteConfigurationService configurationService;
    private final NotificationService notificationService;
    private final UserCompanyAccessService userCompanyAccessService;

    public AlerteAbsenceService(AbsenceRepository absenceRepository,
                                AlerteConfigurationService configurationService,
                                NotificationService notificationService,
                                UserCompanyAccessService userCompanyAccessService) {
        this.absenceRepository = absenceRepository;
        this.configurationService = configurationService;
        this.notificationService = notificationService;
        this.userCompanyAccessService = userCompanyAccessService;
    }

    /**
     * Vérifie toutes les alertes d'absence pour l'utilisateur connecté
     */
    public void verifierAlertesAbsences(User currentUser) {
        if (currentUser == null) {
            return;
        }

        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);
        if (accessibleCompanyIds.isEmpty()) {
            log.info("Aucune entreprise accessible pour l'utilisateur {}", currentUser.getUsername());
            return;
        }

        List<Absence> absences = absenceRepository.findByCompanyIdsAndActives(accessibleCompanyIds);
        LocalDate aujourdhui = LocalDate.now();

        for (Absence absence : absences) {
            verifierAlertesPourAbsence(absence, aujourdhui, currentUser);
        }

        log.info("Vérification des alertes absence terminée pour {} absences", absences.size());
    }

    /**
     * Vérification automatique programmée
     */
    @Scheduled(cron = "0 0 8 * * ?") // Exécution quotidienne à 8h
    public void verifierAlertesAbsencesAutomatique() {
        // Cette méthode utilise la configuration par défaut pour les notifications système
        // Ou elle pourrait notifier tous les utilisateurs ayant ce type d'alerte activé
        log.info("Vérification automatique des alertes absence désactivée - Utilisez la configuration utilisateur");
    }

    private void verifierAlertesPourAbsence(Absence absence, LocalDate dateReference, User currentUser) {
        try {
            // Récupérer les configurations de l'utilisateur
            AlerteConfiguration configDebut = configurationService.getConfigurationForUser(
                    AlerteConfiguration.TypeAlerte.DEBUT_ABSENCE,
                    currentUser
            );

            AlerteConfiguration configFin = configurationService.getConfigurationForUser(
                    AlerteConfiguration.TypeAlerte.FIN_ABSENCE,
                    currentUser
            );

            // Vérifier début d'absence (sortie de l'entreprise)
            if (configDebut.isEnabled()) {
                verifierDebutAbsence(absence, dateReference, currentUser, configDebut);
            }

            // Vérifier fin d'absence (retour dans l'entreprise)
            if (configFin.isEnabled()) {
                verifierFinAbsence(absence, dateReference, currentUser, configFin);
            }

            // Vérifier absences en cours (pour information)
            verifierAbsenceEnCours(absence, dateReference, currentUser, configDebut);

        } catch (Exception e) {
            log.error("Erreur lors de la vérification des alertes pour l'absence {}: {}",
                    absence.getId(), e.getMessage());
        }
    }

    private void verifierDebutAbsence(Absence absence, LocalDate dateReference, User currentUser, AlerteConfiguration config) {
        if (absence.getDateDebut() == null) {
            return;
        }

        // Vérifier si le motif est important pour cet utilisateur
        if (!isMotifImportantForUser(absence, config)) {
            return;
        }

        long joursRestants = Period.between(dateReference, absence.getDateDebut()).getDays();
        List<Integer> echeances = config.getEcheancesJoursAsList();

        for (Integer echeance : echeances) {
            if (joursRestants == echeance) {
                envoyerAlerteDebutAbsence(absence, echeance, currentUser);
                break;
            }
        }
    }

    private void verifierFinAbsence(Absence absence, LocalDate dateReference, User currentUser, AlerteConfiguration config) {
        if (absence.getDateFin() == null) {
            return;
        }

        // Vérifier si le motif est important pour cet utilisateur
        if (!isMotifImportantForUser(absence, config)) {
            return;
        }

        long joursRestants = Period.between(dateReference, absence.getDateFin()).getDays();
        List<Integer> echeances = config.getEcheancesJoursAsList();

        for (Integer echeance : echeances) {
            if (joursRestants == echeance) {
                envoyerAlerteFinAbsence(absence, echeance, currentUser);
                break;
            }
        }
    }

    private void verifierAbsenceEnCours(Absence absence, LocalDate dateReference, User currentUser, AlerteConfiguration config) {
        if (absence.getDateDebut() == null || absence.getDateFin() == null) {
            return;
        }

        // Vérifier si l'absence est en cours aujourd'hui
        if (!dateReference.isBefore(absence.getDateDebut()) && !dateReference.isAfter(absence.getDateFin())) {
            // Envoyer une alerte quotidienne pour les absences en cours (optionnel)
            if (shouldNotifyAbsenceEnCours(absence, config)) {
                envoyerAlerteAbsenceEnCours(absence, currentUser);
            }
        }
    }

    private boolean isMotifImportantForUser(Absence absence, AlerteConfiguration config) {
        if (absence.getMotifAbsence() == null) {
            return false;
        }

        List<String> motifsImportants = config.getMotifsImportantsAsList();
        if (motifsImportants.isEmpty()) {
            return true; // Si aucun motif spécifié, notifier pour tous
        }

        String motifLibelle = absence.getMotifAbsence().getLibelle().toUpperCase();
        return motifsImportants.stream()
                .map(String::toUpperCase)
                .anyMatch(motifImportant -> motifLibelle.contains(motifImportant));
    }

    private boolean shouldNotifyAbsenceEnCours(Absence absence, AlerteConfiguration config) {
        // Notifier seulement pour les motifs importants configurés par l'utilisateur
       return isMotifImportantForUser(absence, config);
//        return  true;
    }

    private void envoyerAlerteDebutAbsence(Absence absence, int joursRestants, User currentUser) {
        String temps = joursRestants == 0 ? "aujourd'hui" : "dans " + joursRestants + " jour(s)";

        String message = String.format(
                "🚪 %s %s (%s) sera absent %s. " +
                        "Motif: %s - Type: %s - Du %s au %s",
                absence.getEmploye().getPrenom(), absence.getEmploye().getNom(),
                getCompanyName(absence),
                temps,
                absence.getMotifAbsence() != null ? absence.getMotifAbsence().getLibelle() : "Non spécifié",
                absence.getTypeAbsence() != null ? absence.getTypeAbsence().getLibelle() : "Non spécifié",
                absence.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                absence.getDateFin() != null ? absence.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non définie"
        );

        log.info("Alerte début absence: {}", message);
        // notificationService.envoyerNotification(message, TypeAlerte.DEBUT_ABSENCE, currentUser);
    }

    private void envoyerAlerteFinAbsence(Absence absence, int joursRestants, User currentUser) {
        String temps = joursRestants == 0 ? "aujourd'hui" : "dans " + joursRestants + " jour(s)";

        String message = String.format(
                "✅ %s %s (%s) reprendra le travail %s. " +
                        "Absence: %s - Poste: %s",
                absence.getEmploye().getPrenom(), absence.getEmploye().getNom(),
                getCompanyName(absence),
                temps,
                absence.getMotifAbsence() != null ? absence.getMotifAbsence().getLibelle() : "Non spécifié",
                absence.getContratEmploye() != null && absence.getContratEmploye().getPoste() != null ?
                        absence.getContratEmploye().getPoste().getLibelle() : "Non spécifié"
        );

        log.info("Alerte fin absence: {}", message);
        // notificationService.envoyerNotification(message, TypeAlerte.FIN_ABSENCE, currentUser);
    }

    private void envoyerAlerteAbsenceEnCours(Absence absence, User currentUser) {
        String message = String.format(
                "📋 Aujourd'hui, %s %s (%s) est absent. " +
                        "Motif: %s - Retour prévu: %s",
                absence.getEmploye().getPrenom(), absence.getEmploye().getNom(),
                getCompanyName(absence),
                absence.getMotifAbsence() != null ? absence.getMotifAbsence().getLibelle() : "Non spécifié",
                absence.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );

        log.info("Alerte absence en cours: {}", message);
        // notificationService.envoyerNotification(message, TypeAlerte.ABSENCE_EN_COURS, currentUser);
    }

    private String getCompanyName(Absence absence) {
        if (absence.getCompany() != null) {
            return absence.getCompany().getName();
        } else if (absence.getContratEmploye() != null && absence.getContratEmploye().getCompany() != null) {
            return absence.getContratEmploye().getCompany().getName();
        }
        return "Entreprise inconnue";
    }

    /**
     * Récupère les départs en absence prochains selon la configuration de l'utilisateur
     */
    public List<AbsenceAlerteDTO> getDepartsProchains(User currentUser, int jours) {
        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);
        if (accessibleCompanyIds.isEmpty()) {
            return List.of();
        }

        // Récupérer la configuration de l'utilisateur pour filtrer par motifs
        AlerteConfiguration config = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.DEBUT_ABSENCE,
                currentUser
        );

        List<Absence> absences = absenceRepository.findByCompanyIdsAndActives(accessibleCompanyIds);
        LocalDate aujourdhui = LocalDate.now();

        return absences.stream()
                .filter(a -> a.getDateDebut() != null)
                .filter(a -> isMotifImportantForUser(a, config)) // Filtrer par configuration utilisateur
                .filter(a -> {
                    long joursRestants = Period.between(aujourdhui, a.getDateDebut()).getDays();
                    return joursRestants <= jours && joursRestants >= 0;
                })
                .sorted(Comparator.comparing(Absence::getDateDebut))
                .map(a -> toDTO(a, "DEBUT_ABSENCE", Period.between(aujourdhui, a.getDateDebut()).getDays()))
                .collect(Collectors.toList());
    }

    /**
     * Récupère les retours d'absence prochains selon la configuration de l'utilisateur
     */
    public List<AbsenceAlerteDTO> getRetoursProchains(User currentUser, int jours) {
        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);
        if (accessibleCompanyIds.isEmpty()) {
            return List.of();
        }

        // Récupérer la configuration de l'utilisateur pour filtrer par motifs
        AlerteConfiguration config = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.FIN_ABSENCE,
                currentUser
        );

        List<Absence> absences = absenceRepository.findByCompanyIdsAndActives(accessibleCompanyIds);
        LocalDate aujourdhui = LocalDate.now();

        return absences.stream()
                .filter(a -> a.getDateFin() != null)
                .filter(a -> isMotifImportantForUser(a, config)) // Filtrer par configuration utilisateur
                .filter(a -> {
                    long joursRestants = Period.between(aujourdhui, a.getDateFin()).getDays();
                    return joursRestants <= jours && joursRestants >= 0;
                })
                .sorted(Comparator.comparing(Absence::getDateFin))
                .map(a -> toDTO(a, "FIN_ABSENCE", Period.between(aujourdhui, a.getDateFin()).getDays()))
                .collect(Collectors.toList());
    }

    /**
     * Récupère les absences en cours selon la configuration de l'utilisateur
     */
    public List<AbsenceAlerteDTO> getAbsencesEnCours(User currentUser) {
        List<Long> accessibleCompanyIds = userCompanyAccessService.getAccessibleCompanyIds(currentUser);
        if (accessibleCompanyIds.isEmpty()) {
            return List.of();
        }

        // Récupérer la configuration de l'utilisateur pour filtrer par motifs
        AlerteConfiguration config = configurationService.getConfigurationForUser(
                AlerteConfiguration.TypeAlerte.DEBUT_ABSENCE,
                currentUser
        );

        List<Absence> absences = absenceRepository.findByCompanyIdsAndActives(accessibleCompanyIds);
        LocalDate aujourdhui = LocalDate.now();

        return absences.stream()
                .filter(a -> a.getDateDebut() != null && a.getDateFin() != null)
                .filter(a -> isMotifImportantForUser(a, config)) // Filtrer par configuration utilisateur
                .filter(a -> !aujourdhui.isBefore(a.getDateDebut()) && !aujourdhui.isAfter(a.getDateFin()))
                .sorted(Comparator.comparing(Absence::getDateFin))
                .map(a -> toDTO(a, "ABSENCE_EN_COURS", 0))
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les absences à venir (départs + retours) selon la configuration utilisateur
     */
    public List<AbsenceAlerteDTO> getAbsencesProchaines(User currentUser, int jours) {
        List<AbsenceAlerteDTO> departs = getDepartsProchains(currentUser, jours);
        List<AbsenceAlerteDTO> retours = getRetoursProchains(currentUser, jours);

        departs.addAll(retours);
        return departs.stream()
                .sorted(Comparator.comparing(AbsenceAlerteDTO::getDateEcheance))
                .collect(Collectors.toList());
    }

    /**
     * Récupère les statistiques des absences selon la configuration utilisateur
     */
    public AbsenceStatistiquesDTO getStatistiques(User currentUser, int jours) {
        List<AbsenceAlerteDTO> departs = getDepartsProchains(currentUser, jours);
        List<AbsenceAlerteDTO> retours = getRetoursProchains(currentUser, jours);
        List<AbsenceAlerteDTO> enCours = getAbsencesEnCours(currentUser);

        return AbsenceStatistiquesDTO.builder()
                .departsProchains(departs.size())
                .retoursProchains(retours.size())
                .absencesEnCours(enCours.size())
                .departsAujourdhui(departs.stream().filter(a -> a.getJoursRestants() == 0).count())
                .retoursAujourdhui(retours.stream().filter(a -> a.getJoursRestants() == 0).count())
                .build();
    }

    private AbsenceAlerteDTO toDTO(Absence absence, String typeAlerte, long joursRestants) {
        LocalDate dateEcheance = null;
        switch (typeAlerte) {
            case "DEBUT_ABSENCE":
                dateEcheance = absence.getDateDebut();
                break;
            case "FIN_ABSENCE":
                dateEcheance = absence.getDateFin();
                break;
            case "ABSENCE_EN_COURS":
                dateEcheance = absence.getDateFin(); // Date de retour
                break;
        }

        return AbsenceAlerteDTO.builder()
                .absenceId(absence.getId())
                .employeId(absence.getEmploye().getId())
                .employeNom(absence.getEmploye().getNom())
                .employePrenom(absence.getEmploye().getPrenom())
                .poste(absence.getContratEmploye() != null && absence.getContratEmploye().getPoste() != null ?
                        absence.getContratEmploye().getPoste().getLibelle() : "Non spécifié")
                .departement(absence.getContratEmploye() != null && absence.getContratEmploye().getDepartement() != null ?
                        absence.getContratEmploye().getDepartement().getLibelle() : "Non spécifié")
                .companyName(getCompanyName(absence))
                .motifAbsence(absence.getMotifAbsence() != null ? absence.getMotifAbsence().getLibelle() : "Non spécifié")
                .typeAbsence(absence.getTypeAbsence() != null ? absence.getTypeAbsence().getLibelle() : "Non spécifié")
                .libelle(absence.getLibelle())
                .dateDebut(absence.getDateDebut())
                .dateFin(absence.getDateFin())
                .duree(absence.getDuree())
                .deductibleTempsTravail(absence.isDeductibleTempsTravail())
                .typeAlerte(typeAlerte)
                .joursRestants(joursRestants)
                .dateEcheance(dateEcheance)
                .createdAt(absence.getCreated_at())
                .build();
    }

    // DTO pour les statistiques
    @Data
    @Builder
    public static class AbsenceStatistiquesDTO {
        private int departsProchains;
        private int retoursProchains;
        private int absencesEnCours;
        private long departsAujourdhui;
        private long retoursAujourdhui;

        public int getDepartsProchains() {
            return departsProchains;
        }

        public void setDepartsProchains(int departsProchains) {
            this.departsProchains = departsProchains;
        }

        public int getRetoursProchains() {
            return retoursProchains;
        }

        public void setRetoursProchains(int retoursProchains) {
            this.retoursProchains = retoursProchains;
        }

        public int getAbsencesEnCours() {
            return absencesEnCours;
        }

        public void setAbsencesEnCours(int absencesEnCours) {
            this.absencesEnCours = absencesEnCours;
        }

        public long getDepartsAujourdhui() {
            return departsAujourdhui;
        }

        public void setDepartsAujourdhui(long departsAujourdhui) {
            this.departsAujourdhui = departsAujourdhui;
        }

        public long getRetoursAujourdhui() {
            return retoursAujourdhui;
        }

        public void setRetoursAujourdhui(long retoursAujourdhui) {
            this.retoursAujourdhui = retoursAujourdhui;
        }
    }
}