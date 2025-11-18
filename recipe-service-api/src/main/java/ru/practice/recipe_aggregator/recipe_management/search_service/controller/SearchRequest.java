package ru.practice.recipe_aggregator.recipe_management.search_service.controller;

import java.util.Set;

public record SearchRequest(
        String name,
        Set<String> ingredientNames,
        int cookingTime,
        String cookingTimeOperator,
        int totalTime,
        String totalTimeOperator,
        int preparationTime,
        String preparationTimeOperator,
        int servings,
        String servingsOperator
) {
}
