package ru.practice.shared.dto.ingredient;

import java.util.function.Function;

final class FallbackExtractor implements Function<String, IngredientDto> {

    @Override
    public IngredientDto apply(String text) {
        var quantity = "";
        var unit = "";
        var name = text;

        if (text.matches("^\\d+(?:\\.\\d+)?.*")) {
            String[] parts = text.split("\\s+", 2);
            if (parts.length == 2) {
                quantity = parts[0];
                name = parts[1].trim();
            }
        }
        return IngredientDto.of(name, quantity, unit);
    }
}
