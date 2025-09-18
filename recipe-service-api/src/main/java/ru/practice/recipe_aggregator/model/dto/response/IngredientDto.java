package ru.practice.recipe_aggregator.model.dto.response;

public record IngredientDto(
        String name,
        String quantity,
        String unit
) {
}
