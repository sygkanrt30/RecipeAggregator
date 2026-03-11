package ru.practice.shared.dto.ingredient;

import java.util.function.Function;

final class QuantityUnitExtractor implements Function<String, IngredientDto> {

    @Override
    public IngredientDto apply(String text) {
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
