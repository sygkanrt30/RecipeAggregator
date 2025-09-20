package ru.practice.parser_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.Map;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Recipe(
        String name,
        int mins4Prep,
        int mins4Cook,
        int additionalMins,
        int totalMins,
        int servings,
        Map<String, String> ingredients,
        String direction,
        String description
) {
}
