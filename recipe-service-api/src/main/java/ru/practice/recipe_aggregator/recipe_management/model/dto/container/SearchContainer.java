package ru.practice.recipe_aggregator.recipe_management.model.dto.container;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Data
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchContainer {
    final String name;
    final List<String> ingredientsName;
    Integer maxMins4Cook;
    Integer maxTotalMins;
    Integer maxMins4Prep;
    Integer minServings;
    Integer maxServings;
}
