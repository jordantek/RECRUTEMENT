package com.tpc.tpcgestpaie.localapp.service.numerisation.analysis;

import com.tpc.tpcgestpaie.localapp.model.numerisation.parcours.EventCategory;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.parcours.EventCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentAnalysisEngine {

    private final EventCategoryRepository categoryRepository;

    public DocumentAnalysisEngine(EventCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public DocumentAnalysisResult analyze(String rawText) {

        String normalizedText = TextNormalizer.normalize(rawText);
        PatternExtractionResult patterns = PatternExtractor.extract(rawText);

        List<EventCategory> categories = categoryRepository.findAll();

        DocumentAnalysisResult bestResult = null;
        double bestScore = 0;

        for (EventCategory category : categories) {

            KeywordScoringEngine.ScoringResult scoring =
                    KeywordScoringEngine.score(normalizedText, category.getSearchKeywords());

            if (scoring.score == 0) continue;

            DocumentAnalysisResult result = new DocumentAnalysisResult();
            result.setCategoryId(category.getId());
            result.setCategoryCode(category.getCode());
            result.setCategoryLabel(category.getLabel());
            result.setMatchedKeywords(scoring.matchedKeywords);

            double normalizedScore = scoring.score / normalizedText.length();
            result.setScore(normalizedScore);

            // Validation contextuelle simple intelligente
            if (!basicContextValidation(category.getCode(), patterns)) {
                continue;
            }

            if (normalizedScore > bestScore) {
                bestScore = normalizedScore;
                bestResult = result;
            }
        }

        if (bestResult == null) {
            bestResult = new DocumentAnalysisResult();
            bestResult.setAccepted(false);
            bestResult.setRequiresValidation(true);
            return bestResult;
        }

        bestResult.setAccepted(bestScore >= 0.0006);
        bestResult.setRequiresValidation(bestScore < 0.001);

        return bestResult;
    }

    private boolean basicContextValidation(String categoryCode,
                                           PatternExtractionResult patterns) {

        switch (categoryCode) {

            case "SALAIRE":
                return patterns.isHasAmount();

            case "CONTRAT":
                return patterns.isHasDate();

            case "SANCTION":
                return patterns.isHasDate();

            default:
                return true;
        }
    }
}
