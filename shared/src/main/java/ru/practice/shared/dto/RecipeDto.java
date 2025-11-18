package ru.practice.shared.dto;

import lombok.Builder;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Builder
public record RecipeDto(
        UUID id,
        String name,
        Duration timeForPreparing,
        Duration timeForCooking,
        Duration additionalTime,
        Duration totalTime,
        int servings,
        List<IngredientDto> ingredients,
        String direction,
        String description
) {
    @Override
    public String toString() {
        return String.format(
                "Recipe{id=%s, name=%s; timeForPreparing=%s; timeForCooking=%s; additionalTime=%s;" +
                        " totalTime=%s; servings=%s; ingredients=%s; direction=%s; description=%s}",
                id, name, timeForPreparing, timeForCooking, additionalTime, totalTime, servings, ingredients,
                direction.substring(0, Math.min(50, direction.length())),
                description.substring(0, Math.min(50, description.length()))
        );
    }
}
