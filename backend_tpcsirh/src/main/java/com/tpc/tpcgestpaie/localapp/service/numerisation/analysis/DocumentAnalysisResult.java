package com.tpc.tpcgestpaie.localapp.service.numerisation.analysis;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class DocumentAnalysisResult {

    private Long categoryId;
    private String categoryCode;
    private String categoryLabel;

    private double score;
    private boolean accepted;
    private boolean requiresValidation;

    private Set<String> matchedKeywords = new HashSet<>();
    private Set<String> detectedPatterns = new HashSet<>();
}
