package com.tpc.tpcgestpaie.localapp.service.numerisation.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternExtractor {

    private static final Pattern DATE_PATTERN =
            Pattern.compile("\\b(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|\\d{1,2}\\s+[a-z]+\\s+\\d{4})\\b");

    private static final Pattern AMOUNT_PATTERN =
            Pattern.compile("\\b\\d{2,3}(?:\\s?\\d{3})*(?:\\s?fcfa|\\s?f\\.?c\\.?f\\.?a?)\\b");

    private static final Pattern MATRICULE_PATTERN =
            Pattern.compile("\\b[a-z]{2,5}-\\d{4}-\\d{3,4}\\b");

    private static final Pattern PERCENTAGE_PATTERN =
            Pattern.compile("\\b\\d{1,3}\\s?%\\b");

    public static PatternExtractionResult extract(String rawText) {

        PatternExtractionResult result = new PatternExtractionResult();

        if (rawText == null) return result;

        String text = TextNormalizer.normalize(rawText);

        Matcher matcher;

        matcher = DATE_PATTERN.matcher(text);
        while (matcher.find()) {
            result.getDetectedDates().add(matcher.group());
        }

        matcher = AMOUNT_PATTERN.matcher(text);
        while (matcher.find()) {
            result.getDetectedAmounts().add(matcher.group());
        }

        matcher = MATRICULE_PATTERN.matcher(text);
        while (matcher.find()) {
            result.getDetectedMatricules().add(matcher.group());
        }

        matcher = PERCENTAGE_PATTERN.matcher(text);
        while (matcher.find()) {
            result.getDetectedPercentages().add(matcher.group());
        }

        result.setHasDate(!result.getDetectedDates().isEmpty());
        result.setHasAmount(!result.getDetectedAmounts().isEmpty());
        result.setHasMatricule(!result.getDetectedMatricules().isEmpty());
        result.setHasPercentage(!result.getDetectedPercentages().isEmpty());

        return result;
    }
}
