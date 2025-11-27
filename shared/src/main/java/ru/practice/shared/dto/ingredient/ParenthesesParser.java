package ru.practice.shared.dto.ingredient;

import java.util.regex.Matcher;

import static ru.practice.shared.dto.ingredient.DefaultValue.QUANTITY;

final class ParenthesesParser {
    static IngredientDto parse(String text) {
        Matcher matcher = IngredientPattern.PARENTHESES.pattern().matcher(text);
        if (matcher.matches()) {
            String mainPart = matcher.group(1).trim();
            String parenPart = matcher.group(2).trim();

            Matcher mainMatcher = IngredientPattern.QUANTITY_UNIT.pattern().matcher(mainPart);
            if (mainMatcher.matches()) {
                String quantity = mainMatcher.group(1);
                String unit = mainMatcher.group(2);
                String name = mainMatcher.group(3).trim() + " (" + parenPart + ")";

                if (quantity == null || quantity.trim().isEmpty()) {
                    quantity = QUANTITY.defaultValue();
                }

                return new IngredientDto(name, quantity, unit);
            }
        }
        return null;
    }
}
