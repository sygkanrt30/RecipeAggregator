package ru.practice.shared.dto.ingredient;

import java.util.regex.Matcher;

import static ru.practice.shared.dto.ingredient.DefaultValue.QUANTITY;

final class RangeQuantityParser {
    static IngredientDto parse(String text) {
        Matcher matcher = IngredientPattern.RANGE_QUANTITY.pattern().matcher(text);
        if (matcher.matches()) {
            String quantity = matcher.group(1) + "-" + matcher.group(2);
            String unit = matcher.group(3) != null ? matcher.group(3) : "";
            String name = matcher.group(4).trim();

            if (quantity.trim().isEmpty()) {
                quantity = QUANTITY.defaultValue();
            }

            return new IngredientDto(name, quantity, unit);
        }
        return null;
    }
}
