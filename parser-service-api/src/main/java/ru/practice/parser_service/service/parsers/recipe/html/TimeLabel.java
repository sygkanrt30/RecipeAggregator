package ru.practice.parser_service.service.parsers.recipe.html;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
enum TimeLabel {

    COOK_TIME("cook time"),
    PREP_TIME("prep time"),
    TOTAL_TIME("total time");

    private final String label;
}
