package ru.practice.shared.dto.ingredient;

final class RangeQuantityParser {
    static IngredientDto parse(String text) {
        var matcher = IngredientPattern.RANGE_QUANTITY.pattern().matcher(text);
        if (matcher.matches()) {
            String quantity = matcher.group(1) + "-" + matcher.group(2);
            String unit = matcher.group(3) != null ? matcher.group(3) : "";
            String name = matcher.group(4).trim();
            return IngredientDto.of(name, quantity, unit);
        }
        return null;
    }
}
