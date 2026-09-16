package com.tpc.tpcgestpaie.localapp.service.numerisation.analysis;

import java.util.HashSet;
import java.util.Set;

public class KeywordScoringEngine {

    public static ScoringResult score(String normalizedText, String keywords) {

        ScoringResult result = new ScoringResult();

        if (keywords == null || keywords.isBlank()) {
            return result;
        }

        String[] words = keywords.split(",");

        for (String word : words) {
            String keyword = word.trim();
            if (normalizedText.contains(keyword)) {
                result.score += 1.0;
                result.matchedKeywords.add(keyword);
            }
        }

        return result;
    }

    public static class ScoringResult {
        double score = 0;
        Set<String> matchedKeywords = new HashSet<>();
    }
}
