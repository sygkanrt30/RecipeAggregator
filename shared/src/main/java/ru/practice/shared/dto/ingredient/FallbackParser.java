package ru.practice.shared.dto.ingredient;

import static ru.practice.shared.dto.ingredient.DefaultValue.QUANTITY;

final class FallbackParser {
    static IngredientDto parse(String text) {
        String quantity = "";
        String unit = "";
        String name = text;

        if (text.matches("^\\d+(?:\\.\\d+)?.*")) {
            String[] parts = text.split("\\s+", 2);
            if (parts.length == 2) {
                quantity = parts[0];
                name = parts[1].trim();
            }
        }

        if (quantity == null || quantity.trim().isEmpty()) {
            quantity = QUANTITY.defaultValue();
        }

        return new IngredientDto(name, quantity, unit);
    }
}
