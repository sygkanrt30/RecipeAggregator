package ru.practice.recipe_aggregator.model.dto.response;

import javax.validation.constraints.NotBlank;
import java.util.List;

public record RecipeResponseDto(
        @NotBlank String name,
        int mins4Prep,
        int mins4Cook,
        int additionalMins,
        int totalMins,
        int servings,
        @NotBlank List<IngredientDto> ingredients,
        @NotBlank String direction,
        @NotBlank String description
) {
}
