package com.tpc.tpcgestpaie.localapp.service.jourFerie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Calculateur pour les jours fériés mobiles
 * Calcule les dates de Pâques, Pentecôte, Ascension et fêtes musulmanes
 */
@Component
@Slf4j
public class JourFerieMobileCalculator {

    /**
     * Calcule la date du dimanche de Pâques pour une année donnée
     * Utilise l'algorithme de Meeus/Jones/Butcher
     *
     * @param annee L'année
     * @return La date du dimanche de Pâques
     */
    public LocalDate calculerDimanchePaques(int annee) {
        int a = annee % 19;
        int b = annee / 100;
        int c = annee % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int mois = (h + l - 7 * m + 114) / 31;
        int jour = ((h + l - 7 * m + 114) % 31) + 1;

        LocalDate dimanchePaques = LocalDate.of(annee, mois, jour);
        log.debug("Dimanche de Pâques {} : {}", annee, dimanchePaques);
        return dimanchePaques;
    }

    /**
     * Calcule la date du lundi de Pâques
     *
     * @param annee L'année
     * @return La date du lundi de Pâques
     */
    public LocalDate calculerLundiPaques(int annee) {
        return calculerDimanchePaques(annee).plusDays(1);
    }

    /**
     * Calcule la date de l'Ascension
     * (39 jours après le dimanche de Pâques, toujours un jeudi)
     *
     * @param annee L'année
     * @return La date de l'Ascension
     */
    public LocalDate calculerAscension(int annee) {
        return calculerDimanchePaques(annee).plusDays(39);
    }

    /**
     * Calcule la date du dimanche de Pentecôte
     * (49 jours après le dimanche de Pâques)
     *
     * @param annee L'année
     * @return La date du dimanche de Pentecôte
     */
    public LocalDate calculerDimanchePentecote(int annee) {
        return calculerDimanchePaques(annee).plusDays(49);
    }

    /**
     * Calcule la date du lundi de Pentecôte
     * (50 jours après le dimanche de Pâques)
     *
     * @param annee L'année
     * @return La date du lundi de Pentecôte
     */
    public LocalDate calculerLundiPentecote(int annee) {
        return calculerDimanchePaques(annee).plusDays(50);
    }

    /**
     * Calcule la date approximative de la Korité (Aïd el-Fitr)
     * Note: Cette estimation est approximative car basée sur le calendrier lunaire
     * Pour une précision absolue, utiliser une API spécialisée
     *
     * @param annee L'année
     * @return La date approximative de la Korité
     */
    public LocalDate calculerKorite(int annee) {
        // Dates approximatives basées sur le calendrier islamique
        // Le calendrier lunaire recule d'environ 11 jours par an

        // Dates de référence pour quelques années
        return switch (annee) {
            case 2024 -> LocalDate.of(2024, 4, 10);
            case 2025 -> LocalDate.of(2025, 3, 30);
            case 2026 -> LocalDate.of(2026, 3, 20);
            case 2027 -> LocalDate.of(2027, 3, 9);
            case 2028 -> LocalDate.of(2028, 2, 26);
            default -> {
                // Estimation pour les autres années (à partir de 2024)
                int joursDepuis2024 = (annee - 2024) * 355; // Année lunaire ≈ 355 jours
                LocalDate reference = LocalDate.of(2024, 4, 10);
                yield reference.plusDays(joursDepuis2024);
            }
        };
    }

    /**
     * Calcule la date approximative de la Tabaski (Aïd el-Kébir)
     * Note: Cette estimation est approximative car basée sur le calendrier lunaire
     *
     * @param annee L'année
     * @return La date approximative de la Tabaski
     */
    public LocalDate calculerTabaski(int annee) {
        // La Tabaski est environ 70 jours après la Korité
        return switch (annee) {
            case 2024 -> LocalDate.of(2024, 6, 16);
            case 2025 -> LocalDate.of(2025, 6, 6);
            case 2026 -> LocalDate.of(2026, 5, 27);
            case 2027 -> LocalDate.of(2027, 5, 16);
            case 2028 -> LocalDate.of(2028, 5, 4);
            default -> calculerKorite(annee).plusDays(70);
        };
    }

    /**
     * Calcule la date approximative du Maouloud (Anniversaire du Prophète)
     * Note: Cette estimation est approximative car basée sur le calendrier lunaire
     *
     * @param annee L'année
     * @return La date approximative du Maouloud
     */
    public LocalDate calculerMaouloud(int annee) {
        return switch (annee) {
            case 2024 -> LocalDate.of(2024, 9, 15);
            case 2025 -> LocalDate.of(2025, 9, 4);
            case 2026 -> LocalDate.of(2026, 8, 25);
            case 2027 -> LocalDate.of(2027, 8, 14);
            case 2028 -> LocalDate.of(2028, 8, 2);
            default -> {
                // Estimation pour les autres années
                int joursDepuis2024 = (annee - 2024) * 355;
                LocalDate reference = LocalDate.of(2024, 9, 15);
                yield reference.plusDays(joursDepuis2024);
            }
        };
    }

    /**
     * Reporte un jour férié au lundi suivant s'il tombe un weekend
     *
     * @param date La date originale
     * @param entrepriseTravailleWeekend Si l'entreprise travaille le weekend
     * @return La date effective (reportée si nécessaire)
     */
    public LocalDate reporterSiWeekend(LocalDate date, boolean entrepriseTravailleWeekend) {
        if (!entrepriseTravailleWeekend) {
            // L'entreprise ne travaille pas le weekend, pas de report nécessaire
            return date;
        }

        DayOfWeek jourSemaine = date.getDayOfWeek();

        return switch (jourSemaine) {
            case SATURDAY -> {
                log.debug("Jour férié tombant un samedi, report au lundi : {} -> {}",
                        date, date.plusDays(2));
                yield date.plusDays(2); // Samedi -> Lundi
            }
            case SUNDAY -> {
                log.debug("Jour férié tombant un dimanche, report au lundi : {} -> {}",
                        date, date.plusDays(1));
                yield date.plusDays(1); // Dimanche -> Lundi
            }
            default -> date; // Jour de semaine, pas de report
        };
    }

    /**
     * Génère le motif de report
     *
     * @param dateOriginale La date originale
     * @param dateEffective La date après report
     * @return Le motif du report
     */
    public String getMotifReport(LocalDate dateOriginale, LocalDate dateEffective) {
        if (dateOriginale.equals(dateEffective)) {
            return null;
        }

        DayOfWeek jourOriginal = dateOriginale.getDayOfWeek();
        String jourFr = jourOriginal == DayOfWeek.SATURDAY ? "samedi" : "dimanche";

        return String.format("Tombait un %s (%s), reporté au lundi %s",
                jourFr,
                dateOriginale.toString(),
                dateEffective.toString()
        );
    }

    /**
     * Vérifie si une date tombe un weekend
     *
     * @param date La date à vérifier
     * @return true si c'est un weekend
     */
    public boolean estWeekend(LocalDate date) {
        DayOfWeek jour = date.getDayOfWeek();
        return jour == DayOfWeek.SATURDAY || jour == DayOfWeek.SUNDAY;
    }
}