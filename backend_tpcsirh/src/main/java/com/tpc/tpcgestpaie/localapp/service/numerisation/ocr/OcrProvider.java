package com.tpc.tpcgestpaie.localapp.service.numerisation.ocr;

import java.io.File;

public interface OcrProvider {
    OcrResult process(File file);
}
