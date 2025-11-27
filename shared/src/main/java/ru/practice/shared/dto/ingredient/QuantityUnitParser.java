package ru.practice.shared.dto.ingredient;

import java.util.regex.Matcher;

import static ru.practice.shared.dto.ingredient.DefaultValue.QUANTITY;

final class QuantityUnitParser {
    static IngredientDto parse(String text) {
        Matcher matcher = IngredientPattern.QUANTITY_UNIT.pattern().matcher(text);
        if (matcher.matches()) {
            String quantity = matcher.group(1);
            String unit = matcher.group(2);
            String name = matcher.group(3).trim();

            if (quantity == null || quantity.trim().isEmpty()) {
                quantity = QUANTITY.defaultValue();
            }

            return new IngredientDto(name, quantity, unit);
        }
        return null;
    }
}
