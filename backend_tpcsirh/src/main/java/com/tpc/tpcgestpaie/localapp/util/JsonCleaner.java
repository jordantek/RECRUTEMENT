package com.tpc.tpcgestpaie.localapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Iterator;
import java.util.Map;

public class JsonCleaner {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // ➤ support des types Java 8 (LocalDate, etc.)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false) // ➤ format lisible pour les dates
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    public static ObjectNode removeNullFields(Object object) {
        ObjectNode node = mapper.convertValue(object, ObjectNode.class);
        Iterator<Map.Entry<String, com.fasterxml.jackson.databind.JsonNode>> fields = node.fields();

        while (fields.hasNext()) {
            Map.Entry<String, com.fasterxml.jackson.databind.JsonNode> entry = fields.next();
            if (entry.getValue() instanceof NullNode) {
                fields.remove();
            }
        }

        return node;
    }
}
