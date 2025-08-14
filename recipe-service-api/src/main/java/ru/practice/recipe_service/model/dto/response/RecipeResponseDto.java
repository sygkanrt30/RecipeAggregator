package ru.practice.recipe_service.model.dto.response;

import ru.practice.recipe_service.model.entity.IngredientEntity;

import java.util.List;

public record RecipeResponseDto(
        Long id,
        String name,
        int mins4Prep,
        int mins4Cook,
        int additionalMins,
        int totalMins,
        int servings,
        List<IngredientEntity> ingredients,
        String direction,
        String description
) {
}
