package ru.practice.shared.dto.ingredient;

final class QuantityUnitParser {
    static IngredientDto parse(String text) {
        var matcher = IngredientPattern.QUANTITY_UNIT.pattern().matcher(text);
        if (matcher.matches()) {
            String quantity = matcher.group(1);
            String unit = matcher.group(2);
            String name = matcher.group(3).trim();
            return IngredientDto.of(name, quantity, unit);
        }
        return null;
    }
}
