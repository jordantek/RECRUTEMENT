package com.tpc.tpcgestpaie.localapp.service.numerisation.analysis;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class PatternExtractionResult {

    private boolean hasDate;
    private boolean hasAmount;
    private boolean hasMatricule;
    private boolean hasPercentage;

    private Set<String> detectedDates = new HashSet<>();
    private Set<String> detectedAmounts = new HashSet<>();
    private Set<String> detectedMatricules = new HashSet<>();
    private Set<String> detectedPercentages = new HashSet<>();
}
