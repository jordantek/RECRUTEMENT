package com.tpc.tpcgestpaie.localapp.service.numerisation.ocr;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class TesseractOcrService implements OcrProvider {

    @Override
    public OcrResult process(File file) {

        try {

            Tesseract tesseract = new Tesseract();

            // ⭐ CHEMIN TESSDATA depuis resources
            File tessDataFolder =
                    new ClassPathResource("tessdata").getFile();

            tesseract.setDatapath(tessDataFolder.getAbsolutePath());
            tesseract.setLanguage("fra");

            log.info("TESSDATA PATH = {}", tessDataFolder.getAbsolutePath());

            String text = tesseract.doOCR(file);

            OcrResult result = new OcrResult();
            result.setExtractedText(text);
            result.setConfidenceScore(estimateConfidence(text));
            result.setSource(OcrResult.OcrSource.LOCAL);
            result.setFallbackUsed(false);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Erreur OCR local", e);
        }
    }

    private double estimateConfidence(String text) {
        if (text == null || text.trim().length() < 100) return 0.3;
        if (text.length() > 2000) return 0.9;
        return 0.7;
    }
}
