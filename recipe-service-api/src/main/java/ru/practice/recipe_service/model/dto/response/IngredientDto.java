package ru.practice.recipe_service.model.dto.response;

public record IngredientDto(
        String name,
        String quantity,
        String unit
) {
}
