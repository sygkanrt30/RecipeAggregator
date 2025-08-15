package ru.practice.recipe_service.model.dto.response;

import java.util.List;

public record RecipeResponseDto(
        Long id,
        String name,
        int mins4Prep,
        int mins4Cook,
        int additionalMins,
        int totalMins,
        int servings,
        List<IngredientDto> ingredients,
        String direction,
        String description
) {
}
