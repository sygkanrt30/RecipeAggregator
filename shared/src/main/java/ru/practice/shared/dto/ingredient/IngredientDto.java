package ru.practice.shared.dto.ingredient;

import static ru.practice.shared.dto.ingredient.DefaultValue.QUANTITY;

public record IngredientDto(
        String name,
        String quantity,
        String unit) {

    public static IngredientDto of(String name, String quantity, String unit) {
        if (quantity == null || quantity.isEmpty()) {
            quantity = QUANTITY.defaultValue();
        }
        return new IngredientDto(name, quantity, unit);
    }

    public static IngredientDto of(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new IngredientDto("", QUANTITY.defaultValue(), "");
        }

        var cleanedText = text.trim();

        IngredientDto result = RangeQuantityParser.parse(cleanedText);
        if (result != null)
            return result;

        result = FractionParser.parse(cleanedText);
        if (result != null)
            return result;

        result = QuantityUnitParser.parse(cleanedText);
        if (result != null)
            return result;

        result = QuantityOnlyParser.parse(cleanedText);
        if (result != null)
            return result;

        result = ParenthesesParser.parse(cleanedText);
        if (result != null)
            return result;

        return FallbackParser.parse(cleanedText);
    }
}
