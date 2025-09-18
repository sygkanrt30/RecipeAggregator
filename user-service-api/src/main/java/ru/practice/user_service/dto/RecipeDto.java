package ru.practice.user_service.dto;

import java.util.List;

public record RecipeDto(
        Long id,
        String name,
        int mins4Prep,
        int mins4Cook,
        int additionalMins,
        int totalMins,
        int servings,
        List<IngredientDto> ingredients,
        String direction,
        String description) {
}
