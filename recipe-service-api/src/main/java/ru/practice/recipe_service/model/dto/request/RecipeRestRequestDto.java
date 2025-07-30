package ru.practice.recipe_service.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import ru.practice.recipe_service.model.entity.IngredientEntity;

import java.util.List;

public record RecipeRestRequestDto(
        @NotBlank String name,
        int mins4Prep,
        int mins4Cook,
        int additionalMins,
        int servings,
        @NotBlank List<IngredientEntity> ingredients,
        @NotBlank String direction,
        @NotBlank String description
) {
}
