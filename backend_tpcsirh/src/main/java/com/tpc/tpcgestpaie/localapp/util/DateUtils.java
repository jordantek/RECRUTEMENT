package com.tpc.tpcgestpaie.localapp.util;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    /**
     * Retourne le mois précédent d'une date au format "yyyy-MM"
     * @param mois Format "yyyy-MM" (ex: "2025-06")
     * @return Le mois précédent au même format (ex: "2025-06"), ou null si invalide
     */
    public static String getMoisPrecedent(String mois) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth current = YearMonth.parse(mois, formatter);
            YearMonth previous = current.minusMonths(1);
            return previous.format(formatter);
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul du mois précédent: " + e.getMessage());
            return null;
        }
    }
}
