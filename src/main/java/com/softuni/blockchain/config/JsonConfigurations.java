package com.softuni.blockchain.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JsonConfigurations {

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        builder.featuresToEnable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        builder.failOnUnknownProperties(false);
        return builder;
    }
}