package ru.practice.recipe_aggregator.recipe_management.model.dto.response;

public record IngredientDto(
        String name,
        String quantity,
        String unit
) {
}
