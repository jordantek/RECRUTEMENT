package com.tpc.tpcgestpaie.localapp.util;

import java.text.Normalizer;

public class SlugUtils {

    /**
     * Génère un slug à partir d’un texte
     *
     * @param text Texte source
     * @param upperCase true = MAJUSCULE, false = minuscule
     * @param separator séparateur ex: "-", "_", "."
     * @return slug formaté
     */
    public static String generateSlug(String text, boolean upperCase, String separator) {

        if (text == null || text.trim().isEmpty()) return "";

        if (separator == null || separator.isBlank()) {
            separator = "-";
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        String slug = normalized
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", separator)
                .replaceAll(separator + "+", separator); // évite doubles séparateurs

        return upperCase ? slug.toUpperCase() : slug.toLowerCase();
    }
}
