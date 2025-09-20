package ru.practice.recipe_aggregator.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi(@Value("${application.rest.api.prefix}/v1") String prefix,
                                    @Value("${spring.application.name}") String group) {
        return GroupedOpenApi.builder()
                .group(group)
                .pathsToMatch(String.format("%s/**", prefix))
                .build();
    }
}
