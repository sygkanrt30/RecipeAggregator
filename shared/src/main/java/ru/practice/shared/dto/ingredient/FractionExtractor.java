package ru.practice.shared.dto.ingredient;

import java.util.function.Function;

final class FractionExtractor implements Function<String, IngredientDto> {

    @Override
    public IngredientDto apply(String text) {
        var matcher = IngredientPattern.FRACTION.pattern().matcher(text);
        if (matcher.matches()) {
            String quantity = matcher.group(1) + " " + matcher.group(2);
            String unit = matcher.group(3) != null ? matcher.group(3) : "";
            String name = matcher.group(4).trim();
            return IngredientDto.of(name, quantity, unit);
        }
        return null;
    }
}
