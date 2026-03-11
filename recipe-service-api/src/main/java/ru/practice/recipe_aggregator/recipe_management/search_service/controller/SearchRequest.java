package ru.practice.recipe_aggregator.recipe_management.search_service.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class SearchRequest {
    @JsonProperty
    String name;
    @JsonProperty
    Set<String> ingredientNames;
    @JsonProperty
    int cookingTime;
    @JsonProperty
    String cookingTimeOperator;
    @JsonProperty
    int totalTime;
    @JsonProperty
    String totalTimeOperator;
    @JsonProperty
    int preparationTime;
    @JsonProperty
    String preparationTimeOperator;
    @JsonProperty
    int servings;
    @JsonProperty
    String servingsOperator;
}
