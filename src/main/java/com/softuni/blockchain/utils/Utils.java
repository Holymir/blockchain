package com.softuni.blockchain.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;


public class Utils {

    private static ObjectMapper objectMapper;

    static {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.featuresToEnable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        builder.failOnUnknownProperties(false);

        objectMapper = builder.build();
    }

    public static String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
        }

        throw new RuntimeException(String.format("Unable to Serialize %s object", object));
    }

    public static <T> T deserialize(Class<T> type, String serializedObject) {
        try {
            return objectMapper.readValue(serializedObject, type);
        } catch (IOException e) {
        }

        throw new RuntimeException(String.format("Unable to Deserialize %s from string.", type));
    }
}
