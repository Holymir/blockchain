package com.softuni.blockchain.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


public class Utils {

    public static String serialize(Object object) {
        try {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
            builder.featuresToEnable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
            builder.failOnUnknownProperties(false);

            return builder.build().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
