package com.tpc.tpcgestpaie.localapp.config;

import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ConditionAcceptationCongeConverter implements AttributeConverter<GlobalEnums.ConditionAcceptationConge, String> {

    @Override
    public String convertToDatabaseColumn(GlobalEnums.ConditionAcceptationConge attribute) {
        // ✅ null reste null en base
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public GlobalEnums.ConditionAcceptationConge convertToEntityAttribute(String dbData) {
        // ✅ Vérifier null AVANT trim()
        if (dbData == null) {
            return null;
        }

        String trimmed = dbData.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        try {
            return GlobalEnums.ConditionAcceptationConge.valueOf(trimmed.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}