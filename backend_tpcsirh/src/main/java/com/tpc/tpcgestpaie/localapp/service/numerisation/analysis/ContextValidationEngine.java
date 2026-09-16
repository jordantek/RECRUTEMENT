package com.tpc.tpcgestpaie.localapp.service.numerisation.analysis;

public class ContextValidationEngine {

    public static boolean validate(String requiredPatterns,
                                   PatternExtractionResult patterns,
                                   DocumentAnalysisResult result) {

        if (requiredPatterns == null || requiredPatterns.isBlank()) {
            return true;
        }

        boolean valid = true;

        for (String rule : requiredPatterns.split(",")) {

            switch (rule.trim().toUpperCase()) {
                case "DATE":
                    if (!patterns.isHasDate()) valid = false;
                    else result.getDetectedPatterns().add("DATE");
                    break;

                case "MONTANT":
                    if (!patterns.isHasAmount()) valid = false;
                    else result.getDetectedPatterns().add("MONTANT");
                    break;

                case "MATRICULE":
                    if (!patterns.isHasMatricule()) valid = false;
                    else result.getDetectedPatterns().add("MATRICULE");
                    break;

                case "POURCENTAGE":
                    if (!patterns.isHasPercentage()) valid = false;
                    else result.getDetectedPatterns().add("POURCENTAGE");
                    break;
            }
        }

        return valid;
    }
}
