package ru.practice.parser_service.config;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum NameOfCaches {
    VISITED_URLS_CACHE("visited_urls"),
    RECIPES_CACHE("recipes"),
    PARSED_RECIPE_URLS_CACHE("parsed_recipe_urls");

    private final String name;
}
