package ru.practice.parser_service.service.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum NameOfUrlCaches {
    VISITED_URLS("visited_urls"),
    PARSED_RECIPE_URLS("parsed_recipe_urls");

    private final String value;
}
