package ru.practice.parser_service.service.parsers.recipe.jsonld;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
enum TimeLabel {

    COOK_TIME("cookTime"),
    PREP_TIME("prepTime"),
    TOTAL_TIME("totalTime");

    private final String label;
}
