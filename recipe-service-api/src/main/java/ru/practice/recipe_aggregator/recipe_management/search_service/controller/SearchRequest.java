package ru.practice.recipe_aggregator.recipe_management.search_service.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty
    String name;
    @JsonProperty
    Set<String> ingredientNames;
    @JsonProperty
    final int cookingTime;
    @JsonProperty
    final String cookingTimeOperator;
    @JsonProperty
    final int totalTime;
    @JsonProperty
    final String totalTimeOperator;
    @JsonProperty
    final int preparationTime;
    @JsonProperty
    final String preparationTimeOperator;
    @JsonProperty
    final int servings;
    @JsonProperty
    final String servingsOperator;
}
