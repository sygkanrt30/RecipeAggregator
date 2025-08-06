package ru.practice.parser_service.service.parsers.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum CssQueryOfRecipesParts {
    NAME("h1.article-heading.text-headline-400"),

    DESCRIPTION("p.article-subheading.text-utility-300"),

    INGREDIENT_QUANTITY("span[data-ingredient-quantity=true]"),

    INGREDIENTS_UNIT("span[data-ingredient-unit=true]"),

    INGREDIENTS_NAME("span[data-ingredient-name=true]"),

    INGREDIENTS_NODE("li.mm-recipes-structured-ingredients__list-item"),

    RECIPE_DETAILS_ITEM("div.mm-recipes-details__item"),

    RECIPE_DETAILS_LABEL("div.mm-recipes-details__label"),

    RECIPE_DETAILS_VALUE("div.mm-recipes-details__value"),

    DIRECTIONS("li p.mntl-sc-block-html");

    private final String cssQuery;
}
