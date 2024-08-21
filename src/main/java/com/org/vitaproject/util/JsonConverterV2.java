package com.org.vitaproject.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.io.IOException;
import java.util.Map;

public class JsonConverterV2 implements AttributeConverter<Map<String, Map<String, Object>>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(Map<String, Map<String, Object>> attribute) {
        if (attribute == null) {
            return null;
        }

        try {

            return objectMapper.writeValueAsString(attribute);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting Map to JSON", e);
        }
    }

    @Override
    public Map<String, Map<String, Object>> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<String, Map<String, Object>>>() {
            });
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to Map", e);
        }
    }
}
