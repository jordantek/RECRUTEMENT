package com.tpc.tpcgestpaie.localapp.service.numerisation.analysis;

import java.text.Normalizer;

public class TextNormalizer {

    public static String normalize(String text) {
        if (text == null) return "";

        text = text.toLowerCase();
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = text.replaceAll("\\p{M}", ""); // supprime accents
        text = text.replaceAll("[^a-z0-9 ]", " ");
        text = text.replaceAll("\\s+", " ");

        return text.trim();
    }
}
