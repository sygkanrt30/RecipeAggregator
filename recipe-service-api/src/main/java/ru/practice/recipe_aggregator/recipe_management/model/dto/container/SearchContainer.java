package ru.practice.recipe_aggregator.recipe_management.model.dto.container;

import lombok.Builder;

import java.util.Set;

@Builder
public record SearchContainer(
        String name,
        Set<String> ingredientNames,
        FilterCondition cookingTimeCondition,
        FilterCondition totalTimeCondition,
        FilterCondition preparationTimeCondition,
        FilterCondition servingsCondition
) {
}