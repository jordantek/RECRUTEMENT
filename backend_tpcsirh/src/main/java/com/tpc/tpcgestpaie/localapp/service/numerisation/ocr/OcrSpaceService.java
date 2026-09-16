package com.tpc.tpcgestpaie.localapp.service.numerisation.ocr;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrSpaceService implements OcrProvider {

    @Override
    public OcrResult process(File file) {

        // ⚠️ À implémenter avec RestTemplate ou WebClient
        // https://api.ocr.space/parse/image

        OcrResult result = new OcrResult();
        result.setExtractedText("TEXT_FROM_OCR_SPACE");
        result.setConfidenceScore(0.90);
        result.setSource(OcrResult.OcrSource.CLOUD);
        result.setFallbackUsed(true);

        return result;
    }
}
