package ru.practice.recipe_aggregator.recipe_management.model.dto.container;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Builder
@Data
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchContainer {
    final String name;
    final Set<String> ingredientNames;
    Integer maxMinsForCooking;
    Integer maxTotalMinutes;
    Integer maxMinsForPreparing;
    Integer minServings;
    Integer maxServings;
}
