package ru.practice.recipe_aggregator.recipe_management.model.dto.kafka;

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
    @Override
    public String toString() {
        return String.format(
                "Recipe{name=%s; mins4Prep=%s; mins4Cook=%s; additionalMins=%s;" +
                        " totalMins=%s; servings=%s; ingredients=%s; direction=%s; description=%s}",
                name, mins4Prep, mins4Cook, additionalMins, totalMins, servings, ingredients,
                direction.substring(0, Math.min(50, direction.length())),
                description.substring(0, Math.min(50, description.length()))
        );
    }
}
