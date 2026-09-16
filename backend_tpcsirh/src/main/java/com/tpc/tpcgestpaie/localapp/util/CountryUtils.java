package com.tpc.tpcgestpaie.localapp.util;

import java.util.Locale;

public class CountryUtils {
    public static String getCodeFromName(String countryName, Locale language) {
        for (String code : Locale.getISOCountries()) {
            Locale locale = new Locale(language.getLanguage(), code);
            if (locale.getDisplayCountry(language).equalsIgnoreCase(countryName)) {
                return code; // Retourne "BJ", "FR", etc.
            }
        }
        return null;
    }
}