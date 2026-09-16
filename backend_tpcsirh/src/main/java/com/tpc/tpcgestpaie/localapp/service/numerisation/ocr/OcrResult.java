package com.tpc.tpcgestpaie.localapp.service.numerisation.ocr;

import lombok.Data;

@Data
public class OcrResult {

    private String extractedText;
    private double confidenceScore;
    private OcrSource source;
    private boolean fallbackUsed;

    public enum OcrSource {
        LOCAL,
        CLOUD
    }
}
