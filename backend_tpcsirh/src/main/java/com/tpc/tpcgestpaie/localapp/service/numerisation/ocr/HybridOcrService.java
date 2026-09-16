package com.tpc.tpcgestpaie.localapp.service.numerisation.ocr;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class HybridOcrService {

    private final TesseractOcrService localOcr;
    private final OcrSpaceService cloudOcr;

    private static final double CONFIDENCE_THRESHOLD = 0.65;

    public HybridOcrService(TesseractOcrService localOcr,
                            OcrSpaceService cloudOcr) {
        this.localOcr = localOcr;
        this.cloudOcr = cloudOcr;
    }

    public OcrResult process(File file) {

        OcrResult localResult = localOcr.process(file);

        if (localResult.getConfidenceScore() >= CONFIDENCE_THRESHOLD) {
            return localResult;
        }

        OcrResult cloudResult = cloudOcr.process(file);
        cloudResult.setFallbackUsed(true);

        return cloudResult;
    }
}
