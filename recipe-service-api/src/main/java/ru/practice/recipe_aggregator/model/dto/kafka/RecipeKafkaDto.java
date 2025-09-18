package ru.practice.recipe_aggregator.model.dto.kafka;

import java.util.Map;

public record RecipeKafkaDto(
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
