package ru.practice.shared.dto.ingredient;

public record IngredientDto(
        String name,
        String quantity,
        String unit) {

    private static final String QUANTITY_DEFAULT = "1";

    public static IngredientDto of(String name, String quantity, String unit) {
        if (quantity == null || quantity.trim().isEmpty()) {
            quantity = QUANTITY_DEFAULT;
        }
        return new IngredientDto(name, quantity, unit);
    }

    public static IngredientDto of(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new IngredientDto("", QUANTITY_DEFAULT, "");
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
