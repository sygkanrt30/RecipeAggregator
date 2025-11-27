package ru.practice.parser_service.service.parsers.recipe.jsonld;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
enum FieldName {
    TYPE("@type"),
    NAME("name"),
    DESCRIPTION("description"),
    SERVINGS("recipeYield"),
    INGREDIENTS("recipeIngredient"),
    INSTRUCTIONS("recipeInstructions"),
    TEXT("text"),
    URL("url");

    private final String value;
}
