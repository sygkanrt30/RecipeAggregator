package ru.practice.parser_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.Duration;
import java.util.Map;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Recipe(
        String name,
        Duration timeForPreparing,
        Duration timeForCooking,
        Duration additionalTime,
        Duration totalTime,
        int servings,
        Map<String, String> ingredients,
        String direction,
        String description
) {
}
