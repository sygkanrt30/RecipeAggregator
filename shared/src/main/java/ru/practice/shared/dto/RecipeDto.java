package ru.practice.shared.dto;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public record RecipeDto(
        UUID id,
        String name,
        Duration minsForPreparing,
        Duration minsForCooking,
        Duration additionalMins,
        Duration totalMins,
        int servings,
        List<IngredientDto> ingredients,
        String direction,
        String description
) {
    @Override
    public String toString() {
        return String.format(
                "Recipe{id=%s, name=%s; minsForPreparing=%s; minsForCooking=%s; additionalMins=%s;" +
                        " totalMins=%s; servings=%s; ingredients=%s; direction=%s; description=%s}",
                id, name, minsForPreparing, minsForCooking, additionalMins, totalMins, servings, ingredients,
                direction.substring(0, Math.min(50, direction.length())),
                description.substring(0, Math.min(50, description.length()))
        );
    }
}
