package ru.practice.shared.dto.ingredient;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.regex.Pattern;

@Getter
@Accessors(fluent = true)
enum IngredientPattern {
    QUANTITY_UNIT("^(\\d+(?:\\.\\d+)?)\\s*([a-zA-Z]+)\\s+(.+)$"),
    QUANTITY_ONLY("^(\\d+(?:\\.\\d+)?)\\s+(.+)$"),
    RANGE_QUANTITY("^(\\d+(?:\\.\\d+)?)\\s*-\\s*(\\d+(?:\\.\\d+)?)\\s*([a-zA-Z]+)?\\s*(.+)$"),
    FRACTION("^(\\d+)\\s*(\\d+/\\d+)\\s*([a-zA-Z]+)?\\s*(.+)$"),
    PARENTHESES("^(.+?)\\s*\\(([^)]+)\\)$");

    private final Pattern pattern;

    IngredientPattern(String regex) {
        this.pattern = Pattern.compile(regex);
    }
}