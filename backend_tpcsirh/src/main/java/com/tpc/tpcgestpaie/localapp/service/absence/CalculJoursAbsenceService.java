package com.tpc.tpcgestpaie.localapp.service.absence;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.absence.DemandeAbsence;
import com.tpc.tpcgestpaie.localapp.service.jourFerie.JourFerieEntrepriseService;
import com.tpc.tpcgestpaie.localapp.service.jourtravail.JoursTravaillesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculJoursAbsenceService {

    private final JoursTravaillesService joursTravaillesService;
    private final JourFerieEntrepriseService jourFerieEntrepriseService;

    /**
     * Calcule le nombre de jours d'absence réels pour une demande
     * en tenant compte des jours travaillés et des jours fériés
     */
    public CalculJoursAbsenceResultat calculerJoursAbsenceReels(
            Long companyId,
            LocalDate dateDebut,
            LocalDate dateFin) {

        log.info("Calcul des jours d'absence réels pour l'entreprise {} du {} au {}",
                companyId, dateDebut, dateFin);

        // 1. Calculer les jours travaillés bruts (selon config entreprise)
        int joursTravailles = joursTravaillesService.calculerJoursCongePris(
                companyId, dateDebut, dateFin);

        // 2. Récupérer les jours fériés dans la période
        List<LocalDate> joursFeries = getJoursFeriesDansPeriode(companyId, dateDebut, dateFin);

        // 3. Filtrer les jours fériés qui tombent sur des jours travaillés
        List<LocalDate> joursFeriesTravailles = joursFeries.stream()
                .filter(date -> estJourTravaille(companyId, date))
                .toList();

        // 4. Calculer le nombre réel de jours d'absence
        int joursAbsenceReels = joursTravailles - joursFeriesTravailles.size();

        // 5. Calculer les jours calendaires pour info
        long joursCalendaires = ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;

        return CalculJoursAbsenceResultat.builder()
                .joursCalendaires((int) joursCalendaires)
                .joursTravailles(joursTravailles)
                .joursFeriesDansPeriode(joursFeries.size())
                .joursFeriesTravailles(joursFeriesTravailles.size())
                .joursAbsenceReels(joursAbsenceReels)
                .datesJoursFeries(joursFeriesTravailles)
                .build();
    }

    /**
     * Calcule les jours d'absence pour une demande existante
     */
    public CalculJoursAbsenceResultat calculerJoursAbsenceReels(DemandeAbsence demande) {
        Employe employe = demande.getEmployeDemandeur();
        if (employe == null || employe.getCompany() == null) {
            throw new RuntimeException("Impossible de calculer : employé ou entreprise non défini");
        }

        return calculerJoursAbsenceReels(
                employe.getCompany().getId(),
                demande.getDateDebut(),
                demande.getDateFin()
        );
    }

    /**
     * Recalcule et met à jour le nombre de jours d'une demande
     */
    public int mettreAJourJoursAbsence(DemandeAbsence demande) {
        CalculJoursAbsenceResultat resultat = calculerJoursAbsenceReels(demande);

        // Mettre à jour la demande
        demande.setNombreJours(resultat.getJoursAbsenceReels());

        log.info("Mise à jour de la demande {} : {} jours d'absence réels (était {} jours calendaires)",
                demande.getId(),
                resultat.getJoursAbsenceReels(),
                resultat.getJoursCalendaires());

        return resultat.getJoursAbsenceReels();
    }

    // ========================================
    // 🔧 MÉTHODES UTILITAIRES
    // ========================================

    /**
     * Récupère tous les jours fériés effectifs dans une période
     */
    private List<LocalDate> getJoursFeriesDansPeriode(
            Long companyId,
            LocalDate dateDebut,
            LocalDate dateFin) {

        // Récupérer les années couvertes par la période
        int anneeDebut = dateDebut.getYear();
        int anneeFin = dateFin.getYear();

        return Stream.iterate(anneeDebut, year -> year + 1)
                .limit(anneeFin - anneeDebut + 1L)
                .flatMap(annee -> jourFerieEntrepriseService
                        .getJoursFeriesEffectifs(companyId, annee)
                        .stream()
                        .map(jf -> jf.getDateFerie())
                        .filter(date -> !date.isBefore(dateDebut) && !date.isAfter(dateFin)))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si une date est un jour travaillé pour l'entreprise
     */
    private boolean estJourTravaille(Long companyId, LocalDate date) {
        String nomJour = getNomJour(date.getDayOfWeek().getValue());
        return joursTravaillesService.estJourTravaille(companyId, nomJour);
    }

    private String getNomJour(int dayOfWeek) {
        return switch (dayOfWeek) {
            case 1 -> "lundi";
            case 2 -> "mardi";
            case 3 -> "mercredi";
            case 4 -> "jeudi";
            case 5 -> "vendredi";
            case 6 -> "samedi";
            case 7 -> "dimanche";
            default -> throw new IllegalArgumentException("Jour invalide: " + dayOfWeek);
        };
    }

    /**
     * DTO pour le résultat du calcul
     */
    @lombok.Data
    @lombok.Builder
    public static class CalculJoursAbsenceResultat {
        private int joursCalendaires;           // Total jours calendaires
        private int joursTravailles;            // Jours travaillés selon config
        private int joursFeriesDansPeriode;     // Total jours fériés dans la période
        private int joursFeriesTravailles;      // Jours fériés tombant sur des jours travaillés
        private int joursAbsenceReels;          // Jours d'absence réels (travaillés - fériés travaillés)
        private List<LocalDate> datesJoursFeries; // Liste des dates des jours fériés concernés
    }

    /**
     * Calcule les jours d'absence réels entre deux dates effectives
     * (utilisé lors de la confirmation de retour)
     */
    public CalculJoursAbsenceResultat calculerJoursAbsenceEffectifs(
            Long companyId,
            LocalDate dateDepartEffective,
            LocalDate dateRetourEffective) {

        // Même logique que calculerJoursAbsenceReels mais avec dates effectives
        return calculerJoursAbsenceReels(companyId, dateDepartEffective, dateRetourEffective);
    }
}