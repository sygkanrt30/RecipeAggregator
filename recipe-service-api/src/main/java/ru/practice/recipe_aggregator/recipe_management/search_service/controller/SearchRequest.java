package ru.practice.recipe_aggregator.recipe_management.search_service.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class SearchRequest {
    String name;
    Set<String> ingredientNames;
    final int cookingTime;
    final String cookingTimeOperator;
    final int totalTime;
    final String totalTimeOperator;
    final int preparationTime;
    final String preparationTimeOperator;
    final int servings;
    final String servingsOperator;
}
